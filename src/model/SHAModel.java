package model;

import com.google.gson.Gson;
import helper.HttpHelper;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

public class SHAModel {

    private static final String SECRET_KEY = "DTBN";
    private static final long _1_DAY_IN_MILI = 86400000;
    public static SHAModel INSTANCE = new SHAModel();

    public SHAModel() {
    }

    public String encrypt(String strToEncrypt, String myKey) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] key = myKey.getBytes("UTF-8");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public String decrypt(String strToDecrypt, String myKey) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] key = myKey.getBytes("UTF-8");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public byte[] getSHA(String input) throws NoSuchAlgorithmException {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public String toHexString(byte[] hash) {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    public String genAccessToken(String email, int id) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("id", id);
            jsonObject.put("expire", System.currentTimeMillis() + _1_DAY_IN_MILI);

            String payload = INSTANCE.encrypt(jsonObject.toString(), SECRET_KEY);
            String sign = INSTANCE.toHexString(INSTANCE.getSHA(jsonObject.toString()));

            return payload + ":" + sign;
        } catch (Exception e) {
            return "";
        }
    }

    public JSONObject vertifyAccessToken(String accessToken) throws NoSuchAlgorithmException {
        String[] splitAccessToken = accessToken.split(":");
        if (splitAccessToken.length == 2) {
            String payload = INSTANCE.decrypt(splitAccessToken[0], SECRET_KEY);
            String sign = INSTANCE.toHexString(INSTANCE.getSHA(payload));
            String signOrgi = splitAccessToken[1];

            if (sign.equals(signOrgi)) {
                JSONObject result = new JSONObject(payload);
                if (result.optLong("expire") > System.currentTimeMillis()) {
                    return result;
                }
            }
        }
        return null;

    }

    public int getIdUser(HttpServletRequest req) {
        try {
            String cookie = HttpHelper.getCookie(req, "authen");
            JSONObject vertifyAccessToken = INSTANCE.vertifyAccessToken(cookie);
            if (vertifyAccessToken != null) {
                Object idObject = vertifyAccessToken.get("id");
                int idUser = Integer.parseInt(idObject.toString());
                return idUser;
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }


}
