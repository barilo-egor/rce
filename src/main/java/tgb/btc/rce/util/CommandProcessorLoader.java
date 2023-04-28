package tgb.btc.rce.util;

import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.TurnOffCurrencyProcessor;
import tgb.btc.rce.service.processors.TurningCurrencyProcessor;
import tgb.btc.rce.service.processors.*;
import tgb.btc.rce.service.processors.bulkdiscounts.UpdateBulkDiscounts;
import tgb.btc.rce.service.processors.paymenttypes.PaymentsTypes;
import tgb.btc.rce.service.processors.paymenttypes.create.CreateNewPaymentType;
import tgb.btc.rce.service.processors.paymenttypes.create.NewPaymentType;
import tgb.btc.rce.service.processors.paymenttypes.create.SaveNamePaymentType;
import tgb.btc.rce.service.processors.paymenttypes.delete.DeletePaymentType;
import tgb.btc.rce.service.processors.paymenttypes.delete.DeletingPaymentType;
import tgb.btc.rce.service.processors.paymenttypes.delete.ShowPaymentTypesForDelete;
import tgb.btc.rce.service.processors.paymenttypes.requisite.create.AskForNewRequisite;
import tgb.btc.rce.service.processors.paymenttypes.requisite.create.NewPaymentTypeRequisite;
import tgb.btc.rce.service.processors.paymenttypes.requisite.create.SaveNewRequisite;
import tgb.btc.rce.service.processors.paymenttypes.requisite.create.ShowPaymentTypesForCreateRequisite;
import tgb.btc.rce.service.processors.paymenttypes.requisite.delete.DeletePaymentTypeRequisite;
import tgb.btc.rce.service.processors.paymenttypes.requisite.delete.DeletingPaymentRequisite;
import tgb.btc.rce.service.processors.paymenttypes.requisite.delete.ShowPaymentTypesForDeleteRequisite;
import tgb.btc.rce.service.processors.paymenttypes.requisite.delete.ShowRequisitesForDelete;
import tgb.btc.rce.service.processors.paymenttypes.turning.ShowPaymentTypesForTurn;
import tgb.btc.rce.service.processors.paymenttypes.turning.TurnPaymentTypes;
import tgb.btc.rce.service.processors.paymenttypes.turning.TurningPaymentType;
import tgb.btc.rce.service.processors.personaldiscounts.buy.AskPersonalBuyDiscountProcessor;
import tgb.btc.rce.service.processors.personaldiscounts.buy.ChangePersonalBuyDiscountProcessor;
import tgb.btc.rce.service.processors.personaldiscounts.buy.SavePersonalBuyDiscountProcessor;
import tgb.btc.rce.service.processors.personaldiscounts.sell.AskPersonalSellDiscountProcessor;
import tgb.btc.rce.service.processors.personaldiscounts.sell.ChangePersonalSellDiscountProcessor;
import tgb.btc.rce.service.processors.personaldiscounts.sell.SavePersonalSellDiscountProcessor;
import tgb.btc.rce.service.processors.referralpercent.AskNewReferralPercent;
import tgb.btc.rce.service.processors.referralpercent.ReferralPercent;
import tgb.btc.rce.service.processors.referralpercent.SaveReferralPercent;

import java.util.HashSet;
import java.util.Set;

public final class CommandProcessorLoader {
    private CommandProcessorLoader() {
    }

    private static final Set<Class<?>> commandProcessors = new HashSet<>();

    public static final String PROCESSORS_PACKAGE = "tgb.btc.rce.service.processors";

    private static final Set<Command> COMMANDS_WITH_STEP = Set.of(
            Command.PERSONAL_BUY_DISCOUNT,
            Command.PERSONAL_SELL_DISCOUNT,
            Command.BULK_DISCOUNTS,
            Command.REFERRAL_PERCENT,
            Command.NEW_PAYMENT_TYPE,
            Command.DELETE_PAYMENT_TYPE,
            Command.NEW_PAYMENT_TYPE_REQUISITE,
            Command.DELETE_PAYMENT_TYPE_REQUISITE,
            Command.TURN_PAYMENT_TYPES,
            Command.CHANGE_MIN_SUM
    );


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
        commandProcessors.add(PaymentTypesOld.class);
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
        commandProcessors.add(ChecksForDate.class);
        commandProcessors.add(DeleteDealAndBlockUserProcessor.class);
        commandProcessors.add(ChangeUsdCourseProcessor.class);
        commandProcessors.add(TurningCurrencyProcessor.class);
        commandProcessors.add(TurnOnCurrencyProcessor.class);
        commandProcessors.add(TurnOffCurrencyProcessor.class);
        commandProcessors.add(DiscountsProcessor.class);
        commandProcessors.add(RankDiscountProcessor.class);
        commandProcessors.add(ChangeRankDiscountProcessor.class);
        commandProcessors.add(SavePersonalBuyDiscountProcessor.class);
        commandProcessors.add(AskPersonalBuyDiscountProcessor.class);
        commandProcessors.add(ChangePersonalBuyDiscountProcessor.class);
        commandProcessors.add(SavePersonalSellDiscountProcessor.class);
        commandProcessors.add(AskPersonalSellDiscountProcessor.class);
        commandProcessors.add(ChangePersonalSellDiscountProcessor.class);
        commandProcessors.add(SendChecksForDate.class);
        commandProcessors.add(UpdateBulkDiscounts.class);
        commandProcessors.add(AskNewReferralPercent.class);
        commandProcessors.add(ReferralPercent.class);
        commandProcessors.add(SaveReferralPercent.class);
        commandProcessors.add(CreateNewPaymentType.class);
        commandProcessors.add(NewPaymentType.class);
        commandProcessors.add(SaveNamePaymentType.class);
        commandProcessors.add(PaymentsTypes.class);
        commandProcessors.add(DeletePaymentType.class);
        commandProcessors.add(DeletingPaymentType.class);
        commandProcessors.add(ShowPaymentTypesForDelete.class);
        commandProcessors.add(InlineDelete.class);
        commandProcessors.add(None.class);
        commandProcessors.add(AskForNewRequisite.class);
        commandProcessors.add(NewPaymentTypeRequisite.class);
        commandProcessors.add(SaveNewRequisite.class);
        commandProcessors.add(ShowPaymentTypesForCreateRequisite.class);
        commandProcessors.add(DeletePaymentTypeRequisite.class);
        commandProcessors.add(DeletingPaymentRequisite.class);
        commandProcessors.add(ShowPaymentTypesForDeleteRequisite.class);
        commandProcessors.add(ShowRequisitesForDelete.class);
        commandProcessors.add(CreateUserDataProcessor.class);
        commandProcessors.add(ShowPaymentTypesForTurn.class);
        commandProcessors.add(TurningPaymentType.class);
        commandProcessors.add(TurnPaymentTypes.class);
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

    public static Class<?> getByCommand(Command command, int step) {
        return commandProcessors.stream()
                .filter(processor -> {
                    CommandProcessor annotation = processor.getAnnotation(CommandProcessor.class);
                    if (Command.START.equals(command) && annotation.command().equals(Command.START)) return true;
                    return annotation.command().equals(command) && (!COMMANDS_WITH_STEP.contains(command) || annotation.step() == step);
                })
                .findFirst()
                .orElseThrow(() -> new BaseException("Не найден процессор для команды " + command.name()));
    }
}
