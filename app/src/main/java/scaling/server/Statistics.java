package scaling.server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Statistics extends TimerTask {
    private ConcurrentHashMap<String, AtomicInteger> msgsProcessed;
    private int interval; // how often this gets called in seconds

    public Statistics(ConcurrentHashMap<String, AtomicInteger> msgsProcessed, int interval) {
        this.msgsProcessed = msgsProcessed;
        this.interval = interval;
    }

    // format:
    // [timestamp] Server Throughput: x messages/s, Active Client Connections: y, Mean Per-
    // client Throughput: p messages/s, Std. Dev. Of Per-client Throughput: q messages/s 

    public void run() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();  

        int activeClients = 0;
        int totalMessages = 0;

        List<Double> clientThroughputs = new ArrayList<>();
        double clientThroughputsSum = 0;

        for(String client : msgsProcessed.keySet()) {
            activeClients++;

            AtomicInteger clientMessagesAtomic = msgsProcessed.get(client);
            int clientMessages = clientMessagesAtomic.get();

            totalMessages += clientMessages;
            double throughput = clientMessages / interval;
            clientThroughputs.add(throughput);
            clientThroughputsSum += throughput;

            clientMessagesAtomic.set(0);
        }

        double meanPerClientThroughput = clientThroughputsSum / activeClients;
        double stdDevPerClientThroughput = 0;
        
        for(Double throughput : clientThroughputs) {
            stdDevPerClientThroughput += Math.pow(throughput - meanPerClientThroughput, 2);
        }

        float serverThroughput = totalMessages / interval;

        System.out.printf("[%s] Server Throughput: %f messages/s, Active Client Connections: %d, Mean Per-client Throughput: %f messages/s, Std. Dev Of Per-client Throughput: %f messages/s\n",
                        dtf.format(now), serverThroughput, activeClients, meanPerClientThroughput, stdDevPerClientThroughput);
    }
    
}
