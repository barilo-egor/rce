package tgb.btc.rce.service;

import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.enums.DealType;

import java.math.BigDecimal;

public interface IUserDiscountService {

    boolean isExistByUserPid(Long userPid);

    void applyPersonal(Long chatId, Deal deal, DealType dealType);
}
