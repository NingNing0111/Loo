package me.pgthinker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.admin.vo.VisitorConfigVO;
import me.pgthinker.common.ErrorCode;
import me.pgthinker.exception.BusinessException;
import me.pgthinker.mapper.VisitorConfigMapper;
import me.pgthinker.model.entity.UserDO;
import me.pgthinker.model.entity.VisitorConfigDO;
import me.pgthinker.model.enums.ConfigTypeEnum;
import me.pgthinker.model.vo.PageBaseVO;
import me.pgthinker.model.vo.UserVO;
import me.pgthinker.service.VisitorConfigService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Project: me.pgthinker.service.impl
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/6 20:15
 * @Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VisitorConfigServiceImpl implements VisitorConfigService {

    private final VisitorConfigMapper visitorConfigMapper;


    @Override
    public Page<VisitorConfigVO> list(String serverName, PageBaseVO pageBaseVO) {
        LambdaQueryWrapper<VisitorConfigDO> qw = new LambdaQueryWrapper<>();
        qw.like(VisitorConfigDO::getServerName, serverName);
        Page<VisitorConfigDO> qPage = Page.of(pageBaseVO.getPage(), pageBaseVO.getPageSize());
        Page<VisitorConfigDO> configPage = visitorConfigMapper.selectPage(qPage, qw);
        List<VisitorConfigVO> configList = transform(configPage.getRecords());

        Page<VisitorConfigVO> res = new Page<>();
        BeanUtils.copyProperties(configPage, res);
        res.setRecords(configList);
        return res;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long addVisitorConfig(VisitorConfigVO visitorConfigVO) {
        if(StringUtils.isEmpty(visitorConfigVO.getServerName())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<VisitorConfigDO> qw = new LambdaQueryWrapper<>();
        qw.eq(VisitorConfigDO::getServerName, visitorConfigVO.getServerName());
        boolean exists = visitorConfigMapper.exists(qw);
        if(exists) {
            throw new BusinessException(ErrorCode.VISITOR_CONFIG_EXISTS);
        }
        VisitorConfigDO visitorConfigDO = new VisitorConfigDO();
        BeanUtils.copyProperties(visitorConfigVO, visitorConfigDO);
        visitorConfigDO.setType(ConfigTypeEnum.SERVER.getValue());
        visitorConfigMapper.insert(visitorConfigDO);

        return visitorConfigDO.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long updateVisitorConfig(VisitorConfigVO visitorConfigVO) {
        String serverName = visitorConfigVO.getServerName();
        List<String> blackList = visitorConfigVO.getBlackList();
        List<String> whiteList = visitorConfigVO.getWhiteList();
        if(StringUtils.isEmpty(serverName) || blackList.isEmpty() || whiteList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaUpdateWrapper<VisitorConfigDO> uw = new LambdaUpdateWrapper<>();
        uw.eq(VisitorConfigDO::getServerName,serverName);
        uw.set(VisitorConfigDO::getBlackList,blackList);
        uw.set(VisitorConfigDO::getWhiteList,whiteList);
        int cnt = visitorConfigMapper.update(uw);

        return (long) cnt;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long deleteVisitorConfig(String serverName) {
        LambdaQueryWrapper<VisitorConfigDO> qw = new LambdaQueryWrapper<>();
        qw.eq(VisitorConfigDO::getServerName, serverName);
        int cnt = visitorConfigMapper.delete(qw);
        return (long) cnt;
    }

    private List<VisitorConfigVO> transform(List<VisitorConfigDO> configDOS) {
        return configDOS.stream().map(item -> {
            VisitorConfigVO visitorConfigVO = new VisitorConfigVO();
            BeanUtils.copyProperties(item, visitorConfigVO);
            return visitorConfigVO;
        }).toList();
    }
}
