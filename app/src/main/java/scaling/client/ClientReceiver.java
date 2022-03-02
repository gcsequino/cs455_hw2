package scaling.client;

import java.net.Socket;

public class ClientReceiver extends Thread {
    private Client client;

    public ClientReceiver(Client client, Socket socket) {
        this.client = client;
    }
    
    @Override
    public void run() {

    }
}
