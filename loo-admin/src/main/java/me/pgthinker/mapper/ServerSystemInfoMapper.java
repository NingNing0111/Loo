package me.pgthinker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.pgthinker.model.entity.ServerSystemInfoDO;
import me.pgthinker.model.vo.AnalysisDataVO;
import me.pgthinker.model.vo.SystemInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Project: me.pgthinker.mapper
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 23:46
 * @Description:
 */
@Mapper
public interface ServerSystemInfoMapper extends BaseMapper<ServerSystemInfoDO> {

    /**
     * 一天内的数据
     * @param serverName
     * @return
     */
    List<AnalysisDataVO> inDaySystemInfoList(@Param("serverName") String serverName);

    /**
     * 一个月内的数据分析
     * @param serverName
     * @return
     */
    List<AnalysisDataVO> onMonthSystemInfoList(@Param("serverName") String serverName);
}
