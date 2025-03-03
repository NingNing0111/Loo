package me.pgthinker.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pgthinker.admin.common.AdminConstants;

import java.io.Serializable;

/**
 * @Project: me.pgthinker.admin
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/2 14:28
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterServerVO implements Serializable {
    private String serverName;
    private String hostname;
    private String osName;
    private String osVersion;
    private String osArch;

    public RegisterServerVO(String serverName, String hostname) {
        String osName = System.getProperty(AdminConstants.OS_NAME);
        String osVersion = System.getProperty(AdminConstants.OS_VERSION);
        String osArch = System.getProperty(AdminConstants.OS_ARCH);
        this.setServerName(serverName);
        this.setHostname(hostname);
        this.setOsName(osName);
        this.setOsVersion(osVersion);
        this.setOsArch(osArch);
    }
    private static final long serialVersionUID = 1L;

}
