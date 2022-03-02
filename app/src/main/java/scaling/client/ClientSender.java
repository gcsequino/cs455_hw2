package scaling.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientSender extends Thread {
    private DataOutputStream output_stream;
    private boolean finished = false;

    public ClientSender(Socket socket) {
        try {
            output_stream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("[client ~ sender] failed to get output stream from socket");
            e.printStackTrace();
        }
    }

    public void send(byte[] bytes) {
        try {
            output_stream.write(bytes);
        } catch (IOException e) {
            System.out.println("[client ~ sender] failed to send payload");
            e.printStackTrace();
        }
    }

    public void close() {
        this.finished = true;
    }

    @Override
    public void run() {
        while(!finished);
    }
    
}
