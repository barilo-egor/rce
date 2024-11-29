package tgb.btc.rce.service.keyboard;

import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.vo.ReplyButton;

import java.util.Collection;
import java.util.List;

public interface IReplyButtonService {

    List<ReplyButton> fromTextCommands(Collection<TextCommand> commands);
}
