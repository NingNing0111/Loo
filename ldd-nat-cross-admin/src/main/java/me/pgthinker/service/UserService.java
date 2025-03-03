package me.pgthinker.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.pgthinker.model.vo.UserVO;

/**
 * @Project: me.pgthinker.service
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/1 17:13
 * @Description:
 */
public interface UserService {

    Page<UserVO> list(UserVO userVO);
    Long addUser(UserVO userVO);
    Long updateUser(UserVO userVO);
    Long deleteUser(Long id);
}
