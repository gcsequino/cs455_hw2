package scaling.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ReadWriteUtils {
    public static int read(ByteBuffer buffer, SocketChannel socket) throws IOException{
        int bytesRead = 0;
        while ( buffer.hasRemaining() && bytesRead != -1 ){
              bytesRead += socket.read(buffer);
        }
        buffer.rewind(); //reset buffer position to 0
        return bytesRead;
    }

    public static int writeString(String input, SocketChannel socket) throws IOException{
        writeLength(input.length(), socket); //first write the length of the data
        return write(input.getBytes(), socket); //then write actual data
    }

    private static void writeLength(int length, SocketChannel socket) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(length);
        buffer.flip();
        socket.write(buffer);
        System.out.printf("Wrote length of buffer %d\n", length);
    }
    public static int write(byte[] bytes, SocketChannel socket) throws IOException{
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        System.out.printf("Writing Buffer with val: %s\n", new String(buffer.array()));
        int bytes_written = 0;
        while ( buffer.hasRemaining() ){
              bytes_written = socket.write(buffer);
              if(bytes_written == 0){
                  System.out.println("ERROR - buffer is full, cannot write.");
              }
              System.out.printf("Bytes Written = %s\n", bytes_written);
        }
        return bytes_written;
    }
}
