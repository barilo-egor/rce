package tgb.btc.rce.annotation;

import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.Command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value= ElementType.TYPE)
@Retention(value= RetentionPolicy.RUNTIME)
@Service
public @interface CommandProcessor {
    Command command();
}
