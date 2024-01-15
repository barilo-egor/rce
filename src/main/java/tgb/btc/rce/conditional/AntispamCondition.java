package tgb.btc.rce.conditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.rce.enums.AntiSpamType;

public class AntispamCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return !PropertiesPath.MODULES_PROPERTIES.getString("anti.spam").equals(AntiSpamType.NONE.name());
    }
}
