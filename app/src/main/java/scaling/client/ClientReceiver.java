package scaling.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientReceiver extends Thread {
    private Client client;
    private DataInputStream input_stream;

    public ClientReceiver(Client client, Socket socket) {
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
                recv_hash = DataInputStream.readUTF(input_stream);
            } catch (IOException e) {
                break;
            }

            if(recv_hash != null) client.removeHash(recv_hash);
        }
        System.out.println("[client ~ recv] shutting down");
    }
}
