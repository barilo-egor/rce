package tgb.btc.rce.service.captcha;

import java.util.LinkedList;
import java.util.Queue;

public class UserUpdateActivity {
    private final Queue<Long> messageTimestamps = new LinkedList<>();

    public void addMessage(long timestamp) {
        messageTimestamps.add(timestamp);
    }

    public int getMessageCount() {
        return messageTimestamps.size();
    }

    public void cleanOldMessages(long currentTime, long timeWindowMs) {
        while (!messageTimestamps.isEmpty() && currentTime - messageTimestamps.peek() > timeWindowMs) {
            messageTimestamps.poll();
        }
    }
}
