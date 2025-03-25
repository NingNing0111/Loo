package me.pgthinker.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import me.pgthinker.common.BaseResponse;
import me.pgthinker.common.ResultUtils;
import me.pgthinker.model.vo.AuthVO;
import me.pgthinker.model.vo.LoginUserVO;
import me.pgthinker.service.AuthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Project: me.pgthinker.controller
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/25 01:07
 * @Description:
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth")
    @PermitAll
    public BaseResponse<LoginUserVO> auth(@RequestBody AuthVO authVO) {
        LoginUserVO vo = authService.auth(authVO);
        return ResultUtils.success(vo);
    }

    @GetMapping("/userInfo")
    @PreAuthorize("hasAnyAuthority('admin')")
    public BaseResponse<LoginUserVO> userInfo(){
        return ResultUtils.success(authService.userInfo());
    }
}
