package scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import scaling.utils.ClientInfo;
import scaling.utils.DataUnit;
import scaling.utils.Hash;
import scaling.utils.RandomBytes;
import scaling.utils.ReadWriteUtils;
import scaling.utils.WorkUnit;

public class ServerReceiverThread extends Thread {
    private Selector selector;
    private ServerSocketChannel serverSocket;

    private List<ClientInfo> clients;
    private WorkUnit current_work_unit;
    private Integer batch_size;
    private Server server;

    private Integer port;
    
    public ServerReceiverThread(Server s, int port, int batch_size){
        this.port = port;
        clients = new ArrayList<>();
        this.server = s;
        current_work_unit = new WorkUnit(batch_size);
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
            SelectionKey client_key = client_socket.register(selector, SelectionKey.OP_READ);

            SocketAddress sa = client_socket.getRemoteAddress();
            ClientInfo client_info = new ClientInfo("", -1, null);
            if (sa instanceof InetSocketAddress) { //SocketAddress will be a InetSocketAddress if the socket is connected
              InetSocketAddress address = (InetSocketAddress) sa;
              String client_host_name = address.getHostName();
              Integer client_port = address.getPort();
              client_info = new ClientInfo(client_host_name, client_port, client_socket);
              clients.add(client_info);
            }
            client_key.attach(client_info); //attach the ClientInfo to this socket.
            System.out.println("[server ~ receiver_thread] new client registered -> " + client_info.toString());
        } catch(IOException ioe){
            System.out.println("[server ~ receiver_thread] ERROR - failed to initiate new client conneciton.");
        }
        

    }

    private void addToWorkUnit(byte[] data, ClientInfo client_info){
        DataUnit current = new DataUnit(data, client_info);
        current_work_unit.addDataUnit(current);
        if(current_work_unit.isFull()){
            server.addToReadyQueue(current_work_unit);
            current_work_unit = new WorkUnit(batch_size); //creating new WorkUnit
        }
    }
    private void readData(SelectionKey key, ClientInfo client_info){
        ByteBuffer data = ByteBuffer.allocate(RandomBytes.BUFFER_SIZE); //allocate buffer for 8KB
        SocketChannel client_socket = (SocketChannel) key.channel();
        try{
            int bytes_read = ReadWriteUtils.read(data, client_socket);
            //System.out.printf("%d bytes read.\n", bytes_read);
            if(bytes_read == -1){
                System.out.println("[server ~ receiver_thread] ERROR - " + client_info + " Disconnected from the server.");
                System.out.println("[server ~ receiver_thread] Deregistering " + client_info + " from the server.");
                key.cancel();
            }
            else{
                byte[] data_bytes = data.array();
                addToWorkUnit(data_bytes, client_info);
                String data_hash = Hash.SHA1FromBytes(data_bytes);
                //System.out.printf("Read data with hash [length: %s]%s from client\n", data_hash.length(), data_hash, client_info);

                // REMOVE ME -- Writing back to client for testing purposes 
                try{
                    //System.out.printf("Writing data with hash %s back to client\n\n", data_hash, client_info); 
                    ReadWriteUtils.writeString(data_hash, client_socket);
                    // System.out.printf("Finished writing data with hash %s back to client\n\n", data_hash, client_info); 
                    // System.out.flush();
                } catch(IOException ioe){
                    System.out.println("[server ~ receiver_thread] error writing data to " + client_info);
                    System.out.println("[server ~ receiver_thread] Deregistering " + client_info + " from the server.");
                    key.cancel();
                }
            }
        }catch(IOException ioe){
            System.out.println("[server ~ receiver_thread] error reading data from " + client_info);
        }
    }

    public void run(){
        while(true) {
            try{
                if (selector.selectNow() == 0){
                    //no channels have made an action
                    continue; 
                }
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while(iter.hasNext()){
                    SelectionKey key = iter.next();
                    if(key.isAcceptable()){ //new connection to serverSocket
                        System.out.println("[server ~ receiver_thread] New Connection");
                        registerClient();
                    }else if(key.isReadable()){
                        ClientInfo info = (ClientInfo) key.attachment();
                        System.out.println("[server ~ receiver_thread] Received data from: " + info);
                        System.out.flush();
                        readData(key, info);
                    }
                    iter.remove(); //remove key so that we don't try to do this event again.
                }
            }catch(IOException ioe){
                System.out.println("[server ~ receiver_thread] ERROR - reading from selector");
                System.exit(1);
            }
        }
    }
}
