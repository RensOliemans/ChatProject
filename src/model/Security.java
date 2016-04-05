package model;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

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

    private String encrypt(String message) {
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
