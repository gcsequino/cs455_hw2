package scaling.utils;

import java.util.Random;

public class RandomBytes {
    public static byte[] randBytes() {
        Random r = new Random();
        byte[] bytes = new byte[8192];
        r.nextBytes(bytes);
        return bytes;
    }
}
