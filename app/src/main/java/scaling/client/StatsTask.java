package scaling.client;

import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsTask extends TimerTask {

    private AtomicInteger sent_count;
    private AtomicInteger received_count;

    public StatsTask(AtomicInteger sent_count, AtomicInteger received_count) {
        this.sent_count = sent_count;
        this.received_count = received_count;
    }

    private void printStats() {
        System.out.printf("[%d] Total Sent Count: %s, Total Received Count: %s\n",
            System.nanoTime(), sent_count.toString(), received_count.toString());
    }

    @Override
    public void run() {
        printStats();
        System.out.flush();
    }
    
}
