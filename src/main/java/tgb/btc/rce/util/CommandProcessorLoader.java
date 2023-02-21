package tgb.btc.rce.util;

import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.*;

import java.util.HashSet;
import java.util.Set;

public final class CommandProcessorLoader {
    private CommandProcessorLoader() {
    }

    private static final Set<Class<?>> commandProcessors = new HashSet<>();

    public static final String PROCESSORS_PACKAGE = "tgb.btc.rce.service.processors";


    public static void scan() {
//        Reflections reflections = new Reflections(PROCESSORS_PACKAGE, Scanners.TypesAnnotated);
//        commandProcessors = reflections.getTypesAnnotatedWith(CommandProcessor.class);
        commandProcessors.add(AddContact.class);
        commandProcessors.add(AdditionalVerification.class);
        commandProcessors.add(AdminBack.class);
        commandProcessors.add(AdminPanel.class);
        commandProcessors.add(Back.class);
        commandProcessors.add(BanUnban.class);
        commandProcessors.add(BotMessages.class);
        commandProcessors.add(BotOffed.class);
        commandProcessors.add(BotSettings.class);
        commandProcessors.add(BotVariables.class);
        commandProcessors.add(BuyBitcoin.class);
        commandProcessors.add(ChangeReferralBalance.class);
        commandProcessors.add(ConfirmUserDeal.class);
        commandProcessors.add(Contacts.class);
        commandProcessors.add(CurrentData.class);
        commandProcessors.add(DealReports.class);
        commandProcessors.add(DeleteContact.class);
        commandProcessors.add(DeleteDeal.class);
        commandProcessors.add(DeleteReview.class);
        commandProcessors.add(DeleteUserDeal.class);
        commandProcessors.add(DeleteWithdrawalRequest.class);
        commandProcessors.add(Draws.class);
        commandProcessors.add(EditContacts.class);
        commandProcessors.add(HideWithdrawal.class);
        commandProcessors.add(Lottery.class);
        commandProcessors.add(MailingList.class);
        commandProcessors.add(NewDeals.class);
        commandProcessors.add(NewReviews.class);
        commandProcessors.add(NewWithdrawals.class);
        commandProcessors.add(OffBot.class);
        commandProcessors.add(OnBot.class);
        commandProcessors.add(PartnersReport.class);
        commandProcessors.add(PaymentRequisites.class);
        commandProcessors.add(PaymentTypes.class);
        commandProcessors.add(PublishReview.class);
        commandProcessors.add(QuitAdminPanel.class);
        commandProcessors.add(Referral.class);
        commandProcessors.add(Reports.class);
        commandProcessors.add(Requests.class);
        commandProcessors.add(Roulette.class);
        commandProcessors.add(SellBitcoin.class);
        commandProcessors.add(SendLink.class);
        commandProcessors.add(SendMessages.class);
        commandProcessors.add(SendMessageToUser.class);
        commandProcessors.add(ShareReview.class);
        commandProcessors.add(ShowDeal.class);
        commandProcessors.add(ShowWithdrawalRequest.class);
        commandProcessors.add(Start.class);
        commandProcessors.add(SystemMessages.class);
        commandProcessors.add(UserAdditionalVerification.class);
        commandProcessors.add(UserReferralBalance.class);
        commandProcessors.add(UsersReport.class);
        commandProcessors.add(WithdrawalOfFunds.class);
        commandProcessors.add(UsersDealsReport.class);
        commandProcessors.stream()
                .filter(processor -> !extendsProcessor(processor))
                .findFirst()
                .ifPresent(processor -> {
                    throw new BaseException("Процессор " + processor.getSimpleName()
                            + " не наследует абстрактный класс Processor");
                });
    }

    private static boolean extendsProcessor(Class<?> clazz) {
        return clazz.getSuperclass().equals(Processor.class);
    }

    public static Class<?> getByCommand(Command command) {
        return commandProcessors.stream()
                .filter(processor -> processor.getAnnotation(CommandProcessor.class).command().equals(command))
                .findFirst()
                .orElseThrow(() -> new BaseException("Не найден процессор для команды " + command.name()));
    }
}
