package tgb.btc.rce.util;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.exception.BaseException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class CommandProcessorLoader {
    private CommandProcessorLoader() {
    }

    private static Set<Class<?>> commandProcessors = new HashSet<>();

    public static final String PROCESSORS_PACKAGE = "tgb.btc.rce.service.processor";


    public static void scan() {
        Reflections reflections = new Reflections(PROCESSORS_PACKAGE, Scanners.TypesAnnotated);
        commandProcessors = reflections.getTypesAnnotatedWith(CommandProcessor.class);
        commandProcessors.stream()
                .filter(processor -> !hasRunnable(processor))
                .findFirst()
                .ifPresent(processor -> {
                    throw new BaseException("Процессор " + processor.getSimpleName()
                            + " не реализует интерфейс Runnable");
                });
    }

    private static boolean hasRunnable(Class<?> clazz) {
        return Arrays.stream(clazz.getInterfaces())
                .anyMatch(i -> i.isAssignableFrom(Runnable.class));
    }

    public static Class<?> getByCommand(Command command) {
        return commandProcessors.stream()
                .filter(processor -> processor.getAnnotation(CommandProcessor.class).command().equals(command))
                .findFirst()
                .orElseThrow(() -> new BaseException("Не найден процессор для команты " + command.name()));
    }
}
