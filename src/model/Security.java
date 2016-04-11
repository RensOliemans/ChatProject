package model;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by Rens on 5-4-2016.
 */
public class Security {
    //TODO: Find way encrypt the symmetric keys with public/private keys

    private static String secretKeyString = "XMzDdG4D03CKm2IwIWQc7g==";
    private static SecretKey secretKey;

    public static SecretKey generateKey() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey key = keyGen.generateKey();
        return key;
    }

    private static String symmetricEncrypt(String text, SecretKey secretKey) {
        byte[] raw;
        String encryptedString;
        SecretKeySpec secretKeySpec;
        byte[] encryptText = text.getBytes();
        Cipher cipher;
        try {
            secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedString = Base64.encodeBase64String(cipher.doFinal(encryptText));
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
        return encryptedString;
    }

    private static String symmetricDecrypt(String text, SecretKey secretKey) {
        Cipher cipher;
        String encryptedString;
        byte[] encryptText = null;
//        byte[] raw;
        SecretKeySpec skeySpec;
        try {
//            raw = Base64.decodeBase64(secretKey);
            skeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
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


    public String encrypt(String text) {
        return symmetricEncrypt(text, this.secretKey);
    }
    public String decrypt(String text, String secretKey) {
        return symmetricDecrypt(text, secretKey);
    }


    private static String symmetricEncrypt(String text, String secretKey) {
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

    private static String symmetricDecrypt(String text, String secretKey) {
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

    public static void main(String[] args) {
        secretKey = generateKey();
        System.out.print("Generated secret key: ");
        System.out.println(new String(secretKey.getEncoded()));

        System.out.print("Enter the text to be encrypted: ");
        String value = new Scanner(System.in).nextLine();

        System.out.print("Encrypted text: ");
//        String encryptedText = symmetricEncrypt(value, secretKey);
        String encryptedText = symmetricEncrypt(value, new String(secretKey.getEncoded()));
        System.out.println(encryptedText);

        System.out.print("Decrypted text: ");
//        String decryptedText = symmetricDecrypt(value, secretKey);
        String decryptedText = symmetricDecrypt(value, new String(secretKey.getEncoded()));
        System.out.println(decryptedText);


    }



}
