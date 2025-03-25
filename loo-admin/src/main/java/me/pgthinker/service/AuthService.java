package me.pgthinker.service;

import me.pgthinker.model.vo.AuthVO;
import me.pgthinker.model.vo.LoginUserVO;

/**
 * @Project: me.pgthinker.service
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/25 16:20
 * @Description:
 */
public interface AuthService {
    LoginUserVO auth(AuthVO authVO);

    LoginUserVO userInfo();
}
