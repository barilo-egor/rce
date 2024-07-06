package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import tgb.btc.library.constants.enums.DiceType;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.service.process.DiceService;
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

import java.util.*;

@CommandProcessor(command = Command.DICE)
@Slf4j
public class Dice extends Processor {

    private IResponseSender responseSender;

    private DiceService diceService;

    private IUpdateDispatcher updateDispatcher;

    @Autowired
    public void setDiceService(DiceService diceService) {
        this.diceService = diceService;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setUpdateDispatcher(IUpdateDispatcher updateDispatcher) {
        this.updateDispatcher = updateDispatcher;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        boolean isAdmin = readUserService.isAdminByChatId(chatId);
        if (DiceType.NONE.isCurrent() || (DiceType.STANDARD_ADMIN.isCurrent() && !isAdmin)) {
            processToStart(chatId, update);
            return;
        }

        if (!update.hasCallbackQuery()) {
            Integer referralBalance = readUserService.getReferralBalanceByChatId(chatId);
            drawDiceBetButtons(chatId, String.format(diceService.selectBetMessage(), referralBalance));
            return;
        }

        if (isDrawsCommand(update)) return;
        if (CallbackQueryUtil.getSplitData(update.getCallbackQuery(), 1).startsWith("Bet")) {
            betCallBack(update);
            return;
        }
        if (CallbackQueryUtil.getSplitData(update.getCallbackQuery(), 1).startsWith("Number")) {
            numberCallBack(update);
        }
    }

    private void numberCallBack(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (PropertiesPath.DICE_PROPERTIES.getString("button.back.text").equals(CallbackQueryUtil.getSplitData(update.getCallbackQuery(), 2))) {
            responseSender.deleteCallbackMessageIfExists(update);
            Integer referralBalance = readUserService.getReferralBalanceByChatId(chatId);
            drawDiceBetButtons(chatId, String.format(diceService.selectBetMessage(), referralBalance));
        } else {
            Integer selectedNumber = Integer.parseInt(CallbackQueryUtil.getSplitData(update.getCallbackQuery(), 2));
            Integer bet = Integer.parseInt(CallbackQueryUtil.getSplitData(update.getCallbackQuery(), 4));
            responseSender.deleteCallbackMessageIfExists(update);
            rollDice(chatId, selectedNumber, bet);
        }
    }

    private void betCallBack(Update update) {
        if (PropertiesPath.DICE_PROPERTIES.getString("button.close.text")
                .equals(CallbackQueryUtil.getSplitData(update.getCallbackQuery(), 2))) {
            responseSender.deleteCallbackMessageIfExists(update);
        } else {
            Long chatId = UpdateUtil.getChatId(update);
            Integer bet = Integer.parseInt(CallbackQueryUtil.getSplitData(update.getCallbackQuery(), 2));
            if (readUserService.getReferralBalanceByChatId(chatId) < bet) {
                responseSender.sendMessage(chatId, PropertiesPath.DICE_MESSAGE.getString("balance.empty"));
                return;
            }
            responseSender.deleteCallbackMessageIfExists(update);

            Integer referralBalance = readUserService.getReferralBalanceByChatId(chatId);
            String text = PropertiesPath.DICE_MESSAGE.getString("selected.bet") + " " + bet + "₽" +
                    System.lineSeparator() + System.lineSeparator() +
                    "Выберите число:";
            drawDiceSelectWinNumber(chatId, String.format(text, referralBalance), bet);
        }
    }


    private void rollDice(Long chatId, Integer selectedNumber, Integer bet) {
        if (readUserService.getReferralBalanceByChatId(chatId) < bet) {
            responseSender.sendMessage(chatId, PropertiesPath.DICE_MESSAGE.getString("balance.empty"));
            return;
        }
        String rollText = PropertiesPath.DICE_MESSAGE.getString("roll") + System.lineSeparator() + System.lineSeparator() +
                PropertiesPath.DICE_MESSAGE.getString("selected.number") + " " + selectedNumber + System.lineSeparator() +
                PropertiesPath.DICE_MESSAGE.getString("selected.bet") + " " + bet + "₽";
        responseSender.sendMessage(chatId, rollText);
        Message message = responseSender.execute(SendDice.builder().chatId(chatId.toString()).emoji("\uD83C\uDFB2").build());
        if (message == null) {
            return;
        }
        Integer referralBalance = readUserService.getReferralBalanceByChatId(chatId);
        log.trace("Исходный баланс пользователя " + chatId + " :" + referralBalance);
        referralBalance -= bet;
        int diceValue = message.getDice().getValue();

        log.trace("Пользователь " + chatId + " бросил " + diceValue + ")");
        String resultText;
        if (selectedNumber == diceValue) {
            referralBalance += bet * 3;
            resultText = String.format(diceService.winMessage(),diceValue, bet,referralBalance);
        }else {
            resultText = String.format(diceService.loseMessage(),diceValue, selectedNumber, referralBalance);
        }
        log.trace("Сохранение баланса пользователя " + chatId + " :" + referralBalance);
        modifyUserService.updateReferralBalanceByChatId(referralBalance, chatId);

        responseSender.sendMessage(chatId,resultText,"Markdown");
        referralBalance = readUserService.getReferralBalanceByChatId(chatId);
        drawDiceBetButtons(chatId, String.format(diceService.selectBetMessage(), referralBalance));
    }

    private void drawDiceSelectWinNumber(Long chatId, String text, Integer bet) {
        String backText = PropertiesPath.DICE_PROPERTIES.getString("button.back.text");
        List<InlineButton> buttons = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            buttons.add(InlineButton.builder()
                    .text(String.valueOf(i + 1))
                    .data(CallbackQueryUtil.buildCallbackData(Command.DICE.getText(), "Number:" + (i + 1), "Bet:" + bet))
                    .build());
        }
        buttons.add(InlineButton.builder()
                .text(backText)
                .data(CallbackQueryUtil.buildCallbackData(Command.DICE.getText(), "Number:" + backText))
                .build());
        responseSender.sendMessage(chatId, StringUtils.defaultIfBlank(text, "Выберите число"),
                KeyboardUtil.buildInline(buttons,2), "Markdown");
    }

    private void drawDiceBetButtons(Long chatId, String text) {
        String closeText = PropertiesPath.DICE_PROPERTIES.getString("button.close.text");
        String[] sums = PropertiesPath.DICE_PROPERTIES.getStringArray("sums");

        List<InlineButton> buttons = new ArrayList<>();

        Arrays.stream(sums).forEach(sum -> {
            buttons.add(InlineButton.builder()
                    .text(sum + "₽")
                    .data(CallbackQueryUtil.buildCallbackData(Command.DICE.getText(), "Bet:" + sum))
                    .build());
        });
        buttons.add(InlineButton.builder()
                .text(closeText)
                .data(CallbackQueryUtil.buildCallbackData(Command.DICE.getText(), "Bet:" + closeText))
                .build());
        responseSender.sendMessage(chatId, StringUtils.defaultIfBlank(text, "Выберите ставку:"),
                buildBetButtons(buttons), "Markdown");
    }

    private InlineKeyboardMarkup buildBetButtons(List<InlineButton> buttons){
        InlineKeyboardMarkup first = KeyboardUtil.buildInline(Collections.singletonList(buttons.remove(0)),1);
        InlineKeyboardMarkup last = KeyboardUtil.buildInline(Collections.singletonList(buttons.remove(buttons.size()-1)),1);
        List<List<InlineKeyboardButton>> listInlineButtons = new ArrayList<>(first.getKeyboard());
        listInlineButtons.addAll(KeyboardUtil.buildInline(buttons,2).getKeyboard());
        listInlineButtons.addAll(last.getKeyboard());
        return InlineKeyboardMarkup.builder().keyboard(listInlineButtons).build();
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

    private void processToStart(Long chatId, Update update) {
        responseSender.deleteCallbackMessageIfExists(update);
        updateDispatcher.runProcessor(Command.START, chatId, update);
    }

}
