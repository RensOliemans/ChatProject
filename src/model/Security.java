package model;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by Rens on 5-4-2016.
 */
public class Security {
    //TODO: implement the following:
    //The steps to encryption are to share the person's symmetric key via public/private keys:
    //  1.  Send the receiver the symmetric key, encrypted with their public key
    //  2.

    private SecretKey symmetricKey;
    private KeyPair RSAKeyPair;


    private KeyPair generateRSAKeyPair() {
        KeyPair kp = null;
        //Generate a key
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(8192); //keysize 512
            kp = kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return kp;
    }

    public SecretKey generateAESKey() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyGen.generateKey();
    }

    //symmetric encryption with symmetricKey in SecretKey format
    private String encryptsymm(String text, SecretKey secretKey) {
        byte[] raw;
        String encryptedString;
        SecretKeySpec secretKeySpec;
        byte[] encryptText = text.getBytes();
        Cipher cipher = null;
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

    //symmetric decrtyption with symmetricKey in SecretKey format
    private String decryptsymm(String text, SecretKey secretKey) {
        Cipher cipher = null;
        String encryptedString;
        byte[] encryptText = null;
        SecretKeySpec skeySpec;
        try {
            skeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
//            encryptText = Base64.encodeBase64(text.getBytes());
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

    //symmetric encryption with symmetricKey in String format
    private static String encryptsymm(String text, String secretKey) {
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

    //symmetric decryption with symmetricKey in String format
    private String decryptsymm(String text, String secretKey) {
        Cipher cipher;
        String decryptedString;
        byte[] encryptText = null;
        byte[] raw;
        SecretKeySpec skeySpec;
        try {
            raw = Base64.decodeBase64(secretKey);
            skeySpec = new SecretKeySpec(raw, "AES");
            encryptText = Base64.decodeBase64(text);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            decryptedString = new String(cipher.doFinal(encryptText));
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in symmetric key decryption";
        }
        return decryptedString;
    }

    private String encryptRSA(String text, PublicKey publicKey) {
        String xform = "RSA";
        try {
            Cipher cipher = Cipher.getInstance(xform);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return new String(cipher.doFinal(text.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in RSA encryption";
        }
    }

    private String decryptRSA(String text, PrivateKey privateKey) {
        String xform = "RSA";
        try {
            Cipher cipher = Cipher.getInstance(xform);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(text.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in RSA decryption";
        }
    }



    public static void main(String[] args) {
        while (true) {
            long startTime = System.currentTimeMillis();
            Security security = new Security();
            long subTime = System.currentTimeMillis();
            security.symmetricKey = security.generateAESKey();
            System.out.println("Time to create AES key: " + (System.currentTimeMillis() - subTime));
            subTime = System.currentTimeMillis();
            security.RSAKeyPair = security.generateRSAKeyPair();
            System.out.println("Time to create RSA key: " + (System.currentTimeMillis() - subTime));


//            System.out.print("Generated AES key: ");
//            System.out.println(new String(security.symmetricKey.getEncoded()) + "\n");

            System.out.print("Enter the text to be encrypted: ");
            String value = new Scanner(System.in).nextLine();

//            System.out.print("\nEncrypted text via AES: ");
//            subTime = System.currentTimeMillis();
//            String encryptedText = security.encryptsymm(value, security.symmetricKey);
//            System.out.println("Time to encrypt via AES: " + (System.currentTimeMillis() - subTime));
//            System.out.println(encryptedText);
//
//            System.out.print("Decrypted text (symmetric, AES): ");
//            subTime = System.currentTimeMillis();
//            String decryptedText = security.decryptsymm(encryptedText, security.symmetricKey);
//            System.out.println("Time to decrypt via AES: " + (System.currentTimeMillis() - subTime));
//            System.out.println(decryptedText);

            System.out.println("Original plaintext message and symmetric key:");
            String symmKeyString = new String(security.symmetricKey.getEncoded());
            System.out.println("Plaintext: \"" + value + "\"" + ", Key: " + "\"" + symmKeyString + "\"");

            String encryptedMsg = security.encryptsymm(value, security.symmetricKey);
            System.out.println("Encrypted plaintext message via symmetric key: " + encryptedMsg);

            String encrypteSymKey = security.encryptRSA(new String(security.symmetricKey.getEncoded()), security.RSAKeyPair.getPublic());
            System.out.println("symmetric key encrypted in RSA: " + security.encryptRSA(new String(security.symmetricKey.getEncoded()), security.RSAKeyPair.getPublic()));

            String decryptedSymKey = security.decryptRSA(encrypteSymKey, security.RSAKeyPair.getPrivate());
            System.out.println("symmetric key decrypted via RSA: " + decryptedSymKey);




            System.out.println("\n\n\n\n\nWant to try again?");



        }
    }
}
