package scaling.server;

public class Server {
    public Server(){

    }

    public static void main(String[] args){
        if(args.length != 1){
            System.err.println("ERROR: too many args provided to Server -> " + args.length);
            System.err.println("Usage: Server serverPort thread-pool-size batch-size batch-time");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        // int thread_pool_size = Integer.parseInt(args[1]);
        // int batch_size = Integer.parseInt(args[2]);
        // int batch_time_in_seconds = Integer.parseInt(args[3]);
        
        System.out.println("Got server port argument: " + port);
        // System.out.println("Got thread-pool-size: " + thread_pool_size);
        // System.out.println("Got server batch-size: " + batch_size);
        // System.out.println("Got server batch-time: " + batch_time_in_seconds);

        ServerReceiverThread reciever = new ServerReceiverThread(port);
        reciever.start();
    }
}
