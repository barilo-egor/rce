package tgb.btc.lib.conditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import tgb.btc.lib.enums.CalculatorType;

public class InlineQueryCalculatorCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return CalculatorType.INLINE_QUERY.isCurrent();
    }
}
