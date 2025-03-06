package me.pgthinker.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.pgthinker.admin.vo.VisitorConfigVO;
import me.pgthinker.model.entity.VisitorConfigDO;
import me.pgthinker.model.vo.PageBaseVO;

/**
 * @Project: me.pgthinker.service
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/5 18:38
 * @Description:
 */
public interface VisitorConfigService {

    Page<VisitorConfigVO> list(String serverName, PageBaseVO pageBaseVO);

    Long addVisitorConfig(VisitorConfigVO visitorConfigVO);

    Long updateVisitorConfig(VisitorConfigVO visitorConfigVO);

    Long deleteVisitorConfig(String serverName);
}
