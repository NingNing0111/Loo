package me.pgthinker.controller;

import lombok.RequiredArgsConstructor;
import me.pgthinker.common.BaseResponse;
import me.pgthinker.common.ResultUtils;
import me.pgthinker.model.entity.ServerClientDO;
import me.pgthinker.service.ServerClientService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Project: me.pgthinker.controller
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/5 18:36
 * @Description:
 */
@RequestMapping("/client")
@RestController
@RequiredArgsConstructor
public class ServerClientController {

    private final ServerClientService service;

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('admin')")
    public BaseResponse<List<ServerClientDO>> serverClientList(@RequestParam(name = "serverId") String serverId) {
        return ResultUtils.success(service.clientList(serverId));
    }


    @PostMapping("/offline/{clientId}")
    @PreAuthorize("hasAnyAuthority('admin')")
    public BaseResponse<String> offlineClient(@PathVariable(name = "clientId") Long clientId) {
        service.offline(clientId);
        return ResultUtils.success("ok");
    }


}
