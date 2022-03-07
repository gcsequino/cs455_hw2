package scaling.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

import scaling.utils.Hash;
import scaling.utils.RandomBytes;

public class Client {
    // Server info
    private InetAddress server_host;
    private final int server_port;
    // Operational components
    private Socket socket;
    private final ClientReceiverThread receiver;
    private final ClientSenderThread sender;
    private final List<String> hashList;
    // Stats
    private final int messaging_rate;
    private AtomicInteger sent_count;
    private AtomicInteger received_count;

    public Client(String host, int port, int rate) {
        this.server_port = port;
        this.messaging_rate = rate;
        this.sent_count = new AtomicInteger(0);
        this.received_count = new AtomicInteger(0);
        this.server_host = attemptResolveHost(host);
        System.out.println("[client ~ main] server resolved!");
        this.socket = attemptConnectHost(this.server_host, this.server_port);
        System.out.println("[client ~ main] server connected!");
        this.sender = new ClientSenderThread(socket);
        this.receiver = new ClientReceiverThread(this, socket);
        this.hashList = new LinkedList<>();
    }

    private Socket attemptConnectHost(InetAddress server_host, int server_port) {
        Socket socket = null;
        int attempts = 0;
        do {
            socket = connectHost(server_host, server_port);
            if(socket == null && attempts > 4) {
                System.out.println("[client ~ main] server unavailable, exiting");
                System.exit(1);
            }
            else if(socket == null) {
                System.out.println("[client ~ main] failed to connect to server, retrying");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            attempts++;
        } while(socket == null);
        return socket;
    }

    private Socket connectHost(InetAddress server_host, int server_port) {
        Socket socket = null;
        try {
            socket = new Socket(server_host, server_port);
        } catch (IOException e) {
            return null;
        }
        return socket;
    }

    private InetAddress attemptResolveHost(String host) {
        InetAddress server_host = null;
        int attempts = 0;
        do {
            server_host = resolveHost(host);
            if(server_host == null && attempts > 4) {
                System.out.println("[client ~ main] server unavailable, exiting");
                System.exit(1);
            }
            else if(server_host == null) {
                System.out.println("[client ~ main] failed to resolve to server, retrying");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            attempts++;
        } while(server_host == null);
        return server_host;
    }

    private InetAddress resolveHost(String host) {
        InetAddress addy = null;
        try{
            addy = InetAddress.getByName(host);
        } catch(UnknownHostException e) {
            return null;
        }
        return addy;
    }

    private void startReceiver() {
        System.out.println("[client ~ main] starting receiver thread");
        this.receiver.start();
    }

    private void startSender() {
        System.out.println("[client ~ main] starting sender thread");
        this.sender.start();
    }

    private void sendPayloads() {
        System.out.printf("[client ~ main] sending payloads\n");
        Timer stats = new Timer();
        stats.scheduleAtFixedRate(new StatsTask(sent_count, received_count), (long)0, (long)2000);
        while(true) {
            byte[] rand_bytes = RandomBytes.randBytes();
            String hash = Hash.SHA1FromBytes(rand_bytes);
            sender.addSendQueue(rand_bytes);
            addHash(hash);
            try {
                Thread.sleep(1000 / messaging_rate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized boolean addHash(String hash) {
        sent_count.getAndIncrement();
        return hashList.add(hash);
    }

    public synchronized boolean removeHash(String hash) {
        if(!hashList.remove(hash)){
            System.out.println("[server ~ receiver] Attempting to remove hash that is not in hashQueue");
            System.out.print("HashList: [");
            for(String s : hashList){
                System.out.print(s + " ");
            }
            System.out.println("]");
            System.exit(1);
            return false;
        }
        received_count.getAndIncrement();
        return true;
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
        me.sendPayloads();
        //me.cleanUp();
        System.out.println("[client ~ main] shutting down");
    }
}
