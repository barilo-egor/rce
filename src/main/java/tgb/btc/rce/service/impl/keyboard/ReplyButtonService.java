package tgb.btc.rce.service.impl.keyboard;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.keyboard.IReplyButtonService;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReplyButtonService implements IReplyButtonService {

    public List<ReplyButton> fromCommands(List<Command> commands) {
        if (CollectionUtils.isEmpty(commands))
            throw new BaseException("Список команд не может быть пуст.");
        return commands.stream()
                .map(command -> ReplyButton.builder().text(command.getText()).build())
                .collect(Collectors.toList());
    }
}
