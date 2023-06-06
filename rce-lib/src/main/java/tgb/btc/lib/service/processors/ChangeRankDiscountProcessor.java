package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.bean.User;
import tgb.btc.lib.bean.UserDiscount;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.repository.UserDiscountRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.UserDiscountService;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.CHANGE_RANK_DISCOUNT)
public class ChangeRankDiscountProcessor extends Processor {

    private RankDiscountProcessor rankDiscountProcessor;

    private UserDiscountRepository userDiscountRepository;

    private UserDiscountService userDiscountService;

    @Autowired
    public void setUserDiscountService(UserDiscountService userDiscountService) {
        this.userDiscountService = userDiscountService;
    }

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    @Autowired
    public void setRankDiscountProcessor(RankDiscountProcessor rankDiscountProcessor) {
        this.rankDiscountProcessor = rankDiscountProcessor;
    }

    @Override
    public void run(Update update) {
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        Long userChatId = Long.valueOf(values[1]);
        Boolean isRankDiscountOn = Boolean.valueOf(values[2]);
        Long userPid = userRepository.getPidByChatId(userChatId);
        if (userDiscountService.isExistByUserPid(userPid)) {
            userDiscountRepository.updateIsRankDiscountOnByPid(isRankDiscountOn, userRepository.getPidByChatId(userChatId));
        } else {
            UserDiscount userDiscount = new UserDiscount();
            userDiscount.setUser(new User(userPid));
            userDiscount.setRankDiscountOn(isRankDiscountOn);
            userDiscountRepository.save(userDiscount);
        }
        responseSender.deleteMessage(UpdateUtil.getChatId(update), update.getCallbackQuery().getMessage().getMessageId());
        rankDiscountProcessor.sendUserRankDiscount(UpdateUtil.getChatId(update), userChatId);
    }
}
