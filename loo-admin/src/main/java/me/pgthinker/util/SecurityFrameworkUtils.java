package me.pgthinker.util;

import me.pgthinker.model.entity.UserDO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @Project: me.pgthinker.util
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/28 19:31
 * @Description:
 */
public class SecurityFrameworkUtils {
    public static final String AUTHORIZATION_BEARER = "Bearer";

    public static Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }
        return context.getAuthentication();
    }

    public static UserDO getLoginUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getPrincipal() instanceof UserDO ? (UserDO) authentication.getPrincipal() : null;
    }
}
