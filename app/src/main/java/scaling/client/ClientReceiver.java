package scaling.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientReceiver extends Thread {
    private Client client;
    private DataInputStream input_stream;
    private Socket socket;
    private boolean finished = false;

    public ClientReceiver(Client client, Socket socket) {
        this.socket = socket;
        this.client = client;
        try {
            this.input_stream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("[client ~ receiver] failed to get input stream from socket");
            e.printStackTrace();
        }
    }

    public void setFinished() {
        this.finished = true;
    }
    
    @Override
    public void run() {
        while(!finished) {
            String recv_hash = null;
            try {
                recv_hash = DataInputStream.readUTF(input_stream);
            } catch (IOException e) {
                System.out.println("[client ~ recv] Failed to read hash from server");
                e.printStackTrace();
            }

            if(recv_hash != null) client.removeHash(recv_hash);
        }
    }
}
