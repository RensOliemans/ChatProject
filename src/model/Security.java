package model;

//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
//import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by Rens on 5-4-2016.
 */
public class Security {

    //What you want to do is encrypt every message.
    //TODO: create way to handle incoming messages

    //TODO: create way to share session keys

    //TODO: create way to sign digitally

    private String key;
    private Key aesKey;
    private Cipher cipher;

    public void setup(String key) {
        try {
            //Create key and cipher
            this.key = key;
            this.aesKey = new SecretKeySpec(key.getBytes(), "AES");
            this.cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public String symmetricEncrypt(String text, String secretKey) {
        byte[] raw;
        String encryptedString;
        SecretKeySpec skeySpec;
        byte[] encryptText = text.getBytes();
        Cipher cipher;
        try {
            raw = Base64.decodeBase64(secretKey);
            skeySpec = new SecretKeySpec(raw, "AES");
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            encryptedString = Base64.encodeBase64String(cipher.doFinal(encryptText));
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
        return encryptedString;
    }

    public static String symmetricDecrypt(String text, String secretKey) {
        Cipher cipher;
        String encryptedString;
        byte[] encryptText = null;
        byte[] raw;
        SecretKeySpec skeySpec;
        try {
            raw = Base64.decodeBase64(secretKey);
            skeySpec = new SecretKeySpec(raw, "AES");
            encryptText = Base64.decodeBase64(text);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            encryptedString = new String(cipher.doFinal(encryptText));
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
        return encryptedString;
    }






    public String encrypt(String message) {
        try {
            //Encrypt the message
            this.cipher.init(Cipher.ENCRYPT_MODE, this.aesKey);
            byte[] encrypted = cipher.doFinal(message.getBytes());
            return new String(encrypted);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return "Couldn't encrypt the message: " + message;
    }

    public String decrypt(String message) {
        try {
            this.cipher.init(Cipher.DECRYPT_MODE, this.aesKey);
            String decrypted = new String(cipher.doFinal(message.getBytes()));
            return decrypted;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return "Couldn't decrypt the message: " + message;
    }

}
