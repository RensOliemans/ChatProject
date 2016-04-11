package model;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.sun.istack.internal.Nullable;
import org.apache.commons.codec.binary.Base64;

/**
 * Created by Rens on 5-4-2016.
 * This is the main security class, which holds
 */
public class Security {
    //TODO: implement the following:
    //The steps to encryption are to share the person's symmetric key via public/private keys:
    //  1.  Send the receiver the symmetric key, encrypted with their public key
    //  2.

    //HashMap with K: computerNumber and V: SecretKey.
    private SecretKey ownSymmetricKey;
    private KeyPair RSAKeyPair;
    private static final String xform = "RSA/ECB/PKCS1Padding";

    public Security() {
        System.out.println("Generating public and private keys...");
        long startTime = System.currentTimeMillis();
        generateRSAKeyPair();
        System.out.println("Successfully created 512 byte RSA keys in : " + (System.currentTimeMillis() - startTime)/1000.0 + " seconds");

    }


    public PublicKey getPublicKey() {
        return RSAKeyPair.getPublic();
    }

    public byte[] getEncryptedAESKey(PublicKey publicKey) {
        return EncryptOwnSecretKey(publicKey);
    }



    private KeyPair generateRSAKeyPair() {
        KeyPair kp = null;
        //Generate a key
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(4096); //keysize: 4096 bits, 512 bytes
            kp = kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return kp;
    }

    public SecretKey generateAESKey(int computerNumber) {
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


    private byte[] EncryptOwnSecretKey(PublicKey publicKey) {
        Cipher cipher = null;
        byte[] key = null;

        try {
            cipher = Cipher.getInstance(xform);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            key = cipher.doFinal(ownSymmetricKey.getEncoded());
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



    public static void main(String[] args) {
        Security security = new Security();
    }
}