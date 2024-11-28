package tgb.btc.rce.service.util;

import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

public interface ICommandProcessorLoader {

    Processor getByCommand(Command command, Integer step);
}
