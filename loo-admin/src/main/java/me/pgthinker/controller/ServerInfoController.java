package me.pgthinker.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.pgthinker.common.BaseResponse;
import me.pgthinker.common.ResultUtils;
import me.pgthinker.model.vo.ServerInfoVO;
import me.pgthinker.model.vo.SimpleServerVO;
import me.pgthinker.service.ServerInfoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public BaseResponse<List<ServerInfoVO>> serverList(){
        List<ServerInfoVO> vo = serverInfoService.list();
        return ResultUtils.success(vo);
    }

    @GetMapping("/historyList")
    @PreAuthorize("hasAnyAuthority('admin')")
    public BaseResponse<Page<ServerInfoVO>> historyList(ServerInfoVO serverInfoVO){
        return ResultUtils.success(serverInfoService.historyList(serverInfoVO));
    }

    @GetMapping("/simpleList")
    @PreAuthorize("hasAnyAuthority('admin')")
    public BaseResponse<List<SimpleServerVO>> simpleList(){
        return ResultUtils.success(serverInfoService.simpleList());
    }
}
