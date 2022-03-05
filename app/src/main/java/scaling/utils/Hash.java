package scaling.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    public static String SHA1FromBytes(byte[] bytes) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte[] hash = digest.digest(bytes);
            BigInteger hashInt = new BigInteger(1, hash);
            return hashInt.toString(16);
        } catch(NoSuchAlgorithmException e) {
            System.out.println("[hash] invalid hash instance");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}