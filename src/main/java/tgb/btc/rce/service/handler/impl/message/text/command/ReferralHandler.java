package tgb.btc.rce.service.handler.impl.message.text.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.ReferralUser;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealCountService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.library.service.properties.BotPropertiesReader;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.enums.Rank;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IMessageImageResponseSender;
import tgb.btc.rce.service.IMessageImageService;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICommandService;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReferralHandler implements ITextCommandHandler {

    private final ICommandService commandService;

    private final IReadUserService readUserService;

    private final IBigDecimalService bigDecimalService;

    private final IDealCountService dealCountService;

    private final IMessageImageService messageImageService;

    private final IMessageImageResponseSender messageImageResponseSender;

    private final IKeyboardBuildService keyboardBuildService;

    private final BotPropertiesReader botPropertiesReader;

    public ReferralHandler(ICommandService commandService, IReadUserService readUserService,
                           IBigDecimalService bigDecimalService, IDealCountService dealCountService,
                           IMessageImageService messageImageService,
                           IMessageImageResponseSender messageImageResponseSender,
                           IKeyboardBuildService keyboardBuildService, BotPropertiesReader botPropertiesReader) {
        this.commandService = commandService;
        this.readUserService = readUserService;
        this.bigDecimalService = bigDecimalService;
        this.dealCountService = dealCountService;
        this.messageImageService = messageImageService;
        this.messageImageResponseSender = messageImageResponseSender;
        this.keyboardBuildService = keyboardBuildService;
        this.botPropertiesReader = botPropertiesReader;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        String startParameter = "?start=" + chatId;
        String refLink = botPropertiesReader.getString("SEND_LINK").concat(startParameter);
        BigDecimal referralBalance = BigDecimal.valueOf(readUserService.getReferralBalanceByChatId(chatId));
        String currentBalance = bigDecimalService.roundToPlainString(referralBalance);
        List<ReferralUser> referralUsers = readUserService.getUserReferralsByChatId(chatId);
        String numberOfReferrals = String.valueOf(referralUsers.size());
        int numberOfActiveReferrals = (int) referralUsers.stream()
                .filter(usr -> dealCountService.getCountConfirmedByUserChatId(usr.getChatId()) > 0).count();

        Long dealsCount = dealCountService.getCountConfirmedByUserChatId(chatId);
        Rank rank = Rank.getByDealsNumber(dealsCount.intValue());
        MessageImage messageImage = MessageImage.REFERRAL;
        Integer subType = messageImageService.getSubType(messageImage);
        switch (subType) {
            case 1:
                messageImageResponseSender.sendMessage(messageImage, chatId,
                        String.format(messageImageService.getMessage(messageImage),
                                refLink, currentBalance, numberOfReferrals, numberOfActiveReferrals,
                                readUserService.getChargesByChatId(chatId), dealsCount, rank.getSmile(),
                                rank.getPercent()),
                        keyboardBuildService.buildInline(getButtons(refLink)));
                break;
            case 2:
                String refBalanceString = PropertiesPath.VARIABLE_PROPERTIES.isNotBlank("course.rub.byn")
                        ? bigDecimalService.roundToPlainString(referralBalance.multiply(PropertiesPath.VARIABLE_PROPERTIES.getBigDecimal("course.rub.byn")), 2)
                        : bigDecimalService.roundToPlainString(referralBalance);
                messageImageResponseSender.sendMessage(messageImage, chatId,
                        String.format(messageImageService.getMessage(messageImage),
                                refLink, currentBalance, refBalanceString,
                                numberOfReferrals, numberOfActiveReferrals,
                                readUserService.getChargesByChatId(chatId), dealsCount, rank.getSmile(), rank.getPercent()),
                        keyboardBuildService.buildInline(getButtons(refLink)));
                break;
        }
    }

    private List<InlineButton> getButtons(String refLink) {
        return List.of(InlineButton.builder()
                        .text("Пригласить друга")
                        .data(refLink)
                        .inlineType(InlineType.SWITCH_INLINE_QUERY)
                        .build(),
                InlineButton.builder()
                        .text(commandService.getText(Command.WITHDRAWAL_OF_FUNDS))
                        .data(Command.WITHDRAWAL_OF_FUNDS.name())
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build());
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.REFERRAL;
    }
}
