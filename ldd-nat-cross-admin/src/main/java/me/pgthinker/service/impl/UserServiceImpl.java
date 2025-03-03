package me.pgthinker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.pgthinker.common.ErrorCode;
import me.pgthinker.exception.BusinessException;
import me.pgthinker.mapper.UserMapper;
import me.pgthinker.model.entity.UserDO;
import me.pgthinker.model.vo.UserVO;
import me.pgthinker.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Project: me.pgthinker.service.impl
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/1 17:14
 * @Description:
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public Page<UserVO> list(UserVO userVO) {
        LambdaQueryWrapper<UserDO> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotEmpty(userVO.getUsername()), UserDO::getUsername, userVO.getUsername());
        qw.eq(StringUtils.isNotBlank(userVO.getRole()), UserDO::getRole, userVO.getRole());
        Page<UserDO> qPage = Page.of(userVO.getPage(), userVO.getPageSize());
        Page<UserDO> userPage = userMapper.selectPage(qPage, qw);
        List<UserVO> vos = transform(userPage.getRecords());
        Page<UserVO> res = new Page<>();
        BeanUtils.copyProperties(userPage, res);
        res.setRecords(vos);
        return res;
    }

    @Override
    public Long addUser(UserVO userVO) {
        String username = userVO.getUsername();
        String password = userVO.getPassword();
        String role = userVO.getRole();
        if (StringUtils.isAnyEmpty(username, password, role)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断用户名是否存在
        LambdaUpdateWrapper<UserDO> qw = new LambdaUpdateWrapper<>();
        qw.eq(UserDO::getUsername, username);
        boolean exists = userMapper.exists(qw);
        if (exists) {
            throw new BusinessException(ErrorCode.USER_EXISTS);
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userVO, userDO);
        userMapper.insert(userDO);
        return userDO.getId();
    }

    @Override
    public Long updateUser(UserVO userVO) {

        return 0L;
    }

    @Override
    public Long deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserDO userDO = userMapper.selectById(id);
        if (userDO == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUNT);
        }
        if (Objects.equals(userDO.getUsername(), "admin")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "系统管理员用户不允许删除");
        }
        userMapper.deleteById(id);
        return id;
    }


    private List<UserVO> transform(List<UserDO> userDOS) {
        return userDOS.stream().map(item -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(item, userVO);
            userVO.setPassword(null);
            return userVO;
        }).collect(Collectors.toList());
    }
}
