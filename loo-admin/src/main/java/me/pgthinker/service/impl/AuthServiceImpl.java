package me.pgthinker.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.common.ErrorCode;
import me.pgthinker.config.AdminConfig;
import me.pgthinker.exception.BusinessException;
import me.pgthinker.mapper.UserMapper;
import me.pgthinker.model.entity.UserDO;
import me.pgthinker.model.enums.RoleEnum;
import me.pgthinker.model.vo.AuthVO;
import me.pgthinker.model.vo.LoginUserVO;
import me.pgthinker.service.AuthService;
import me.pgthinker.util.JwtUtil;
import me.pgthinker.util.SecurityFrameworkUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Project: me.pgthinker.service.impl
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/25 16:22
 * @Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final AdminConfig adminConfig;

    @Override
    public LoginUserVO auth(AuthVO authVO) {
        UserDO userDO = checkAuthVO(authVO);
        String token = jwtUtil.generateToken(userDO);
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(userDO, loginUserVO);
        loginUserVO.setToken(token);
        return loginUserVO;
    }

    @Override
    public LoginUserVO userInfo() {
        UserDO loginUserDO = SecurityFrameworkUtils.getLoginUser();
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(loginUserDO, loginUserVO);
        return loginUserVO;
    }

    private UserDO checkAuthVO(AuthVO authVO) {
        String password = authVO.getPassword();
        String username = authVO.getUsername();
        if(StringUtils.isEmpty(password) || StringUtils.isEmpty(username)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<UserDO> userQW = new LambdaQueryWrapper<>();
        userQW.eq(UserDO::getUsername, username);
        UserDO storeUserDO = userMapper.selectOne(userQW);
        if(ObjectUtil.isEmpty(storeUserDO)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUNT);
        }
        if(!passwordEncoder.matches(password, storeUserDO.getPassword())){
            throw new BusinessException(ErrorCode.USER_ACCOUNT_ERROR);
        }
        return storeUserDO;
    }

    @Transactional(rollbackFor = Exception.class)
    @PostConstruct
    public void initRootAccount(){
        LambdaQueryWrapper<UserDO> userQW = new LambdaQueryWrapper<>();
        userQW.eq(UserDO::getUsername, adminConfig.getUsername());

        if(adminConfig.getNewAccount()) {
            userMapper.delete(userQW);
            UserDO userDO = new UserDO();
            userDO.setRole(RoleEnum.ADMIN.getName());
            userDO.setUsername(adminConfig.getUsername());
            userDO.setPassword(passwordEncoder.encode(adminConfig.getPassword()));
            userMapper.insert(userDO);
        }else{
            boolean exists = userMapper.exists(userQW);
            if(!exists){
                UserDO userDO = new UserDO();
                userDO.setRole(RoleEnum.ADMIN.getName());
                userDO.setUsername(adminConfig.getUsername());
                userDO.setPassword(passwordEncoder.encode(adminConfig.getPassword()));
                userMapper.insert(userDO);
            }
        }

    }
}
