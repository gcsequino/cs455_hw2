package scaling.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import scaling.utils.Hash;

public class TestServer {
    public static void main(String[] args) {
        System.out.println("Running test server");
        ServerSocket server = null;
        try {
            server = new ServerSocket(6969);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Socket socket = null;
        try {
            socket = server.accept();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Connected to client");
        InputStream input_stream = null;
        try {
            input_stream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        DataOutputStream output_stream = null;
        try {
            output_stream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        while(socket.isConnected() && !socket.isClosed()) {
            byte[] bytes = new byte[8000];
            try {
                input_stream.read(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String hash = Hash.SHA1FromBytes(bytes);
            System.out.println("Recieved msg, hash: " + hash);
            hash = null;
            try {
                output_stream.writeUTF(hash);
            } catch (IOException e) {
                break;
            }
        }
    }
}