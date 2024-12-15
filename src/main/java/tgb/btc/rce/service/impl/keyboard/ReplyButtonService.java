package tgb.btc.rce.service.impl.keyboard;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.keyboard.IReplyButtonService;
import tgb.btc.rce.service.util.ITextCommandService;
import tgb.btc.rce.vo.ReplyButton;

import java.util.Collection;
import java.util.List;

@Service
public class ReplyButtonService implements IReplyButtonService {

    private ITextCommandService commandService;

    @Autowired
    public void setCommandService(ITextCommandService commandService) {
        this.commandService = commandService;
    }

    @Override
    public List<ReplyButton> fromTextCommands(Collection<TextCommand> commands) {
        if (CollectionUtils.isEmpty(commands))
            throw new BaseException("Список команд не может быть пуст.");
        return commands.stream()
                .map(command -> ReplyButton.builder().text(commandService.getText(command)).build())
                .toList();
    }
}
