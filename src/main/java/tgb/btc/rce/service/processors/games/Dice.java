package tgb.btc.rce.service.processors.games;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import tgb.btc.library.constants.enums.DiceType;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.library.service.process.DiceService;
import tgb.btc.library.service.properties.DiceMessagePropertiesReader;
import tgb.btc.library.service.properties.DiceProperties;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.util.IUpdateDispatcher;
import tgb.btc.rce.vo.InlineButton;

import java.util.*;

@CommandProcessor(command = Command.DICE)
@Slf4j
public class Dice extends Processor {

    private IResponseSender responseSender;

    private DiceService diceService;

    private IUpdateDispatcher updateDispatcher;

    private IModule<DiceType> diceModule;

    private IStartService startService;

    private DiceMessagePropertiesReader diceMessagePropertiesReader;
    
    private DiceProperties diceProperties;

    @Autowired
    public void setDiceProperties(DiceProperties diceProperties) {
        this.diceProperties = diceProperties;
    }

    @Autowired
    public void setDiceMessagePropertiesReader(DiceMessagePropertiesReader diceMessagePropertiesReader) {
        this.diceMessagePropertiesReader = diceMessagePropertiesReader;
    }

    @Autowired
    public void setStartService(IStartService startService) {
        this.startService = startService;
    }

    @Autowired
    public void setDiceModule(IModule<DiceType> diceModule) {
        this.diceModule = diceModule;
    }

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
        Long chatId = updateService.getChatId(update);
        if (diceModule.isCurrent(DiceType.NONE) || (diceModule.isCurrent(DiceType.STANDARD_ADMIN) && UserRole.USER.equals(readUserService.getUserRoleByChatId(chatId)))) {
            processToStart(chatId, update);
            return;
        }

        if (!update.hasCallbackQuery()) {
            Integer referralBalance = readUserService.getReferralBalanceByChatId(chatId);
            drawDiceBetButtons(chatId, String.format(diceService.selectBetMessage(), referralBalance));
            return;
        }

        if (isDrawsCommand(update)) return;
        if (callbackQueryService.getSplitData(update.getCallbackQuery(), 1).startsWith("Bet")) {
            betCallBack(update);
            return;
        }
        if (callbackQueryService.getSplitData(update.getCallbackQuery(), 1).startsWith("Number")) {
            numberCallBack(update);
        }
    }

    private void numberCallBack(Update update) {
        Long chatId = updateService.getChatId(update);
        if (diceProperties.getString("button.back.text").equals(callbackQueryService.getSplitData(update.getCallbackQuery(), 2))) {
            responseSender.deleteCallbackMessageIfExists(update);
            Integer referralBalance = readUserService.getReferralBalanceByChatId(chatId);
            drawDiceBetButtons(chatId, String.format(diceService.selectBetMessage(), referralBalance));
        } else {
            Integer selectedNumber = Integer.parseInt(callbackQueryService.getSplitData(update.getCallbackQuery(), 2));
            Integer bet = Integer.parseInt(callbackQueryService.getSplitData(update.getCallbackQuery(), 4));
            responseSender.deleteCallbackMessageIfExists(update);
            rollDice(chatId, selectedNumber, bet);
        }
    }

    private void betCallBack(Update update) {
        if (diceProperties.getString("button.close.text")
                .equals(callbackQueryService.getSplitData(update.getCallbackQuery(), 2))) {
            responseSender.deleteCallbackMessageIfExists(update);
        } else {
            Long chatId = updateService.getChatId(update);
            Integer bet = Integer.parseInt(callbackQueryService.getSplitData(update.getCallbackQuery(), 2));
            if (readUserService.getReferralBalanceByChatId(chatId) < bet) {
                responseSender.sendMessage(chatId, diceMessagePropertiesReader.getString("balance.empty"));
                return;
            }
            responseSender.deleteCallbackMessageIfExists(update);

            Integer referralBalance = readUserService.getReferralBalanceByChatId(chatId);
            String text = diceMessagePropertiesReader.getString("selected.bet") + " " + bet + "₽" +
                    System.lineSeparator() + System.lineSeparator() +
                    "Выберите число:";
            drawDiceSelectWinNumber(chatId, String.format(text, referralBalance), bet);
        }
    }


    private void rollDice(Long chatId, Integer selectedNumber, Integer bet) {
        if (readUserService.getReferralBalanceByChatId(chatId) < bet) {
            responseSender.sendMessage(chatId, diceMessagePropertiesReader.getString("balance.empty"));
            return;
        }
        String rollText = diceMessagePropertiesReader.getString("roll") + System.lineSeparator() + System.lineSeparator() +
                diceMessagePropertiesReader.getString("selected.number") + " " + selectedNumber + System.lineSeparator() +
                diceMessagePropertiesReader.getString("selected.bet") + " " + bet + "₽";
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
        String backText = diceProperties.getString("button.back.text");
        List<InlineButton> buttons = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            buttons.add(InlineButton.builder()
                    .text(String.valueOf(i + 1))
                    .data(callbackQueryService.buildCallbackData(Command.DICE, new Object[]{"Number:" + (i + 1), "Bet:" + bet}))
                    .build());
        }
        buttons.add(InlineButton.builder()
                .text(backText)
                .data(callbackQueryService.buildCallbackData(Command.DICE, "Number:" + backText))
                .build());
        responseSender.sendMessage(chatId, StringUtils.defaultIfBlank(text, "Выберите число"),
                keyboardBuildService.buildInline(buttons,2), "Markdown");
    }

    private void drawDiceBetButtons(Long chatId, String text) {
        String closeText = diceProperties.getString("button.close.text");
        String[] sums = diceProperties.getStringArray("sums");

        List<InlineButton> buttons = new ArrayList<>();

        Arrays.stream(sums).forEach(sum -> {
            buttons.add(InlineButton.builder()
                    .text(sum + "₽")
                    .data(callbackQueryService.buildCallbackData(Command.DICE, "Bet:" + sum))
                    .build());
        });
        buttons.add(InlineButton.builder()
                .text(closeText)
                .data(callbackQueryService.buildCallbackData(Command.DICE, "Bet:" + closeText))
                .build());
        responseSender.sendMessage(chatId, StringUtils.defaultIfBlank(text, "Выберите ставку:"),
                buildBetButtons(buttons), "Markdown");
    }

    private InlineKeyboardMarkup buildBetButtons(List<InlineButton> buttons){
        InlineKeyboardMarkup first = keyboardBuildService.buildInline(Collections.singletonList(buttons.remove(0)),1);
        InlineKeyboardMarkup last = keyboardBuildService.buildInline(Collections.singletonList(buttons.remove(buttons.size()-1)),1);
        List<List<InlineKeyboardButton>> listInlineButtons = new ArrayList<>(first.getKeyboard());
        listInlineButtons.addAll(keyboardBuildService.buildInline(buttons,2).getKeyboard());
        listInlineButtons.addAll(last.getKeyboard());
        return InlineKeyboardMarkup.builder().keyboard(listInlineButtons).build();
    }

    private boolean isDrawsCommand(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return false;
        Long chatId = updateService.getChatId(update);
        Command drawsCommand = null;
        Command commandFromUpdate = commandService.fromUpdate(update);
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
        startService.process(chatId);
    }

}
