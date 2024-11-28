package me.pgthinker.controller;

import lombok.RequiredArgsConstructor;
import me.pgthinker.common.BaseResponse;
import me.pgthinker.common.ResultUtils;
import me.pgthinker.model.vo.ServerInfoVO;
import me.pgthinker.service.ServerInfoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Project: me.pgthinker.controller
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/25 16:51
 * @Description:
 */
@RestController
@RequestMapping("/server")
@RequiredArgsConstructor
public class ServerInfoController {
    private final ServerInfoService serverInfoService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('admin')")
    public BaseResponse<List<ServerInfoVO>> list(){
        List<ServerInfoVO> vo = serverInfoService.list();
        return ResultUtils.success(vo);
    }
}
