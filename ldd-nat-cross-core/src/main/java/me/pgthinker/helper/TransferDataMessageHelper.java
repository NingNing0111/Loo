package me.pgthinker.helper;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.RequiredArgsConstructor;
import me.pgthinker.ProxyConfig;
import me.pgthinker.common.Constants;
import me.pgthinker.enums.CmdTypeProto.CmdType;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;
import me.pgthinker.message.TransferMessageMetaDataProto;
import me.pgthinker.message.TransferMessageMetaDataProto.TransferMessageMetaData;
import me.pgthinker.util.TimestampUtil;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Project: me.pgthinker.util
 * @Author: De Ning
 * @Date: 2024/10/7 21:29
 * @Description: 构建Message的Helper
 */
@RequiredArgsConstructor
public class TransferDataMessageHelper {

    private final String licenseKey;


    public static TransferDataMessage buildAuthOkMessage(String clientId){
        HashMap<String, String> meta = new HashMap<>();
        meta.put(Constants.LICENSE_KEY, clientId);
        TransferMessageMetaData metaData = TransferMessageMetaData.newBuilder()
                .putAllMetaData(meta)
                .setTimestamp(TimestampUtil.now())
                .build();
        return TransferDataMessage.newBuilder().setMetaData(metaData).setCmdType(CmdType.AUTH_OK).build();

    }

    public static TransferDataMessage buildAuthErrMessage() {
        return TransferDataMessage.newBuilder().setCmdType(CmdType.AUTH_ERR).build();
    }

    public static TransferDataMessage buildAuthMessage(String password) {
        Map<String, String> meta = new HashMap<>();
        meta.put(Constants.AUTH_PASSWORD, password);
        TransferMessageMetaData metaData = TransferMessageMetaData.newBuilder().putAllMetaData(meta)
                .setTimestamp(TimestampUtil.now())
                .build();
        return TransferDataMessage.newBuilder().setMetaData(metaData)
                .setCmdType(CmdType.AUTH).build();
    }

    public TransferDataMessage buildTransferMessage(ProxyConfig proxyConfig, ByteBuf data) {
        byte[] bytes = new byte[data.readableBytes()];
        data.readBytes(bytes);
        TransferMessageMetaData transferMessageMetaData = buildMetaMessage(proxyConfig);
        TransferDataMessage dataMessage = TransferDataMessage.newBuilder().setMetaData(transferMessageMetaData)
                .setCmdType(CmdType.TRANSFER)
                .setData(ByteString.copyFrom(bytes))
                .build();
        return dataMessage;
    }

    public TransferDataMessage buildTransferMessage(Map<String,String> meta, ByteBuf data) {
        byte[] bytes = new byte[data.readableBytes()];
        data.readBytes(bytes);
        TransferMessageMetaData metaData = TransferMessageMetaData.newBuilder().putAllMetaData(meta).build();
        return TransferDataMessage.newBuilder().setMetaData(metaData)
                .setCmdType(CmdType.TRANSFER)
                .setData(ByteString.copyFrom(bytes))
                .build();
    }

    public TransferDataMessage buildTransferMessage(Map<String, String> meta, byte[] bytes) {
        TransferMessageMetaData metaData = TransferMessageMetaData.newBuilder().putAllMetaData(meta).build();
        return TransferDataMessage.newBuilder().setMetaData(metaData)
                .setCmdType(CmdType.TRANSFER)
                .setData(ByteString.copyFrom(bytes))
                .build();
    }

    public TransferDataMessage buildOpenServerMessage(ProxyConfig proxyConfig) {
        TransferMessageMetaData transferMessageMetaData = buildMetaMessage(proxyConfig);
        return TransferDataMessage.newBuilder()
                .setMetaData(transferMessageMetaData)
                .setCmdType(CmdType.OPEN_SERVER).build();
    }

    public TransferDataMessage buildCloseServerMessage(ProxyConfig proxyConfig) {
        TransferMessageMetaData transferMessageMetaData = buildMetaMessage(proxyConfig);
        return TransferDataMessage.newBuilder()
                .setMetaData(transferMessageMetaData)
                .setCmdType(CmdType.CLOSE_SERVER).build();
    }


    public TransferMessageMetaData buildMetaMessage(ProxyConfig proxyConfig) {
        Map<String, String> map = proxyConfig.toMap();
        map.put(Constants.LICENSE_KEY, licenseKey);
        return TransferMessageMetaData.newBuilder()
                .setTimestamp(TimestampUtil.now())
                .putAllMetaData(map)
                .build();
    }

    public TransferDataMessage buildDisconnectMessage(ProxyConfig proxyConfig, String visitorId) {
        Map<String, String> metaMap = proxyConfig.toMap();
        metaMap.put(Constants.LICENSE_KEY, licenseKey);
        metaMap.put(Constants.VISITOR_ID, visitorId);
        TransferMessageMetaData metaData = TransferMessageMetaData.newBuilder().putAllMetaData(metaMap).setTimestamp(TimestampUtil.now()).build();
        return TransferDataMessage.newBuilder().setMetaData(metaData).setCmdType(CmdType.DISCONNECT).build();
    }


    public TransferDataMessage buildDisconnectMessage(ProxyConfig proxyConfig) {
        Map<String, String> metaMap = proxyConfig.toMap();
        metaMap.put(Constants.LICENSE_KEY, licenseKey);
        TransferMessageMetaData metaData = TransferMessageMetaData.newBuilder().putAllMetaData(metaMap).setTimestamp(TimestampUtil.now()).build();
        return TransferDataMessage.newBuilder().setMetaData(metaData).setCmdType(CmdType.DISCONNECT).build();
    }

    public TransferDataMessage buildDisconnectMessage(String message) {
        Map<String, String> metaMap = new HashMap<>();
        metaMap.put(Constants.MESSAGE, message);
        TransferMessageMetaData metaData = TransferMessageMetaData.newBuilder().putAllMetaData(metaMap).setTimestamp(TimestampUtil.now()).build();
        return TransferDataMessage.newBuilder().setMetaData(metaData).setCmdType(CmdType.DISCONNECT).build();
    }

    public TransferDataMessage buildConnectMessage(ProxyConfig proxyConfig,String visitorId) {
        Map<String, String> metaMap = proxyConfig.toMap();
        metaMap.put(Constants.LICENSE_KEY, licenseKey);
        metaMap.put(Constants.VISITOR_ID, visitorId);
        TransferMessageMetaData metaData = TransferMessageMetaData.newBuilder().putAllMetaData(metaMap).setTimestamp(TimestampUtil.now()).build();
        return TransferDataMessage.newBuilder().setMetaData(metaData).setData(ByteString.copyFromUtf8("empty")).setCmdType(CmdType.CONNECT).build();
    }

    public TransferDataMessage buildConnectMessage(ProxyConfig proxyConfig) {
        Map<String, String> metaMap = proxyConfig.toMap();
        metaMap.put(Constants.LICENSE_KEY, licenseKey);
        TransferMessageMetaData metaData = TransferMessageMetaData.newBuilder().putAllMetaData(metaMap).setTimestamp(TimestampUtil.now()).build();
        return TransferDataMessage.newBuilder().setMetaData(metaData).setCmdType(CmdType.CONNECT).build();
    }

    public static TransferDataMessage buildHeartbeatMessage(String msg) {
        ByteString bytes = ByteString.copyFrom(msg, Charset.defaultCharset());

        TransferMessageMetaData metaData = TransferMessageMetaData.newBuilder().setTimestamp(TimestampUtil.now()).build();
        return TransferDataMessage.newBuilder().setData(bytes).setMetaData(metaData).build();
    }
}
