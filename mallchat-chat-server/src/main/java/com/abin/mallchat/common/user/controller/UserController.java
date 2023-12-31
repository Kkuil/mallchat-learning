package com.abin.mallchat.common.user.controller;

import com.abin.mallchat.common.common.domain.vo.resp.ApiResult;
import com.abin.mallchat.common.common.utils.AssertUtil;
import com.abin.mallchat.common.common.utils.RequestHolder;
import com.abin.mallchat.common.user.domain.enums.RoleEnum;
import com.abin.mallchat.common.user.domain.vo.req.BlackReq;
import com.abin.mallchat.common.user.domain.vo.req.ModifyNameReq;
import com.abin.mallchat.common.user.domain.vo.req.WearingBadgeReq;
import com.abin.mallchat.common.user.domain.vo.resp.BadgeResp;
import com.abin.mallchat.common.user.domain.vo.resp.UserInfoResp;
import com.abin.mallchat.common.user.service.IRoleService;
import com.abin.mallchat.common.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author <a href="https://github.com/zongzibinbin">abin</a>
 * @Date 2023/09/17
 */
@RestController
@RequestMapping("/capi/user")
@Api(tags = "用户相关接口")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private IRoleService iRoleService;

    @GetMapping("/userInfo")
    @ApiOperation("获取用户个人信息")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PutMapping("/name")
    @ApiOperation("修改用户名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyName(RequestHolder.get().getUid(), req.getName());
        return ApiResult.success();
    }

    @GetMapping("/badges")
    @ApiOperation("可选徽章预览")
    public ApiResult<List<BadgeResp>> badges() {
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    @PutMapping("/badge")
    @ApiOperation("佩戴徽章")
    public ApiResult<Void> wearingBadge(@Valid @RequestBody WearingBadgeReq req) {
        Long uid = RequestHolder.get().getUid();
        Long itemId = req.getItemId();
        userService.wearingBadge(uid, itemId);
        // 幂等设计，不管请求（点击佩戴）多少次，都返回空数据
        return ApiResult.success();
    }

    @PutMapping("/black")
    @ApiOperation("拉黑用户")
    public ApiResult<Void> black(@Valid @RequestBody BlackReq req) {
        Long uid = RequestHolder.get().getUid();
        boolean hasPower = iRoleService.hasPower(uid, RoleEnum.ADMIN);
        // 断定他为真，如果不为空返回报错信息
        AssertUtil.isTrue(hasPower, "抹茶管理员没权限");
        userService.black(req);
        return ApiResult.success();
    }
}
