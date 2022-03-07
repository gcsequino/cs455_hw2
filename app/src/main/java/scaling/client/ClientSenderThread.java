package scaling.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ClientSenderThread extends Thread {
    private DataOutputStream output_stream;
    private Queue<byte[]> sendQueue;

    public ClientSenderThread(Socket socket) {
        try {
            output_stream = new DataOutputStream(socket.getOutputStream());
            sendQueue = new LinkedList<>();
        } catch (IOException e) {
            System.out.println("[client ~ sender] failed to get output stream from socket");
            e.printStackTrace();
        }
    }

    public void send(byte[] bytes) throws IOException {
        output_stream.write(bytes);
    }

    public synchronized void addSendQueue(byte[] bytes) {
        sendQueue.add(bytes);
    }

    public synchronized byte[] removeSendQueue() {
        return sendQueue.remove();
    }

    public synchronized boolean isSendQueueEmpty() {
        return sendQueue.isEmpty();
    }

    @Override
    public void run() {
        while(true){
            if(!isSendQueueEmpty()) {
                try {
                    send(removeSendQueue());
                } catch (IOException e) {
                    break;
                }
            }
        }
        System.out.println("[client ~ sender] shutting down");
    }
    
}
