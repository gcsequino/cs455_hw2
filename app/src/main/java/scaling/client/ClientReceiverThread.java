package scaling.client;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ClientReceiverThread extends Thread {
    private Client client;
    private DataInputStream input_stream;

    public ClientReceiverThread(Client client, Socket socket) {
        this.client = client;
        try {
            this.input_stream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("[client ~ receiver] failed to get input stream from socket");
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        while(true) {
            String recv_hash = null;
            try {
                byte[] input_size_bytes = new byte[4];
                System.out.println("[client ~ receiver] Waiting to read length from socket");
                input_stream.read(input_size_bytes);
                int input_length = Integer.parseInt(new String(input_size_bytes));
                System.out.printf("[client ~ receiver] waiting for %d number of bytes.", input_length);
                System.out.flush();
                byte[] hash_bytes = new byte[input_length];
                input_stream.readFully(hash_bytes);
                recv_hash = new String(hash_bytes);
                //System.out.printf("[client ~ receiver] Recieved data with [%s]hash %s\n", recv_hash.length(), recv_hash);
            }catch (EOFException eof){
                System.out.printf("[client ~ receiver] EOF -- Server disconnected before full hash bytes were read.\n");
                break;
            } catch (IOException e) {
                System.out.printf("[client ~ receiver] Server disconnected before full hash bytes were read.\n");
                break;
            } 

            if(recv_hash != null) client.removeHash(recv_hash);
        }
        System.out.println("[client ~ recv] shutting down");
        System.out.flush();
    }
}
