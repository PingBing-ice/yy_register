package com.yy.order.service.impl;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONObject;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yy.all.client.HospFeignClient;
import com.yy.all.client.UserFeignClient;
import com.yy.order.mapper.OrderInfoMapper;
import com.yy.order.service.IOrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yy.order.service.IPaymentInfoService;
import com.yy.order.service.WxService;
import com.yy.order.utils.HttpRequestHelper;
import com.yy.rabbitmq.MqConst;
import com.yy.rabbitmq.RabbitService;
import com.yy.util.exception.RException;
import com.yy.yygh.enums.OrderStatusEnum;
import com.yy.yygh.model.order.OrderInfo;
import com.yy.yygh.model.order.PaymentInfo;
import com.yy.yygh.model.user.Patient;
import com.yy.yygh.vo.hosp.ScheduleOrderVo;
import com.yy.yygh.vo.msm.MsmVo;
import com.yy.yygh.vo.order.OrderCountQueryVo;
import com.yy.yygh.vo.order.OrderCountVo;
import com.yy.yygh.vo.order.OrderMqVo;
import com.yy.yygh.vo.order.OrderQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author ice
 * @since 2022-08-13
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private HospFeignClient hospFeignClient;

    @Autowired
    private IPaymentInfoService paymentInfoService;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private WxService wxService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    // 获取订单统计数据
    @Override
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        List<OrderCountVo> countVoList =baseMapper.selectOrderCount(orderCountQueryVo);
        List<String> reserveDateList = countVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());
        List<Integer> countList = countVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("dateList", reserveDateList);
        map.put("countList", countList);
        return map;
    }

    // 预约提醒
    @Override
    public void patientTips(String time) {
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        wrapper.eq("reserve_date", format);
        wrapper.ne("order_status", OrderStatusEnum.CANCLE.getStatus());
        List<OrderInfo> orderInfos = baseMapper.selectList(wrapper);
        if (orderInfos.size()==0) return;
        orderInfos.forEach(orderInfo -> {
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            msmVo.setTemplateCode("预约提醒"+time);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
        });
    }

    // 取消预约
    @Override
    public boolean cancelOrder(String orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        DateTime dateTime = new DateTime(new Date());
        boolean before = dateTime.isBefore(orderInfo.getQuitTime());
        if (before) {
            throw new RException("时间已过,无法取消");
        }
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("hoscode", orderInfo.getHoscode());
        reqMap.put("hosRecordId", orderInfo.getHosRecordId());
        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
        reqMap.put("sign", "");

        JSONObject result = HttpRequestHelper.sendRequest(reqMap, "http://localhost:9998/order/updateCancelStatus");
        if (result.getIntValue("code") != 200) {
            throw new RException("无法取消");
        }
        if (orderInfo.getOrderStatus() == 2) {
            // 微信退款
            boolean isRefund = wxService.refund(orderId);
            if (!isRefund) {
                throw new RException("无法取消");
            }
        }

        // 更改订单状态
        orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
        baseMapper.updateById(orderInfo);
        //更新支付记录状态。
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);

        PaymentInfo paymentInfo =paymentInfoService.getOne(queryWrapper);
        paymentInfo.setPaymentStatus(-1); //退款
        paymentInfo.setUpdateTime(new Date());
        paymentInfoService.updateById(paymentInfo);
        //发送mq信息更新预约数 我们与下单成功更新预约数使用相同的mq信息，不设置可预约数与剩余预约数，接收端可预约数减1即可
        OrderMqVo orderMqVo = new OrderMqVo();
        orderMqVo.setScheduleId(orderInfo.getScheduleId());
        //短信提示
        MsmVo msmVo = new MsmVo();
        msmVo.setPhone(orderInfo.getPatientPhone());
        msmVo.setTemplateCode("退款成功");
        orderMqVo.setMsmVo(msmVo);
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
        return true;
    }

    //实现列表
    //（条件查询带分页）
    @Override
    public IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {
        //orderQueryVo获取条件值
        String name = orderQueryVo.getKeyword(); //医院名称
        Long patientId = orderQueryVo.getPatientId(); //就诊人名称
        String orderStatus = orderQueryVo.getOrderStatus(); //订单状态
        String reserveDate = orderQueryVo.getReserveDate();//安排时间
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();
        String userId = orderQueryVo.getUserId();
        //对条件值进行非空判断
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(userId)) {
            wrapper.eq("user_id", userId);
        }
        if (!StringUtils.isEmpty(name)) {
            wrapper.like("hosname", name);
        }
        if (!StringUtils.isEmpty(patientId)) {
            wrapper.eq("patient_id", patientId);
        }
        if (!StringUtils.isEmpty(orderStatus)) {
            wrapper.eq("order_status", orderStatus);
        }
        if (!StringUtils.isEmpty(reserveDate)) {
            wrapper.ge("reserve_date", reserveDate);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time", createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time", createTimeEnd);
        }
        //调用mapper的方法
        IPage<OrderInfo> pages = baseMapper.selectPage(pageParam, wrapper);
        //编号变成对应值封装
        pages.getRecords().forEach(this::packOrderInfo);
        return pages;
    }

    private void packOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
    }

    /**
     * @param scheduleId 排班id
     * @param patientId  就诊人id
     * @return
     */
    @Override
    public String saveOrder(String scheduleId, String patientId) {
        // 获取就诊人信息
        Patient patient = userFeignClient.getPatientClientById(patientId);
        // 获取排班信息
        ScheduleOrderVo scheduleOrderVo = hospFeignClient.getFeignScheduleById(scheduleId);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode", scheduleOrderVo.getHoscode());
        paramMap.put("depcode", scheduleOrderVo.getDepcode());
        paramMap.put("hosScheduleId", scheduleOrderVo.getHosScheduleId());
        paramMap.put("reserveDate", new DateTime(scheduleOrderVo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", scheduleOrderVo.getReserveTime());
        paramMap.put("amount", scheduleOrderVo.getAmount()); //挂号费用
        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType", patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex", patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone", patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode", patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode", patient.getDistrictCode());
        paramMap.put("address", patient.getAddress());
        //联系人
        paramMap.put("contactsName", patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo", patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone", patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
        //String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
        paramMap.put("sign", "");
        // 调用第三方医院系统,确认是否挂号
        JSONObject result = HttpRequestHelper.sendRequest(paramMap, "http://localhost:9998/order/submitOrder");
        if (result.getIntValue("code") == 200) {
            // 3.2 如果返回成功，得到返回其他数据
            JSONObject jsonObject = result.getJSONObject("data");
            //预约记录唯一标识（医院预约记录主键）
            String hosRecordId = jsonObject.getString("hosRecordId");
            //预约序号
            Integer number = jsonObject.getInteger("number");
            ;
            //取号时间
            String fetchTime = jsonObject.getString("fetchTime");
            ;
            //取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");

            //4 如果医院接口返回成功，添加上面三部分数据到数据库
            OrderInfo orderInfo = new OrderInfo();
            //设置添加数据--排班数据
            BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
            //设置添加数据--就诊人数据
            //订单号
            String outTradeNo = System.currentTimeMillis() + "" + new Random().nextInt(100);
            orderInfo.setOutTradeNo(outTradeNo);
            orderInfo.setScheduleId(scheduleOrderVo.getHosScheduleId());
            orderInfo.setUserId(patient.getUserId());
            orderInfo.setPatientId(patientId);
            orderInfo.setPatientName(patient.getName());
            orderInfo.setPatientPhone(patient.getPhone());
            orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());

            //设置添加数据--医院接口返回数据
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);

            //调用方法添加
            int insert = baseMapper.insert(orderInfo);


            if (insert != 1) {
                throw new RException("保存失败");
            }

            //排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");

            //排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setAvailableNumber(availableNumber);
            orderMqVo.setReservedNumber(reservedNumber);
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(patient.getPhone());
            msmVo.setParam(null);

            msmVo.setTemplateCode("预约成功");
            orderMqVo.setMsmVo(msmVo);

            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);

            //7 返回订单号
            return orderInfo.getId();

        } else { //挂号失败
            throw new RException("挂号失败");
        }

    }
}
