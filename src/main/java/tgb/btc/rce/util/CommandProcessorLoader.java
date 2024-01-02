package tgb.btc.rce.util;

import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.*;
import tgb.btc.rce.service.processors.apideal.CancelApiDeal;
import tgb.btc.rce.service.processors.apideal.ConfirmApiDeal;
import tgb.btc.rce.service.processors.bulkdiscounts.BulkDiscounts;
import tgb.btc.rce.service.processors.bulkdiscounts.UpdateBulkDiscounts;
import tgb.btc.rce.service.processors.calculator.InlineQueryCalculator;
import tgb.btc.rce.service.processors.calculator.NoneCalculator;
import tgb.btc.rce.service.processors.paymenttypes.PaymentsTypes;
import tgb.btc.rce.service.processors.paymenttypes.create.CreateNewPaymentType;
import tgb.btc.rce.service.processors.paymenttypes.create.FiatCurrencyNewPaymentType;
import tgb.btc.rce.service.processors.paymenttypes.create.NewPaymentType;
import tgb.btc.rce.service.processors.paymenttypes.create.SaveNamePaymentType;
import tgb.btc.rce.service.processors.paymenttypes.delete.DeletePaymentType;
import tgb.btc.rce.service.processors.paymenttypes.delete.DeletingPaymentType;
import tgb.btc.rce.service.processors.paymenttypes.delete.FiatCurrencyDeletePaymentType;
import tgb.btc.rce.service.processors.paymenttypes.delete.ShowPaymentTypesForDelete;
import tgb.btc.rce.service.processors.paymenttypes.minsum.*;
import tgb.btc.rce.service.processors.paymenttypes.requisite.create.AskForNewRequisite;
import tgb.btc.rce.service.processors.paymenttypes.requisite.create.FiatCurrencyCreateRequisite;
import tgb.btc.rce.service.processors.paymenttypes.requisite.create.NewPaymentTypeRequisite;
import tgb.btc.rce.service.processors.paymenttypes.requisite.create.ShowPaymentTypesForCreateRequisite;
import tgb.btc.rce.service.processors.paymenttypes.requisite.delete.DeletingPaymentRequisite;
import tgb.btc.rce.service.processors.paymenttypes.requisite.delete.FiatCurrenciesDeleteRequisite;
import tgb.btc.rce.service.processors.paymenttypes.requisite.delete.ShowPaymentTypesForDeleteRequisite;
import tgb.btc.rce.service.processors.paymenttypes.requisite.delete.ShowRequisitesForDelete;
import tgb.btc.rce.service.processors.paymenttypes.requisite.dynamic.FiatCurrencyDynamicRequisite;
import tgb.btc.rce.service.processors.paymenttypes.requisite.dynamic.TurnDynamicRequisites;
import tgb.btc.rce.service.processors.paymenttypes.requisite.dynamic.TurningDynamic;
import tgb.btc.rce.service.processors.paymenttypes.turning.FiatCurrencyTurnPaymentType;
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
import tgb.btc.rce.service.processors.spamban.KeepSpamBan;
import tgb.btc.rce.service.processors.spamban.NewSpamBans;
import tgb.btc.rce.service.processors.spamban.ShowSpamBannedUser;
import tgb.btc.rce.service.processors.spamban.SpamUnban;

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
            Command.CHANGE_MIN_SUM,
            Command.TURN_DYNAMIC_REQUISITES,
            Command.CHOOSING_FIAT_CURRENCY,
            Command.NONE_CALCULATOR,
            Command.INLINE_QUERY_CALCULATOR,
            Command.INLINE_CALCULATOR
    );


    public static void scan() {
//        Reflections reflections = new Reflections(PROCESSORS_PACKAGE, Scanners.TypesAnnotated);
//        commandProcessors = reflections.getTypesAnnotatedWith(CommandProcessor.class);
        commandProcessors.add(AddContact.class);
        commandProcessors.add(AdditionalVerification.class);
        commandProcessors.add(AdminBack.class);
        commandProcessors.add(Back.class);
        commandProcessors.add(BanUnban.class);
        commandProcessors.add(BotMessages.class);
        commandProcessors.add(BotVariables.class);
        commandProcessors.add(ChangeReferralBalance.class);
        commandProcessors.add(ConfirmUserDeal.class);
        commandProcessors.add(CurrentData.class);
        commandProcessors.add(DealReports.class);
        commandProcessors.add(DeleteContact.class);
        commandProcessors.add(DeleteDeal.class);
        commandProcessors.add(DeleteReview.class);
        commandProcessors.add(DeleteUserDeal.class);
        commandProcessors.add(DeleteWithdrawalRequest.class);
        commandProcessors.add(HideWithdrawal.class);
        commandProcessors.add(Lottery.class);
        commandProcessors.add(MailingList.class);
        commandProcessors.add(NewDeals.class);
        commandProcessors.add(NewReviews.class);
        commandProcessors.add(NewWithdrawals.class);
        commandProcessors.add(OffBot.class);
        commandProcessors.add(OnBot.class);
        commandProcessors.add(PartnersReport.class);
        commandProcessors.add(PublishReview.class);
        commandProcessors.add(QuitAdminPanel.class);
        commandProcessors.add(Referral.class);
        commandProcessors.add(SendLink.class);
        commandProcessors.add(SendMessageToUser.class);
        commandProcessors.add(ShareReview.class);
        commandProcessors.add(ShowDeal.class);
        commandProcessors.add(ShowWithdrawalRequest.class);
        commandProcessors.add(Start.class);
        commandProcessors.add(SystemMessages.class);
        commandProcessors.add(UserAdditionalVerification.class);
        commandProcessors.add(UserReferralBalance.class);
        commandProcessors.add(UsersReport.class);
        commandProcessors.add(UserInformation.class);
        commandProcessors.add(WithdrawalOfFunds.class);
        commandProcessors.add(UsersDealsReport.class);
        commandProcessors.add(ChecksForDate.class);
        commandProcessors.add(DeleteDealAndBlockUserProcessor.class);
        commandProcessors.add(TurningCurrencyProcessor.class);
        commandProcessors.add(TurnOnCurrencyProcessor.class);
        commandProcessors.add(TurnOffCurrencyProcessor.class);
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
        commandProcessors.add(AskForNewRequisite.class);
        commandProcessors.add(NewPaymentTypeRequisite.class);
        commandProcessors.add(ShowPaymentTypesForCreateRequisite.class);
        commandProcessors.add(DeletingPaymentRequisite.class);
        commandProcessors.add(ShowPaymentTypesForDeleteRequisite.class);
        commandProcessors.add(ShowRequisitesForDelete.class);
        commandProcessors.add(CreateUserDataProcessor.class);
        commandProcessors.add(ShowPaymentTypesForTurn.class);
        commandProcessors.add(TurningPaymentType.class);
        commandProcessors.add(TurnPaymentTypes.class);
        commandProcessors.add(AskForMinSum.class);
        commandProcessors.add(ChangeMinSum.class);
        commandProcessors.add(SaveMinSum.class);
        commandProcessors.add(ShowTypesForMinSum.class);
        commandProcessors.add(TurnDynamicRequisites.class);
        commandProcessors.add(TurningDynamic.class);
        commandProcessors.add(TurnRankDiscount.class);
        commandProcessors.add(TurningRankDiscount.class);
        commandProcessors.add(LotteryReport.class);
        commandProcessors.add(BulkDiscounts.class);
        commandProcessors.add(CaptchaProcessor.class);
        commandProcessors.add(DeleteUser.class);
        commandProcessors.add(ShowSpamBannedUser.class);
        commandProcessors.add(KeepSpamBan.class);
        commandProcessors.add(SpamUnban.class);
        commandProcessors.add(NewSpamBans.class);
        commandProcessors.add(MakeAdmin.class);
        commandProcessors.add(FiatCurrencyNewPaymentType.class);
        commandProcessors.add(FiatCurrencyCreateRequisite.class);
        commandProcessors.add(FiatCurrencyDynamicRequisite.class);
        commandProcessors.add(FiatCurrencyDeletePaymentType.class);
        commandProcessors.add(SaveFiatCurrencyMinSum.class);
        commandProcessors.add(FiatCurrencyTurnPaymentType.class);
        commandProcessors.add(FiatCurrenciesDeleteRequisite.class);
        commandProcessors.add(BuyBitcoin.class);
        commandProcessors.add(SellBitcoin.class);
        commandProcessors.add(DealProcessor.class);
        commandProcessors.add(NoneCalculator.class);
        commandProcessors.add(InlineQueryCalculator.class);
        commandProcessors.add(InlineCalculator.class);
        commandProcessors.add(Help.class);
        commandProcessors.add(ShowApiDeal.class);
        commandProcessors.add(NewApiDeals.class);
        commandProcessors.add(ConfirmApiDeal.class);
        commandProcessors.add(CancelApiDeal.class);
        commandProcessors.add(WebAdminPanelProcessor.class);
        commandProcessors.add(TurningDeliveryTypeProcessor.class);
        commandProcessors.add(TurningProcessDeliveryProcessor.class);
        commandProcessors.add(BackupBD.class);
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
