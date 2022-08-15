package com.yy.hosp.controller;

import com.yy.hosp.service.IHospitalSetService;
import com.yy.util.encryption.MD5;

import com.yy.util.result.R;
import com.yy.yygh.model.hosp.HospitalSet;
import com.yy.yygh.vo.hosp.HospitalSetQueryVo;
import com.yy.yygh.vo.user.LoginUser;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * <p>
 * 医院设置表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-07-29
 */
@Api(tags = "预约设置接口")
@RestController
@RequestMapping("/hosp/admin")
@Log4j2
public class HospitalSetController {
    @Resource
    private IHospitalSetService hospitalSetService;


    @PostMapping("/login")
    public R login(@RequestBody LoginUser loginUser) {
        String username = loginUser.getUsername();
        String password = loginUser.getPassword();
        log.info(username);
        log.info(password);
        if (username.equals("admin") && password.equals("111111")) {
            return R.ok();
        }
        return R.error();
    }

    @GetMapping("/info")
    public R info(@RequestParam(required = false) String token) {
        log.info(token);
        return R.ok().data("roles","[admin]").data("introduction","I am a super administrator")
                .data("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif")
                .data("name","Super Admin");
    }

    @GetMapping("/getList")
    public R getList() {
        List<HospitalSet> list = hospitalSetService.list();
        return R.ok().data("list", list);
    }

    /**
     * 更具查询条件查询消息
     *
     * @param current            当前页
     * @param limit              总页数
     * @param hospitalSetQueryVo 查询条件
     * @return 返回数据
     */
    @PostMapping("/getListPage/{current}/{limit}")
    public R pageList(@PathVariable("current") long current,
                      @PathVariable("limit") long limit,
                      @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        Map<String, Object> map = hospitalSetService.selectQueryByPage(current, limit, hospitalSetQueryVo);

        return R.ok().data(map);

    }

    /**
     * 新增医院设置
     */
    @PostMapping("/save")
    public R save(@RequestBody HospitalSet hospitalSet) {
        if (hospitalSet == null) {
            return R.error().message("数据为空");
        }
        //设置状态 1 使用 0 不能使用
        hospitalSet.setStatus(1);
        //签名秘钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.getMD5(System.currentTimeMillis() + "" + random.nextInt(1000)));
        boolean save = hospitalSetService.save(hospitalSet);
        if (save) {
            return R.ok();
        }
        return R.error().message("保存失败");
    }

    //批量删除医院设置
    @DeleteMapping("batchRemove")
    public R batchRemoveHospitalSet(@RequestBody List<Long> idList) {
        if (idList.size() == 0) {
            return R.error().message("传入数据错误");
        }
        boolean removeByIds = hospitalSetService.removeByIds(idList);
        if (!removeByIds) {
            return R.error().message("删除错误");
        }
        return R.ok();
    }
    //根据id删除医院设置
    @DeleteMapping("removeById/{id}")
    public R RemoveHospitalSetById(@PathVariable("id") String id) {
        boolean removeByIds = hospitalSetService.removeById(id);
        if (!removeByIds) {
            return R.error().message("删除错误");
        }
        return R.ok();
    }

    // 修改类容
    @PostMapping("/UpdateEdit")
    public R updateEdit(@RequestBody HospitalSet hospitalSet) {

        if (hospitalSet == null) {
            return R.error();
        }
        log.info(hospitalSet.getId());
        boolean update = hospitalSetService.updateById(hospitalSet);
        if (!update) {
            return R.error();
        }
        return R.ok();
    }

    // 医院设置锁定和解锁
    @GetMapping("lockHospitalSet/{id}/{status}")
    public R lockHospitalSet(@PathVariable Long id,
                             @PathVariable Integer status) {

        if (id == null) {
            return R.error().message("传入的数据错误");
        }
        if (status == null) {
            return R.error().message("传入的数据错误");
        }
        //根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //设置状态
        hospitalSet.setStatus(status);
        //调用方法
        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }
}
