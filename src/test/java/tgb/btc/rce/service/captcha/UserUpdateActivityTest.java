package tgb.btc.rce.service.captcha;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserUpdateActivityTest {

    @Test
    void addMessage() {
        UserUpdateActivity activity = new UserUpdateActivity();
        long t1 = System.currentTimeMillis();
        activity.addMessage(t1);
        assertEquals(1, activity.getMessageCount());
        long t2 = System.currentTimeMillis();
        activity.addMessage(t2);
        assertEquals(2, activity.getMessageCount());
    }

    @Test
    void getMessageCount() {
        UserUpdateActivity activity = new UserUpdateActivity();
        assertEquals(0, activity.getMessageCount());
        long t1 = System.currentTimeMillis();
        activity.addMessage(t1);
        long t2 = System.currentTimeMillis();
        activity.addMessage(t2);
        long t3 = System.currentTimeMillis();
        activity.addMessage(t3);
        assertEquals(3, activity.getMessageCount());
    }

    @Test
    public void testCleanOldMessages_noOldMessages() {
        UserUpdateActivity activity = new UserUpdateActivity();

        long currentTime = System.currentTimeMillis();
        long timeWindowMs = 10000;

        activity.addMessage(currentTime - 5000);
        activity.addMessage(currentTime - 2000);

        activity.cleanOldMessages(currentTime, timeWindowMs);

        // No messages should be removed
        assertEquals(2, activity.getMessageCount());
    }

    @Test
    public void testCleanOldMessages_someOldMessages() {
        UserUpdateActivity activity = new UserUpdateActivity();

        long currentTime = System.currentTimeMillis();
        long timeWindowMs = 10000;

        activity.addMessage(currentTime - 15000);
        activity.addMessage(currentTime - 5000);
        activity.addMessage(currentTime - 2000);

        activity.cleanOldMessages(currentTime, timeWindowMs);

        assertEquals(2, activity.getMessageCount());
    }

    @Test
    public void testCleanOldMessages_allOldMessages() {
        UserUpdateActivity activity = new UserUpdateActivity();

        long currentTime = System.currentTimeMillis();
        long timeWindowMs = 10000;

        activity.addMessage(currentTime - 20000);
        activity.addMessage(currentTime - 15000);

        activity.cleanOldMessages(currentTime, timeWindowMs);

        assertEquals(0, activity.getMessageCount());
    }

    @Test
    public void testCleanOldMessages_emptyQueue() {
        UserUpdateActivity activity = new UserUpdateActivity();

        long currentTime = System.currentTimeMillis();
        long timeWindowMs = 10000;

        activity.cleanOldMessages(currentTime, timeWindowMs);

        assertEquals(0, activity.getMessageCount());
    }

    @Test
    public void testCleanOldMessages_edgeCaseExactTime() {
        UserUpdateActivity activity = new UserUpdateActivity();

        long currentTime = System.currentTimeMillis();
        long timeWindowMs = 10000;

        activity.addMessage(currentTime - timeWindowMs);
        activity.addMessage(currentTime - timeWindowMs + 1);

        activity.cleanOldMessages(currentTime, timeWindowMs);

        assertEquals(2, activity.getMessageCount());
    }
}