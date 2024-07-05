package tgb.btc.rce.service;

import tgb.btc.rce.enums.Command;

public interface ICommandProcessorLoader {

    Processor getByCommand(Command command, int step);
}
