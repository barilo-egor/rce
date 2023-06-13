package tgb.btc.rce.conditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import tgb.btc.rce.enums.AntiSpamType;
import tgb.btc.rce.enums.BotProperties;

public class AntispamCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return !BotProperties.MODULES.getString("anti.spam").equals(AntiSpamType.NONE.name());
    }
}
