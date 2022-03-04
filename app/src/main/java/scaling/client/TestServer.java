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
        DataInputStream input_stream = null;
        try {
            input_stream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        DataOutputStream output_stream = null;
        try {
            output_stream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(1);
        }

        while(true) {
            byte[] bytes = new byte[8000];
            try {
                input_stream.readFully(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String hash = Hash.SHA1FromBytes(bytes);
            System.out.println("Recieved msg, hash: " + hash);
            try {
                if(hash != null) output_stream.writeUTF(hash);
                else break;
            } catch (IOException e) {
                break;
            }
            hash = null;
        }
    }
}