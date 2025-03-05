package me.pgthinker.admin.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import me.pgthinker.admin.vo.HeartbeatDataVO;
import me.pgthinker.admin.vo.RegisterServerVO;
import me.pgthinker.admin.service.AdminService;
import me.pgthinker.admin.vo.ServerClientVO;
import me.pgthinker.admin.vo.VisitorConfigVO;
import me.pgthinker.common.BaseResponse;
import me.pgthinker.common.ResultUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @Project: me.pgthinker.controller
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/2 14:25
 * @Description:
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/register")
    @PermitAll
    public BaseResponse<String> register(@RequestBody RegisterServerVO registerServerVO) {
        return ResultUtils.success(adminService.register(registerServerVO));
    }

    @PostMapping("/heartbeat/{serverId}")
    @PermitAll
    public BaseResponse<String> heartbeat(@RequestBody HeartbeatDataVO heartbeatDataVO, @PathVariable(name = "serverId") String serverId){
        adminService.heartbeat(serverId, heartbeatDataVO);
        return ResultUtils.success("ok");
    }

    @PostMapping("/client/add")
    @PermitAll
    public BaseResponse<String> addClientInfo(@RequestBody ServerClientVO clientVO) {
        adminService.addClient(clientVO);
        return ResultUtils.success("ok");
    }

    @PostMapping("/client/delete/{licenseKey}")
    @PermitAll
    public BaseResponse<String> removeClientInfo(@PathVariable(name = "licenseKey") String licenseKey) {
        adminService.removeClient(licenseKey);
        return ResultUtils.success("ok");
    }

    @GetMapping("/config/visitor/{serverName}")
    @PermitAll
    public BaseResponse<VisitorConfigVO> getClientInfo(@PathVariable(name = "serverName") String serverName) {
        return ResultUtils.success(adminService.visitorConfig(serverName));
    }
}
