package tgb.btc.rce.service.impl.schedule;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.service.process.IReviewProcessService;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

@Service
public class ReviewPublisher {

    private final IReviewProcessService reviewProcessService;

    private final TaskScheduler taskScheduler;

    private ScheduledFuture<?> scheduledFuture;

    private volatile int intervalMinutes;

    public ReviewPublisher(IReviewProcessService reviewProcessService, TaskScheduler taskScheduler,
                           VariablePropertiesReader variablePropertiesReader) {
        this.reviewProcessService = reviewProcessService;
        this.taskScheduler = taskScheduler;
        Integer interval;
        try {
            interval = variablePropertiesReader.getInt(VariableType.REVIEW_PUBLISH_MINUTES_INTERVAL);
        } catch (BaseException e) {
            interval = 10;
            variablePropertiesReader.setProperty(VariableType.REVIEW_PUBLISH_MINUTES_INTERVAL.getKey(), interval.toString());
        }
        this.intervalMinutes = interval;
        scheduleTask();
    }

    private void scheduleTask() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        Instant startTime = Instant.now().plus(Duration.ofSeconds(intervalMinutes));
        scheduledFuture = taskScheduler.scheduleAtFixedRate(
                this::performTask,
                startTime,
                Duration.ofSeconds(intervalMinutes)
        );
    }

    private void performTask() {
        reviewProcessService.publishNext();
    }

    public void updateInterval(int newIntervalMinutes) {
        this.intervalMinutes = newIntervalMinutes;
        scheduleTask();
    }
}
