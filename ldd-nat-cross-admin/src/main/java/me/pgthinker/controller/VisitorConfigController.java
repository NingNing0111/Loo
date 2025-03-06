package me.pgthinker.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.pgthinker.admin.vo.VisitorConfigVO;
import me.pgthinker.common.BaseResponse;
import me.pgthinker.common.ResultUtils;
import me.pgthinker.model.entity.VisitorConfigDO;
import me.pgthinker.model.vo.PageBaseVO;
import me.pgthinker.service.VisitorConfigService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @Project: me.pgthinker.controller
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/6 20:37
 * @Description:
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/visitor")
public class VisitorConfigController {

    private final VisitorConfigService visitorConfigService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('admin')")
    public BaseResponse<Page<VisitorConfigVO>> visitorConfigList(@RequestParam(name = "serverName") String serverName, PageBaseVO baseVO) {
        return ResultUtils.success(visitorConfigService.list(serverName, baseVO));
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('admin')")
    public BaseResponse<Long> addVisitorConfig(@RequestBody VisitorConfigVO visitorConfigVO) {
        return ResultUtils.success(visitorConfigService.addVisitorConfig(visitorConfigVO));
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyAuthority('admin')")
    public BaseResponse<Long> deleteVisitorConfig(@RequestParam(name = "serverName") String serverName) {
        return ResultUtils.success(visitorConfigService.deleteVisitorConfig(serverName));
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('admin')")
    public BaseResponse<Long> updateVisitorConfig(@RequestBody VisitorConfigVO visitorConfigVO) {
        return ResultUtils.success(visitorConfigService.updateVisitorConfig(visitorConfigVO));
    }

}
