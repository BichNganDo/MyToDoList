package helper;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.xml.bind.DatatypeConverter;

public class SecurityHelper {

    public static String getMD5Hash(String data) {
        String result = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            return bytesToHex(hash); // make it printable
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private static String bytesToHex(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash);
    }

    public static String genRandomSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[6];
        sr.nextBytes(code);
        String verifier = Base64.getUrlEncoder().withoutPadding().encodeToString(code);
        return verifier;
    }
}
