package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.SlotReelType;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.service.bean.bot.UserService;
import tgb.btc.library.service.process.SlotReelService;
import tgb.btc.library.vo.slotReel.ScrollResult;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.sender.IResponseSender;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@CommandProcessor(command = Command.SLOT_REEL)
@Slf4j
public class SlotReel extends Processor {

    private IResponseSender responseSender;

    private SlotReelService slotReelService;

    private IUpdateDispatcher updateDispatcher;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setSlotReelService(SlotReelService slotReelService) {
        this.slotReelService = slotReelService;
    }

    @Autowired
    public void setUpdateDispatcher(IUpdateDispatcher updateDispatcher) {
        this.updateDispatcher = updateDispatcher;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        boolean isAdmin = userService.isAdminByChatId(chatId);
        if (SlotReelType.NONE.isCurrent() || (SlotReelType.STANDARD_ADMIN.isCurrent() && !isAdmin)) {
            processToStart(chatId, update);
            return;
        }

        if(!update.hasCallbackQuery()){
            drawSlotButtons(chatId, slotReelService.startMessage());
            return;
        }
        if (isDrawsCommand(update)) return;
        if (PropertiesPath.SLOT_REEL_PROPERTIES.getString("button.try.text")
                .equals(CallbackQueryUtil.getSplitData(update.getCallbackQuery(), 1))) {
            scroll(chatId);
        } else {
            responseSender.deleteCallbackMessageIfExists(update);
        }
    }

    private boolean isDrawsCommand(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return false;
        Long chatId = UpdateUtil.getChatId(update);
        Command drawsCommand = null;
        Command commandFromUpdate = Command.fromUpdate(update);
        List<Command> commands = new ArrayList<>(Menu.DRAWS.getCommands());
        for (Command command : commands) {
            if (command.equals(commandFromUpdate)) drawsCommand = command;
        }
        if (Objects.isNull(drawsCommand)) return false;
        responseSender.deleteCallbackMessageIfExists(update);
        updateDispatcher.runProcessor(drawsCommand, chatId, update);
        return true;
    }


    private void scroll(Long chatId) {
        log.trace("Пользователь " + chatId + " крутит барабан");
        if (readUserService.getReferralBalanceByChatId(chatId) < PropertiesPath.SLOT_REEL_PROPERTIES.getInteger("try", 10)) {
            responseSender.sendMessage(chatId, PropertiesPath.SLOT_REEL_MESSAGE.getString("balance.empty"));
            return;
        }
        String scrollText = PropertiesPath.SLOT_REEL_MESSAGE.getString("scroll");
        if (StringUtils.isNotBlank(scrollText)) {
            responseSender.sendMessage(chatId, scrollText);
        }
        Message message = responseSender.execute(SendDice.builder().chatId(chatId.toString()).emoji("\uD83C\uDFB0").build());
        if (message == null) {
            return;
        }
        Integer referralBalance = readUserService.getReferralBalanceByChatId(chatId);
        log.trace("Исходный баланс пользователя " + chatId + " :" + referralBalance);
        referralBalance -= PropertiesPath.SLOT_REEL_PROPERTIES.getInteger("try", 10);
        int diceValue = message.getDice().getValue();
        ScrollResult scrollResult = slotReelService.scrollResult(diceValue);
        log.trace("Пользователь " + chatId + " зароллил " + Arrays.toString(scrollResult.getSlotValues()) + "(" + diceValue + ")");
        StringBuilder sb = new StringBuilder();
        sb.append("ℹ *Ваша комбинация:* ")
                .append(slotReelService.slotCombinationToText(scrollResult.getSlotValues(), ", ")).append(System.lineSeparator()).append(System.lineSeparator());
        if (scrollResult.getWinAmount() != null) {
            referralBalance += Integer.parseInt(scrollResult.getWinAmount());
            sb.append("*").append(PropertiesPath.SLOT_REEL_MESSAGE.getString("win")).append("* ").append(scrollResult.getWinAmount())
                    .append("₽!").append(System.lineSeparator()).append(System.lineSeparator());
        } else {
            sb.append(PropertiesPath.SLOT_REEL_MESSAGE.getString("lose")).append(System.lineSeparator()).append(System.lineSeparator());
        }
        log.trace("Сохранение баланса пользователя " + chatId + " :" + referralBalance);
        modifyUserService.updateReferralBalanceByChatId(referralBalance, chatId);
        sb.append("\uD83D\uDCB8 *Ваш текущий баланс:* ").append(referralBalance).append("₽");
        drawSlotButtons(chatId, sb.toString());

    }

    private void drawSlotButtons(Long chatId, String text) {
        String tryText = PropertiesPath.SLOT_REEL_PROPERTIES.getString("button.try.text");
        String closeText = PropertiesPath.SLOT_REEL_PROPERTIES.getString("button.close.text");
        List<InlineButton> buttons = new ArrayList<>();
        buttons.add(InlineButton.builder()
                .text(tryText)
                .data(CallbackQueryUtil.buildCallbackData(Command.SLOT_REEL.getText(), tryText))
                .build());
        buttons.add(InlineButton.builder()
                .text(closeText)
                .data(CallbackQueryUtil.buildCallbackData(Command.SLOT_REEL.getText(), closeText))
                .build());
        responseSender.sendMessage(chatId, StringUtils.defaultIfBlank(text, "Выберите действие"),
                KeyboardUtil.buildInline(buttons), "Markdown");
    }

    private void processToStart(Long chatId, Update update) {
        responseSender.deleteCallbackMessageIfExists(update);
        updateDispatcher.runProcessor(Command.START, chatId, update);
    }

}
