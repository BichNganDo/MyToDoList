
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class TestRandom {

    public static String genRandomSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[6];
        sr.nextBytes(code);
        String verifier = Base64.getUrlEncoder().withoutPadding().encodeToString(code);
        return verifier;
    }

    public static void main(String[] args) {
        String genState = genRandomSalt();
        System.out.println(genState);
    }

}
