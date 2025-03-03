package me.pgthinker.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.pgthinker.common.BaseResponse;
import me.pgthinker.common.ResultUtils;
import me.pgthinker.model.vo.UserVO;
import me.pgthinker.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Project: me.pgthinker.controller
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/1 17:10
 * @Description:
 */
@RestController("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('admin')")
    public BaseResponse<Page<UserVO>> list(UserVO userVO) {
        return ResultUtils.success(userService.list(userVO));
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('admin')")
    public BaseResponse<Long> add(@RequestBody UserVO userVO) {
        return ResultUtils.success(userService.addUser(userVO));
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyAuthority('admin')")
    public BaseResponse<Long> delete(@RequestBody UserVO userVO) throws Exception {
        return ResultUtils.success(userService.deleteUser(userVO.getId()));
    }
}
