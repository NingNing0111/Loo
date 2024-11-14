package me.pgthinker.core.factory;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.core.process.ProcessMessageService;
import me.pgthinker.enums.CmdTypeProto.CmdType;
import org.springframework.stereotype.Component;

/**
 * @Project: me.pgthinker.core.factory
 * @Author: De Ning
 * @Date: 2024/10/29 14:10
 * @Description: 消息处理工厂，根据服务端接收到的消息类型 返回对应的处理器
 *
 * 服务端可能接收到的消息：
 *     HEARTBEAT = 0; // 客户端发送过来一个心跳包
 *     AUTH = 1; // 认证 客户端发送过来认证包 用于握手
 *     TRANSFER = 6; // 数据传输 客户端发送过来代理的数据包
 *     OPEN_SERVER = 7; // 开启代理端口 客户端指令 开启公网上的某个代理服务
 *     CLOSE_SERVER = 8; // 关闭代理端口 客户端指令 关闭公网上的某个代理服务
 */
@Component
@Slf4j
public class ProcessMessageFactory implements IProcessMessageFactory{

    @Resource(name = "processAuthMessage")
    private ProcessMessageService processAuthMessage;
    @Resource(name = "processCloseServerMessage")
    private ProcessMessageService processCloseServerMessage;
    @Resource(name = "processHeartbeatMessage")
    private ProcessMessageService processHeartbeatMessage;
    @Resource(name = "processOpenServerMessage")
    private ProcessMessageService processOpenServerMessage;
    @Resource(name = "processTransferMessage")
    private ProcessMessageService processTransferMessage;
    @Resource(name = "processDisconnectMessage")
    private ProcessMessageService processDisconnectMessage;

    @Override
    public ProcessMessageService getProcessService(CmdType cmdType) {
        switch (cmdType) {
            case AUTH -> { // 认证信息处理
                return processAuthMessage;
            }
            case OPEN_SERVER -> { // 开放端口服务
                return processOpenServerMessage;
            }
            case TRANSFER -> { // 信息传输
                return processTransferMessage;
            }

            case CLOSE_SERVER -> { // 关闭端口服务
                return processCloseServerMessage;
            }
            case HEARTBEAT -> { // 心跳检测
                return processHeartbeatMessage;
            }
            case DISCONNECT -> { // 端口访问channel连接
                return processDisconnectMessage;
            }
        }
        throw new RuntimeException("unknown type:" + cmdType);
    }
}
