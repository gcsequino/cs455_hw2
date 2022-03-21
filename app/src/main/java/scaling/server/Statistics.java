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
    private DateTimeFormatter dtf;

    public Statistics(ConcurrentHashMap<String, AtomicInteger> msgsProcessed, int interval) {
        this.msgsProcessed = msgsProcessed;
        this.interval = interval;
        dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    }

    // format:
    // [timestamp] Server Throughput: x messages/s, Active Client Connections: y, Mean Per-
    // client Throughput: p messages/s, Std. Dev. Of Per-client Throughput: q messages/s 

    public void run() {
        SubStats subStats = handleMsgsProcessed();

        float serverThroughput = subStats.totalMessages / interval;
        int activeClients = msgsProcessed.keySet().size();
        double meanPerClientThroughput = subStats.clientThroughputsSum / activeClients;
        double stdDevPerClientThroughput = calcStandardDeviation(subStats.clientThroughputs, meanPerClientThroughput);

        System.out.printf("[%s] Server Throughput: %f messages/s, Active Client Connections: %d, Mean Per-client Throughput: %f messages/s, Std. Dev Of Per-client Throughput: %f messages/s\n",
                        getTimeStamp(), serverThroughput, activeClients, meanPerClientThroughput, stdDevPerClientThroughput);
    }

    private SubStats handleMsgsProcessed() {
        int totalMessages = 0;

        List<Double> clientThroughputs = new ArrayList<>();
        double clientThroughputsSum = 0;

        for(String client : msgsProcessed.keySet()) {
            AtomicInteger clientMessagesAtomic = msgsProcessed.get(client);
            int clientMessages = clientMessagesAtomic.get();

            totalMessages += clientMessages;
            double throughput = clientMessages / interval;
            clientThroughputs.add(throughput);
            clientThroughputsSum += throughput;

            clientMessagesAtomic.set(0);
        }

        return new SubStats(totalMessages, clientThroughputsSum, clientThroughputs);
    }

    private double calcStandardDeviation(List<Double> values, double mean) {
        double stdDev = 0;

        for(Double d : values) {
            stdDev += Math.pow(d - mean, 2);
        }

        return Math.sqrt(stdDev / values.size());
    }

    private String getTimeStamp() {
        return dtf.format(LocalDateTime.now());
    }

    private static class SubStats {
        public final int totalMessages;
        public final double clientThroughputsSum;
        public final List<Double> clientThroughputs;

        public SubStats(int totalMessages, double clientThroughputsSum, List<Double> clientThroughputs) {
            this.totalMessages = totalMessages;
            this.clientThroughputsSum = clientThroughputsSum;
            this.clientThroughputs = clientThroughputs;
        }
    }
    
}
