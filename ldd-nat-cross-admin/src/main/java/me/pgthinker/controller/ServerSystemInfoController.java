package me.pgthinker.controller;

import lombok.RequiredArgsConstructor;
import me.pgthinker.common.BaseResponse;
import me.pgthinker.common.ResultUtils;
import me.pgthinker.model.entity.ServerSystemInfoDO;
import me.pgthinker.model.vo.ServerSystemReqVO;
import me.pgthinker.service.ServerSystemInfoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Project: me.pgthinker.controller
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/25 17:41
 * @Description:
 */
@RestController
@RequestMapping("/serverSystem")
@RequiredArgsConstructor
public class ServerSystemInfoController {

    private final ServerSystemInfoService serverSystemInfoService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('admin')")
    public BaseResponse<List<ServerSystemInfoDO>> serverSystemInfoList(ServerSystemReqVO reqVO) {
        List<ServerSystemInfoDO> resp = serverSystemInfoService.list(reqVO);
        return ResultUtils.success(resp);
    }
}
