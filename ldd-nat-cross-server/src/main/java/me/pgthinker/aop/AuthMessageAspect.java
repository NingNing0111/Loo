package me.pgthinker.aop;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import me.pgthinker.annotation.AuthMessage;
import me.pgthinker.common.Constants;
import me.pgthinker.core.manager.ServerManager;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
public class AuthMessageAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuthMessageAspect.class);

    private final ServerManager serverManager;

    @Pointcut("@annotation(me.pgthinker.annotation.AuthMessage)")
    public void authMessageAspect(){
    }

    @Around("@annotation(authMessage)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthMessage authMessage) throws Throwable {

        Object[] args = joinPoint.getArgs();
        ChannelHandlerContext ctx = (ChannelHandlerContext) args[0];
        TransferDataMessage message = (TransferDataMessage)args[1];
        Map<String, String> metaDataMap = message.getMetaData().getMetaDataMap();

        String licenseKey = metaDataMap.get(Constants.LICENSE_KEY);
        List<String> licenseKeyList = serverManager.getLicenseKeyList();
        if(!licenseKeyList.contains(licenseKey)){
            ctx.close();
            logger.error("AUTH_ERROR!");
            logger.error("CmdType:{}", message.getCmdType());
            logger.error("MetaData:{}", message.getMetaData().getMetaDataMap());
            logger.error("All licenseKey:{}", licenseKeyList);
            throw new RuntimeException("AUTH_ERROR. cmdType:" + message.getCmdType());
        }
        // 否则放行
        return joinPoint.proceed();
    }

}
