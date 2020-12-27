package logic.helpers;

import com.student.adminweather.R;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import data.App;

public class EncryptionUtil {

    private final static String HEX = "0123456789ABCDEF";

    public static String safeEncrypt(String strNormalText) {
        try {
            return encrypt(strNormalText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strNormalText;
    }

    public static String safeDecryption(String strEncryptedText) {
        try {
            return decrypt(strEncryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strEncryptedText;
    }


    private static String encrypt(String cleartext) throws Exception {
        byte[] rawKey = getRawKey();
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    private static String decrypt(String encrypted) throws Exception {
        byte[] rawKey = getRawKey();
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

    private static byte[] getRawKey() {
        SecretKey skey = generateKey();
        return skey.getEncoded();
    }


    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(clear);
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(encrypted);
    }

    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    private static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (byte b : buf) {
            appendHex(result, b);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    private static SecretKey generateKey() {
        String pass = App.getAppCtx().getResources().getString(R.string.encrypt_key);
        return new SecretKeySpec(pass.getBytes(), "AES");
    }
}
