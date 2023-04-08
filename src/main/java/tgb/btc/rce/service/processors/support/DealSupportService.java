package tgb.btc.rce.service.processors.support;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.vo.InlineButton;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DealSupportService {

    private final DealService dealService;
    private final UserService userService;

    @Autowired
    public DealSupportService(DealService dealService, UserService userService) {
        this.dealService = dealService;
        this.userService = userService;
    }

    public String dealToString(Long pid) {
        Deal deal = dealService.getByPid(pid);
        User user = deal.getUser();
        return String.format(BotStringConstants.DEAL_INFO, deal.getDealType().getDisplayName(), deal.getPid(),
                deal.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                deal.getPaymentType().getDisplayName(),
                deal.getWallet(), StringUtils.defaultIfEmpty(userService.getUsernameByChatId(user.getChatId()), BotStringConstants.ABSENT),
                dealService.getCountPassedByUserChatId(user.getChatId()), user.getChatId(), deal.getCryptoCurrency().getShortName(),
                deal.getCryptoAmount().setScale(8, RoundingMode.FLOOR).stripTrailingZeros().toPlainString(),
                deal.getAmount().setScale(0, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
    }

    public ReplyKeyboard dealToStringButtons(Long pid) {
        return KeyboardUtil.buildInline(List.of(
                InlineButton.builder()
                        .text("Подтвердить")
                        .data(Command.CONFIRM_USER_DEAL.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER + pid)
                        .build(),
                InlineButton.builder()
                        .text("Доп.верификация")
                        .data(Command.ADDITIONAL_VERIFICATION.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER + pid)
                        .build(),
                InlineButton.builder()
                        .text("Удалить")
                        .data(Command.DELETE_USER_DEAL.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER + pid)
                        .build(),
                InlineButton.builder()
                        .text("Удалить и заблокировать")
                        .data(Command.DELETE_DEAL_AND_BLOCK_USER.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER + pid)
                        .build()
                )

        );
    }
}
