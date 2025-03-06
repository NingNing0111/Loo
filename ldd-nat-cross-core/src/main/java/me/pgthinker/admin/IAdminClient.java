package me.pgthinker.admin;

import me.pgthinker.admin.vo.ServerClientVO;
import me.pgthinker.admin.vo.VisitorConfigVO;

/**
 * @Project: me.pgthinker.admin
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/4 17:59
 * @Description:
 */
public interface IAdminClient {
    String register();
    void startHeartbeat(String serverId);
    VisitorConfigVO readVisitorList();
    void addClientInfo(ServerClientVO serverClientVO);
    void removeClientInfo(String licenseKey);

    void startUpdateClient(String serverId);
}
