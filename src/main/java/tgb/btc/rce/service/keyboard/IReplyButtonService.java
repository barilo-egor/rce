package tgb.btc.rce.service.keyboard;

import tgb.btc.rce.enums.Command;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

public interface IReplyButtonService {

    List<ReplyButton> fromCommands(List<Command> commands);
}
