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
    private static final String xform = "RSA/ECB/PKCS1Padding";


    public PublicKey getPublicKey() {
        return RSAKeyPair.getPublic();
    }

    public byte[] getEncryptedAESKey(PublicKey publicKey) {
        return EncryptSecretKey(publicKey);
    }



    private KeyPair generateRSAKeyPair() {
        KeyPair kp = null;
        //Generate a key
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(4096); //keysize 8192 bits, 1024 bytes
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
    private String encryptSymm(String text, SecretKey secretKey) {
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

    //symmetric decryption with symmetricKey in SecretKey format
    private String decryptSymm(String text, SecretKey secretKey) {
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


    private byte[] EncryptSecretKey(PublicKey publicKey) {
        Cipher cipher = null;
        byte[] key = null;

        try {
            cipher = Cipher.getInstance(xform);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            key = cipher.doFinal(symmetricKey.getEncoded());
        } catch (Exception e) {
            System.out.println("exception encoding key: " + e.getMessage());
            e.printStackTrace();
        }
        return key;
    }

    private SecretKey decryptAESKey(byte[] data) {
        SecretKey key = null;
        PrivateKey privateKey = null;
        Cipher cipher = null;
        try {
            privateKey = RSAKeyPair.getPrivate();

            cipher = Cipher.getInstance(xform);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            key = new SecretKeySpec(cipher.doFinal(data), "AES");
        } catch (Exception e) {
            System.out.println("Error decrypting the aes key: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return key;
    }


    private String encryptRSA(String text, PublicKey publicKey) {
        //Uses a public key (so you can send an encrypted AES key with someone else's keys)
        try {
            Cipher cipher = Cipher.getInstance(xform);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return new String(cipher.doFinal(text.getBytes()));
        } catch (Exception e) {
            System.out.println("Exception encoding key: " + e.getMessage());
            e.printStackTrace();
            return "Error in RSA encryption";
        }
    }

    private String decryptRSA(String text) {
        //Uses its own private key
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(xform);
            cipher.init(Cipher.DECRYPT_MODE, RSAKeyPair.getPrivate());
            return new String(cipher.doFinal(text.getBytes()));
        } catch (Exception e) {
            System.out.println("Exception decoding key: " + e.getMessage());
            e.printStackTrace();
            return "Error in RSA decryption";
        }
    }



//    public static void main(String[] args) {
//        long startTime = System.currentTimeMillis();
//        Security security = new Security();
//        long subTime = System.currentTimeMillis();
//        security.symmetricKey = security.generateAESKey();
//        System.out.println("Time to create AES key: " + (System.currentTimeMillis() - subTime)/1000.0 + " seconds");
//        subTime = System.currentTimeMillis();
//        security.RSAKeyPair = security.generateRSAKeyPair();
//        System.out.println("Time to create RSA key: " + (System.currentTimeMillis() - subTime)/1000.0 + " seconds");
//
//        System.out.println("AES key: " + Base64.encodeBase64String(security.symmetricKey.getEncoded()));
//        byte[] encryptedAESKey = security.EncryptSecretKey();
//        System.out.println("Encrypted AES key: " + Base64.encodeBase64String(encryptedAESKey));
//
//        SecretKey decryptedAESKey = security.decryptAESKey(encryptedAESKey);
//        System.out.println("Decrypted AES key: " + Base64.encodeBase64String(decryptedAESKey.getEncoded()));
//
//        System.out.println("Total time: " + (System.currentTimeMillis() - startTime)/1000.0 + " seconds");
//    }
}
