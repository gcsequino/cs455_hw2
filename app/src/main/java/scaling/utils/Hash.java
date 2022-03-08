package scaling.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    public static final int HASH_SIZE = 40;
    private static String zeroPad(String number, int pad_size){
        return String.format("%" + pad_size + "s", number).replace(' ', '0');
    }
    public static String SHA1FromBytes(byte[] bytes) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte[] hash = digest.digest(bytes);
            BigInteger hashInt = new BigInteger(1, hash);
            String hashStr = hashInt.toString(16);
            return zeroPad(hashStr, HASH_SIZE);
        } catch(NoSuchAlgorithmException e) {
            System.out.println("[hash] invalid hash instance");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
