package me.pgthinker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.pgthinker.model.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Project: me.pgthinker.mapper
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/25 01:17
 * @Description:
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}
