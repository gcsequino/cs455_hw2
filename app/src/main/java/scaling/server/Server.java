package scaling.server;

import java.util.concurrent.ConcurrentLinkedQueue;

import scaling.utils.WorkUnit;

public class Server {
    ConcurrentLinkedQueue<WorkUnit> ready_queue;
    ServerReceiverThread receiver;

    public Server(int port, int batch_size, int thread_pool_size){
        ready_queue = new ConcurrentLinkedQueue<>();

        receiver = new ServerReceiverThread(this, port, batch_size);
        receiver.start();

        startThreadPool(thread_pool_size);
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
            WorkerThread worker = new WorkerThread(ready_queue);
            worker.start();
        }
    }
}
