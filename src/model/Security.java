package model;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import view.GUI;

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
    private Map<Integer, SecretKey> symmetricKeys;
    private GUI gui;
    private KeyPair RSAKeyPair;
    private static final String xform = "RSA/ECB/PKCS1Padding";

    public Security() {
        this.symmetricKeys = new HashMap<Integer, SecretKey>();
        System.out.println("Generating public and private keys...");
        long startTime = System.currentTimeMillis();
        generateRSAKeyPair();
        System.out.println("Successfully created 512 byte RSA keys in : " + (System.currentTimeMillis() - startTime)/1000.0 + " seconds");
    }


    public PublicKey getPublicKey() {
        return RSAKeyPair.getPublic();
    }

    public byte[] getEncryptedAESKey(PublicKey publicKey, SecretKey AESKey) {
        return EncryptSecretKey(publicKey, AESKey);
    }

    public SecretKey getSymmetricKey(int computerNumber) {
        if (symmetricKeys.containsKey(computerNumber)) {
            return symmetricKeys.get(computerNumber);
        }
        return null;
    }

    public void addSymmetricKey(int computerNumber, SecretKey secretKey) {
        if (!this.symmetricKeys.containsKey(computerNumber)) {
            this.symmetricKeys.put(computerNumber, secretKey);
        } else {
            System.out.println("symmetric key already exists for this person, see Security.addSymmetricKey(..)");
        }
    }



    private void generateRSAKeyPair() {
        KeyPair kp = null;
        //Generate a key
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(4096); //keysize: 4096 bits, 512 bytes
            kp = kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            gui.showError("NoSuchAlgoritmException in generateRSAKeyPair(). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        }
        this.RSAKeyPair = kp;
    }

    public void generateAESKey(int computerNumber) {
        KeyGenerator keyGen = null;
        try {

            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            gui.showError("NoSuchAlgoritmException in generateAESKey(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        }
        this.symmetricKeys.put(computerNumber, keyGen.generateKey());
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
        } catch (NoSuchAlgorithmException e) {
            gui.showError("NoSuchAlgoritmException in encryptSymm(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
            return "Error";
        } catch (InvalidKeyException e) {
            gui.showError("InvalidKeyException in encryptSymm(..). " +
                    "Ask Rens. " +
            "\nError message: " + e.getMessage());
            return "Error";
        } catch (NoSuchPaddingException e) {
            gui.showError("NoSuchPaddingException in encryptSymm(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
            return "Error";
        } catch (BadPaddingException e) {
            gui.showError("BadPaddingException in encryptSymm(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
            return "Error";
        } catch (IllegalBlockSizeException e) {
            gui.showError("IllegalBlockSizeException in encryptSymm(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
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
        } catch (NoSuchAlgorithmException e) {
            gui.showError("NoSuchAlgoritmException in decryptSymm(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
            return "Error";
        } catch (InvalidKeyException e) {
            gui.showError("InvalidKeyException in decryptSymm(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
            return "Error";
        } catch (NoSuchPaddingException e) {
            gui.showError("NoSuchPaddingException in decryptSymm(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
            return "Error";
        } catch (BadPaddingException e) {
            gui.showError("BadPaddingException in decryptSymm(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
            return "Error";
        } catch (IllegalBlockSizeException e) {
            gui.showError("IllegalBlockSizeException in decryptSymm(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
            return "Error";
            //If this happens, check if String text (input) is actually a decrypted text.
            // Debug this method and see how this method is called
        }
        return encryptedString;
    }


    private byte[] EncryptSecretKey(PublicKey publicKey, SecretKey symmetricKey) {
        Cipher cipher = null;
        byte[] key = null;

        try {
            cipher = Cipher.getInstance(xform);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            key = cipher.doFinal(symmetricKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            gui.showError("NoSuchAlgorithmException in EncryptSecretKey(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (InvalidKeyException e) {
            gui.showError("InvalidKeyException in EncryptSecretKey(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            gui.showError("NoSuchPaddingException in EncryptSecretKey(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (BadPaddingException e) {
            gui.showError("BadPaddingException in EncryptSecretKey(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            gui.showError("IllegalBlockSizeException in EncryptSecretKey(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        }
        return key;
    }

    public SecretKey decryptAESKey(byte[] data) {
        //This method decrypts an encrypted AES key with its own private key
        SecretKey key = null;
        PrivateKey privateKey = null;
        Cipher cipher = null;
        try {
            privateKey = RSAKeyPair.getPrivate();

            cipher = Cipher.getInstance(xform);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            key = new SecretKeySpec(cipher.doFinal(data), "AES");
        } catch (NoSuchAlgorithmException e) {
            gui.showError("NoSuchAlgorithmException in decryptAESKey(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (InvalidKeyException e) {
            gui.showError("InvalidKeyException in decryptAESKey(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            gui.showError("NoSuchPaddingException in decryptAESKey(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (BadPaddingException e) {
            gui.showError("BadPaddingException in decryptAESKey(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            gui.showError("IllegalBlockSizeException in decryptAESKey(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        }
        return key;
    }


    private String encryptRSA(String text, PublicKey publicKey) {
        //Uses a public key (so you can send an encrypted AES key with someone else's keys)
        try {
            Cipher cipher = Cipher.getInstance(xform);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return new String(cipher.doFinal(text.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            gui.showError("NoSuchAlgorithmException in encryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (InvalidKeyException e) {
            gui.showError("InvalidKeyException in encryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            gui.showError("NoSuchPaddingException in encryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (BadPaddingException e) {
            gui.showError("BadPaddingException in encryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            gui.showError("IllegalBlockSizeException in encryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        }
        return "Error";
    }

    private String decryptRSA(String text) {
        //Uses its own private key
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(xform);
            cipher.init(Cipher.DECRYPT_MODE, RSAKeyPair.getPrivate());
            return new String(cipher.doFinal(text.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            gui.showError("NoSuchAlgorithmException in decryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (InvalidKeyException e) {
            gui.showError("NoSuchAlgorithmException in decryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            gui.showError("NoSuchAlgorithmException in decryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (BadPaddingException e) {
            gui.showError("NoSuchAlgorithmException in decryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            gui.showError("NoSuchAlgorithmException in decryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        }
        return "Error";
    }



    public static void main(String[] args) {
        Security security = new Security();
    }
}