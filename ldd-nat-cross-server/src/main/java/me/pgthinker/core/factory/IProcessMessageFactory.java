package me.pgthinker.core.factory;

import me.pgthinker.core.process.ProcessMessageService;
import me.pgthinker.enums.CmdTypeProto.CmdType;

/**
 * @Project: me.pgthinker.core.factory
 * @Author: De Ning
 * @Date: 2024/10/29 14:07
 * @Description:
 */
public interface IProcessMessageFactory {
    ProcessMessageService getProcessService(CmdType cmdType);
}
