package scaling.server;

public class Server {
    public Server(){

    }

    public static void main(String[] args){
        if(args.length != 1){
            System.err.println("ERROR: too many args provided to Server -> " + args.length);
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        System.out.println("Got server port argument: " + port);

        ServerReceiverThread reciever = new ServerReceiverThread(port);
        reciever.start();
    }
}
