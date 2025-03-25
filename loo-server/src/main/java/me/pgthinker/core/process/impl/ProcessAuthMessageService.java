package me.pgthinker.core.process.impl;

import cn.hutool.crypto.digest.BCrypt;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.admin.AdminClient;
import me.pgthinker.admin.vo.ServerClientVO;
import me.pgthinker.annotation.MessageLog;
import me.pgthinker.common.Constants;
import me.pgthinker.config.ServerConfig;
import me.pgthinker.core.process.ProcessMessageService;
import me.pgthinker.helper.TransferDataMessageHelper;
import me.pgthinker.core.manager.ServerManager;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @Project: me.pgthinker.core.process
 * @Author: De Ning
 * @Date: 2024/10/26 18:43
 * @Description: 认证处理
 */
@Slf4j
@Component("processAuthMessage")
@RequiredArgsConstructor
public class ProcessAuthMessageService implements ProcessMessageService {

    private final ServerConfig serverConfig;
    private final ServerManager serverManager;
    private final AdminClient adminClient;

    @MessageLog
    @Override
    public void process(ChannelHandlerContext target, TransferDataMessage transferDataMessage) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) target.channel().remoteAddress();

        Map<String, String> metaDataMap = transferDataMessage.getMetaData().getMetaDataMap();
        String authPassword = metaDataMap.get(Constants.AUTH_PASSWORD);

        boolean isOk = BCrypt.checkpw(authPassword, serverConfig.getEncryptedPassword());
        TransferDataMessage authResMessage;
        // 校验通过
        if(isOk){
            String licenseKey = serverManager.addClientChannel(target);
            authResMessage = TransferDataMessageHelper.buildAuthOkMessage(licenseKey);
            target.writeAndFlush(authResMessage);
            if(serverConfig.getAdmin().getEnabled() && adminClient.getServerId() != null) {
                ServerClientVO serverClientVO = new ServerClientVO();
                serverClientVO.setServerId(adminClient.getServerId());
                serverClientVO.setClientHost(inetSocketAddress.getHostName());
                serverClientVO.setClientPort(inetSocketAddress.getPort());
                serverClientVO.setLicenseKey(licenseKey);
                adminClient.addClientInfo(serverClientVO);
            }
        }else{
            authResMessage = TransferDataMessageHelper.buildAuthErrMessage();
            target.writeAndFlush(authResMessage);
            target.close();
        }
    }

}
