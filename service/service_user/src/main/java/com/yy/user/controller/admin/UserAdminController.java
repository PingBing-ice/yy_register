package com.yy.user.controller.admin;

import com.yy.user.service.IUserInfoService;
import com.yy.util.result.R;
import com.yy.yygh.vo.user.UserInfoQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 后台用户管理
 *
 * @author ice
 * @date 2022/8/12 17:43
 */
@RestController
@RequestMapping("/user/admin")
public class UserAdminController {
    @Resource
    private IUserInfoService userInfoService;

    //用户列表（条件查询带分页）
    @GetMapping("/selectListByPage/{page}/{limit}")
    public R selectListByPage(@PathVariable("page") int page, @PathVariable("limit") int limit, UserInfoQueryVo userInfoQueryVo) {
        Map<String, Object>  map= userInfoService.selectPageList(page, limit, userInfoQueryVo);
        System.out.println(map);
        return R.ok().data(map);
    }

    @ApiOperation(value = "锁定")
    @GetMapping("lock/{userId}/{status}")
    public R lock(
            @PathVariable("userId") String userId,
            @PathVariable("status") Integer status){
        userInfoService.lock(userId, status);
        return R.ok();
    }

    //用户详情
    @GetMapping("show/{userId}")
    public R show(@PathVariable String userId) {
        Map<String,Object> map = userInfoService.show(userId);
        return R.ok().data(map);
    }

    //认证审批
    @GetMapping("approval/{userId}/{authStatus}")
    public R approval(@PathVariable("userId") String userId,@PathVariable("authStatus") Integer authStatus) {
        userInfoService.approval(userId,authStatus);
        return R.ok();
    }
}
