package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.CHANGE_RANK_DISCOUNT)
public class ChangeRankDiscountProcessor extends Processor {

    private UserRepository userRepository;

    private RankDiscountProcessor rankDiscountProcessor;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRankDiscountProcessor(RankDiscountProcessor rankDiscountProcessor) {
        this.rankDiscountProcessor = rankDiscountProcessor;
    }

    @Autowired
    public ChangeRankDiscountProcessor(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        Long userChatId = Long.valueOf(values[1]);
        Boolean isRankDiscountOn = Boolean.valueOf(values[2]);
        userRepository.updateIsRankDiscountOnByChatId(isRankDiscountOn, userChatId);
        responseSender.deleteMessage(UpdateUtil.getChatId(update), update.getCallbackQuery().getMessage().getMessageId());
        rankDiscountProcessor.sendUserRankDiscount(UpdateUtil.getChatId(update), userChatId);
    }
}
