package me.pgthinker.config;

import cn.hutool.crypto.digest.BCrypt;
import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import me.pgthinker.admin.vo.VisitorConfigVO;
import me.pgthinker.enums.CmdTypeProto.CmdType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Project: me.pgthinker.config
 * @Author: De Ning
 * @Date: 2024/10/7 19:37
 * @Description:
 */
@Configuration
@ConfigurationProperties(prefix = "server")
@Data
public class ServerConfig {
    private Integer port;
    private String password;
    private Integer bossCnt = 1;
    private Integer workerCnt = Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));
    private Integer adminCnt = 1;
    private Integer readIdleTime = 60;
    private Integer writeIdleTime = 40;
    private Integer userChannelReadIdleTime = 0;
    private List<CmdType> msgLogRange = Collections.emptyList();
    private boolean isAllLog = false;
    private List<String> blackList = Collections.emptyList();
    private List<String> whiteList = Collections.emptyList();
    private AdminConfig admin;

    /**
     * 密码加密不允许set覆盖
     */
    @Setter(AccessLevel.NONE)
    private String encryptedPassword;

    /**
     * 初始化完成后自动对密码进行加密
     */
    @PostConstruct
    public void encryptPassword() {
        this.encryptedPassword = BCrypt.hashpw(this.password,BCrypt.gensalt());
    }


}
