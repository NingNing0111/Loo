package me.pgthinker.aop;

import cn.hutool.http.HttpResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMessageDecoderResult;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpUtil;
import lombok.RequiredArgsConstructor;
import me.pgthinker.ProxyConfig;
import me.pgthinker.annotation.MessageLog;
import me.pgthinker.common.Constants;
import me.pgthinker.config.ServerConfig;
import me.pgthinker.enums.CmdTypeProto.CmdType;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;
import me.pgthinker.util.HttpResponseUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * @Project: me.pgthinker.aop
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/12 10:46
 * @Description:
 */
@Aspect
@Component
@RequiredArgsConstructor
public class MessageLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(MessageLogAspect.class);
    private final ServerConfig serverConfig;

    @Pointcut("@annotation(me.pgthinker.annotation.MessageLog)")
    public void messageLogPointcut(){
    }

    @Around("@annotation(messageLog)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, MessageLog messageLog) throws Throwable {
        Object[] args = joinPoint.getArgs();
        ChannelHandlerContext ctx = (ChannelHandlerContext) args[0];
        TransferDataMessage dataMessage = (TransferDataMessage)args[1];
        CmdType cmdType = dataMessage.getCmdType();
        List<CmdType> msgLogRange = serverConfig.getMsgLogRange();
        if(msgLogRange.contains(cmdType)){
            switch (cmdType) {
                case AUTH -> printAuthLog(ctx,dataMessage);
                case OPEN_SERVER -> printOpenServerLog(ctx,dataMessage);
                case TRANSFER -> printTransferLog(ctx,dataMessage);
            }
        }
        return joinPoint.proceed();
    }


    private void printAuthLog(ChannelHandlerContext ctx,TransferDataMessage dataMessage) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String hostString = inetSocketAddress.getHostString();
        String password = dataMessage.getMetaData().getMetaDataMap().get(Constants.AUTH_PASSWORD);
        logger.info("\nclient:{} -------> CmdType:{} password:{}", hostString,dataMessage.getCmdType(), password);
    }

    private void printOpenServerLog(ChannelHandlerContext ctx,TransferDataMessage dataMessage) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String hostString = inetSocketAddress.getHostString();
        Map<String, String> metaDataMap = dataMessage.getMetaData().getMetaDataMap();
        ProxyConfig proxyConfig = ProxyConfig.fromMap(metaDataMap);
        logger.info("\nClient:{} -------> CmdTyp:{} proxyConfig:{}", hostString, dataMessage.getCmdType(), proxyConfig);
    }

    private void printTransferLog(ChannelHandlerContext ctx,TransferDataMessage dataMessage) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String hostString = inetSocketAddress.getHostString();
        String dataStr = dataMessage.getData().toString(Charset.defaultCharset());
        Map<String, String> stringStringMap = HttpResponseUtil.parseMap(dataStr);
        logger.info("\nClient:{} -------> CmdTyp:{} \n", hostString, dataMessage.getCmdType());
        if(stringStringMap.containsKey("Content-Type")){
            logger.info("content-type:{} data:{}", stringStringMap.get("Content-Type"), stringStringMap.get("Response-Body"));
        }

    }


}
