package scaling.utils;

import java.util.Random;

public class RandomBytes {

    public final static int BUFFER_SIZE = 1024*8;
    public static byte[] randBytes() {
        Random r = new Random();
        byte[] bytes = new byte[BUFFER_SIZE];
        r.nextBytes(bytes);
        return bytes;
    }
}
