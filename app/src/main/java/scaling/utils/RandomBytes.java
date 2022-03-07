package scaling.utils;

import java.util.Random;

public class RandomBytes {

    public final static int BUFFER_SIZE = RandomBytes.BUFFER_SIZE;
    public static byte[] randBytes() {
        Random r = new Random();
        byte[] bytes = new byte[BUFFER_SIZE];
        r.nextBytes(bytes);
        return bytes;
    }
}
