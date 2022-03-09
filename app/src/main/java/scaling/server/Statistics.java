package scaling.server;

import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Statistics extends TimerTask {
    private ConcurrentHashMap<String, AtomicInteger> msgsProcessed;
    
    public Statistics(ConcurrentHashMap<String, AtomicInteger> msgsProcessed) {
        this.msgsProcessed = msgsProcessed;
    }

    public void run() {
        int totalMessages = 0;
        for(String client : msgsProcessed.keySet()) {
            AtomicInteger msgs_for_this_port = msgsProcessed.get(client);
            System.out.printf("Processed %d messages for client %s\n", msgs_for_this_port.get(), client);
            totalMessages += msgs_for_this_port.get();
            msgs_for_this_port.set(0);
        }
        System.out.printf("%d total messages processed in the last 20 seconds.\n", totalMessages);
    }
    
}
