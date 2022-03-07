package scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import scaling.utils.ClientInfo;
import scaling.utils.WorkUnit;

public class ServerReceiverThread extends Thread {
    private Selector selector;
    private ServerSocketChannel serverSocket;

    private List<ClientInfo> clients;
    private WorkUnit current_work_unit;

    private Integer port;
    
    public ServerReceiverThread(int port){
        this.port = port;
        clients = new ArrayList<>();
        try{
            selector = Selector.open();
            System.out.println("[server ~ receiver_thread] Opened Selector");
        } catch(IOException ioe){
            System.out.println("[server ~ receiver_thread] ERROR - failed to open selector");
            System.exit(1);
        }
        try{
            serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(port));
            serverSocket.configureBlocking(false);
            System.out.println("[server ~ receiver_thread] Opened ServerSocket");
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("[server ~ receiver_thread] Registered ServerSocket to selector");
        } catch(IOException ioe){
            System.out.println("[server ~ receiver_thread] ERROR - failed to open serverSocket");
            System.exit(1);
        }   
    }

    private void registerClient(){
        try{
            SocketChannel client_socket = serverSocket.accept();
            client_socket.configureBlocking(false);
            client_socket.register(selector, SelectionKey.OP_READ);

            SocketAddress sa = client_socket.getRemoteAddress();
            ClientInfo client = new ClientInfo("", -1, null);
            if (sa instanceof InetSocketAddress) { //SocketAddress will be a InetSocketAddress if the socket is connected
              InetSocketAddress address = (InetSocketAddress) sa;
              String client_host_name = address.getHostString();
              Integer client_port = address.getPort();
              client = new ClientInfo(client_host_name, client_port, client_socket);
              clients.add(client);
            }
            System.out.println("[server ~ receiver_thread] new client registered -> " + client.toString());
        } catch(IOException ioe){
            System.out.println("[server ~ receiver_thread] ERROR - failed to initiate new client conneciton.");
        }
        

    }


    public void run(){
        while(true) {
            try{
                if (selector.selectNow() == 0) continue; //no channels have made an action
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while(iter.hasNext()){
                    SelectionKey key = iter.next();
                    if(key.isAcceptable()){ //new connection to serverSocket
                        System.out.println("[server ~ receiver_thread] New Connection");
                        registerClient();
                    }else if(key.isReadable()){
                        readData(key);
                    }
                    iter.remove(); //remove key so that we don't try to do this event again.
                }
            }catch(IOException ioe){
                System.out.println("[server ~ receiver_thread] ERROR - reading from selector");
            }
        }
    }
}
