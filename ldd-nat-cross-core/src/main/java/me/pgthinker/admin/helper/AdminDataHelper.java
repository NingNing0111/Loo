package me.pgthinker.admin.helper;

import cn.hutool.system.RuntimeInfo;
import cn.hutool.system.SystemUtil;
import me.pgthinker.admin.common.AdminConstants;
import me.pgthinker.admin.enums.AdminCmdTypeProto.CmdType;
import me.pgthinker.admin.message.AdminDataProto.AdminData;
import me.pgthinker.admin.message.AdminTransferDataMessageProto.TransferDataMessage;
import me.pgthinker.util.TimestampUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @Project: me.pgthinker.admin.helper
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 21:59
 * @Description:
 */
public class AdminDataHelper {
    /**
     * 构建注册信息
     * @param serverName
     * @return
     */
    public static TransferDataMessage buildRegisterMessage(String serverName) {
        Map<String, String> map = new HashMap<>();
        map.put(AdminConstants.SERVER_NAME, serverName);
        map.put(AdminConstants.OS_NAME, System.getProperty(AdminConstants.OS_NAME));
        map.put(AdminConstants.OS_VERSION, System.getProperty(AdminConstants.OS_VERSION));
        map.put(AdminConstants.OS_ARCH, System.getProperty(AdminConstants.OS_ARCH));
        AdminData adminData = AdminData.newBuilder()
                .setTimestamp(TimestampUtil.now())
                .putAllMetaData(map)
                .build();
        return TransferDataMessage.newBuilder()
                .setData(adminData)
                .setCmdType(CmdType.REGISTER)
                .build();
    }


    public static TransferDataMessage buildSystemInfoMessage() {
        Map<String, String> map = getSystemInfoMap();
        AdminData adminData = AdminData.newBuilder()
                .setTimestamp(TimestampUtil.now())
                .putAllMetaData(map)
                .build();
        return TransferDataMessage.newBuilder()
                .setData(adminData)
                .setCmdType(CmdType.TRANSFER)
                .build();
    }



    private static Map<String, String> getSystemInfoMap() {
        RuntimeInfo runtimeInfo = SystemUtil.getRuntimeInfo();
        Map<String, String> map = new HashMap<>();
        map.put(AdminConstants.RUNTIME_MAX_MEMORY, String.valueOf(runtimeInfo.getMaxMemory()));
        map.put(AdminConstants.RUNTIME_TOTAL_MEMORY, String.valueOf(runtimeInfo.getTotalMemory()));
        map.put(AdminConstants.RUNTIME_USABLE_MEMORY, String.valueOf(runtimeInfo.getUsableMemory()));
        map.put(AdminConstants.RUNTIME_FREE_MEMORY, String.valueOf(runtimeInfo.getFreeMemory()));
        return map;
    }
}
