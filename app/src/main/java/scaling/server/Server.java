package scaling.server;

import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import scaling.utils.WorkUnit;

public class Server {
    private LinkedBlockingQueue<WorkUnit> ready_queue;
    private ServerReceiverThread receiver;
    private ConcurrentHashMap<String, AtomicInteger> msgs_processed;
    
    private final int statsInterval = 20;


    public Server(int port, int batch_size, int thread_pool_size){
        ready_queue = new LinkedBlockingQueue<>();
        msgs_processed = new ConcurrentHashMap<>();

        receiver = new ServerReceiverThread(this, port, batch_size, msgs_processed);
        receiver.start();

        startThreadPool(thread_pool_size);

        Timer t = new Timer();
        t.scheduleAtFixedRate(new Statistics(msgs_processed, statsInterval), 0, statsInterval * 1000);
    }

    public boolean addToReadyQueue(WorkUnit work){
        return ready_queue.add(work);
    }
    public static void main(String[] args){
        if(args.length != 4){
            System.err.println("ERROR: too many args provided to Server -> " + args.length);
            System.err.println("Usage: Server serverPort thread-pool-size batch-size batch-time");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        int thread_pool_size = Integer.parseInt(args[1]);
        int batch_size = Integer.parseInt(args[2]);
        int batch_time_in_seconds = Integer.parseInt(args[3]);
        
        System.out.println("Got server port argument: " + port);
        System.out.println("Got thread-pool-size: " + thread_pool_size);
        System.out.println("Got server batch-size: " + batch_size);
        System.out.println("Got server batch-time: " + batch_time_in_seconds);

        new Server(port, batch_size, thread_pool_size);
    }

    private void startThreadPool(int thread_pool_size) {
        for(int i  = 0; i < thread_pool_size; i++) {
            WorkerThread worker = new WorkerThread(ready_queue, msgs_processed);
            worker.start();
        }
    }
}
