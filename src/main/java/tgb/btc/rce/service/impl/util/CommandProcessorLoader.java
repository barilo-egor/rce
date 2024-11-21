package tgb.btc.rce.service.impl.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.util.ICommandProcessorLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public final class CommandProcessorLoader implements ICommandProcessorLoader {

    private List<Processor> processors;

    private final Map<Command, Map<Integer, Processor>> processorMap = new HashMap<>();

    @Autowired
    public void setProcessors(List<Processor> processors) {
        this.processors = processors;
    }

    @PostConstruct
    private void init() {
        processors.forEach(processor
                -> {
            CommandProcessor commandProcessorAnnotation = ClassUtils.getUserClass(processor).getAnnotation(CommandProcessor.class);
            if (processorMap.containsKey(commandProcessorAnnotation.command())) {
                processorMap.get(commandProcessorAnnotation.command()).put(commandProcessorAnnotation.step(), processor);
            } else {
                Map<Integer, Processor> stepProcessor = new HashMap<>();
                stepProcessor.put(commandProcessorAnnotation.step(), processor);
                processorMap.put(commandProcessorAnnotation.command(), stepProcessor);
            }
        });
        log.info("Загружены процессоры.");
    }

    public Processor getByCommand(Command command, int step) {
        try {
            if (Command.START.equals(command)) {
                return processorMap.get(Command.START).get(0);
            }
            Map<Integer, Processor> stepProcessors = processorMap.get(command);
            if (Objects.isNull(stepProcessors)) {
                return null;
            }
            if (stepProcessors.size() > 1)
                return processorMap.get(command).get(step);
            return stepProcessors.values().iterator().next();
        } catch (NullPointerException e) {
            log.error("Не найден процессор для команды {} по шагу {}.", command.name(), step);
            throw new BaseException("Не найден процессор для команды " + command.name() + " по шагу " + step);
        }
    }
}
