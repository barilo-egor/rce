package tgb.btc.rce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.handler.impl.message.text.command.settings.payment.merchant.MerchantBindingHandler;
import tgb.btc.rce.service.handler.message.text.ISimpleTextHandler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class MerchantSpringConfig {

    private final IBotMerchantService botMerchantService;

    public MerchantSpringConfig(IBotMerchantService botMerchantService) {
        this.botMerchantService = botMerchantService;
    }

    @Bean
    public List<ISimpleTextHandler> merchantBindingHandlers() {
        return Arrays.stream(Merchant.values())
                .map(merchant -> new MerchantBindingHandler(botMerchantService) {
                    @Override
                    public Merchant getMerchant() {
                        return merchant;
                    }
                })
                .collect(Collectors.toList());
    }
}
