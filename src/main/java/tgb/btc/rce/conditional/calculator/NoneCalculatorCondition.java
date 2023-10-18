package tgb.btc.rce.conditional.calculator;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import tgb.btc.rce.enums.CalculatorType;

public class NoneCalculatorCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return CalculatorType.NONE.isCurrent();
    }

}
