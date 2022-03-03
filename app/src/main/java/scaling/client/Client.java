package scaling.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import scaling.utils.Hash;
import scaling.utils.RandomBytes;

public class Client {
    // Server info
    private InetAddress server_host;
    private final int server_port;
    // Operational components
    private Socket socket;
    private final ClientReceiver receiver;
    private final ClientSender sender;
    private final List<String> hashList;
    // Stats
    private final int messaging_rate;
    private int sent_count;
    private int received_count;
    private long interval_start_time;

    public Client(String host, int port, int rate) {
        this.server_port = port;
        this.messaging_rate = rate;
        try{
            this.server_host = InetAddress.getByName(host);
        } catch(UnknownHostException e) {
            System.out.println("[client ~ main] ERROR: could not resolve host, exiting");
            e.printStackTrace();
            System.exit(1);
        }
        try {
            this.socket = new Socket(server_host, server_port);
        } catch (IOException e) {
            System.out.println("[client ~ main] ERROR: could not open socket to server, exiting");
            e.printStackTrace();
            System.exit(1);
        }
        this.sender = new ClientSender(socket);
        this.receiver = new ClientReceiver(this, socket);
        this.hashList = new LinkedList<>();
    }

    private void startReceiver() {
        System.out.println("[client ~ main] starting receiver thread");
        this.receiver.start();
    }

    private void startSender() {
        System.out.println("[client ~ main] starting sender thread");
        this.sender.start();
    }

    private void sendPayloads(int count) {
        System.out.printf("[client ~ main] sending %d payloads\n", count);
        this.interval_start_time = System.nanoTime();
        for(int i = 0; i < count; i++) {
            if(System.nanoTime() - this.interval_start_time >= 2e10) {
                this.interval_start_time = System.nanoTime();
                printStats();
            }
            byte[] rand_bytes = RandomBytes.randBytes();
            String hash = Hash.SHA1FromBytes(rand_bytes);
            addHash(hash);
            sender.send(rand_bytes);
            try {
                Thread.sleep(1000 / messaging_rate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized boolean addHash(String hash) {
        sent_count++;
        return hashList.add(hash);
    }

    public synchronized boolean removeHash(String hash) {
        received_count++;
        return hashList.add(hash);
    }

    private void printStats() {
        System.out.printf("[%f] Total Sent Count: %d, Total Received Count: %d\n",
            System.nanoTime(), this.sent_count, this.received_count);
    }

    private void cleanUp() {
        try{
            this.socket.close();
        } catch(IOException e) {
            System.out.println("[client ~ main] failed to close socket");
        }
    }

    private static void checkUsage(String[] args) {
        if(args.length != 3) {
            System.out.println("[client ~ main] invalid usage, expected: Client <server-host> <server_port> <messaging_rate>");
            System.exit(1);
        }
    }

    private static String parseHost(String[] args) {
        InetAddress host = null;
        try {
            host = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.out.println("[client ~ main] received invald server_host, exiting");
            System.exit(1);
        }
        if(host != null) return host.getHostName();
        return null;
    }

    private static int parsePort(String[] args) {
        int port = Integer.parseInt(args[1]);
        if(port < 1024 || port > 65535) {
            System.out.println("[client ~ main] received invalid port, exiting");
            System.exit(1);
            return -1;
        }
        return port;
    }

    private static int parseRate(String[] args) {
        return Integer.parseInt(args[2]);
    }
    public static void main(String[] args) {
        // Collect params
        System.out.println("[client ~ main] initializing");
        checkUsage(args);
        String server_host = parseHost(args);
        int server_port = parsePort(args);
        int messaging_rate = parseRate(args);
        System.out.printf("[client ~ main] starting with server_host = %s, server_port = %d, messaging_rate = %d\n", server_host, server_port, messaging_rate);
        
        // Client operations
        Client me = new Client(server_host, server_port, messaging_rate);
        me.startReceiver();
        me.startSender();
        me.sendPayloads(250000);
        me.cleanUp();
    }
}
