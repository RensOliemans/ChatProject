package model;

import org.apache.commons.codec.binary.Base64;
import view.GUI;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rens on 5-4-2016.
 * This is the main security class, which holds
 */
public class Security {
    //HashMap with K: computerNumber and V: SecretKey. This is because every sender-receiver connection has a separate SecretKey
    private Map<Integer, SecretKey> symmetricKeys;
    private final GUI gui; //GUI to call errors that happen during encryption/decryption
    private KeyPair RSAKeyPair; //RSAKeyPair. Every MultiCast (person) has a separate Security object, so every person has a public and private key
    private static final String xform = "RSA/ECB/PKCS1Padding"; //The instance used for the public/private key (RSA)

    /**
     * The constructor of the Security object. Also generates RSA public and private keys
     * @param gui GUI gui, a GUI object so it can show errors
     */
    public Security(GUI gui) {
        this.gui = gui;
        this.symmetricKeys = new HashMap<>();
        generateRSAKeyPair(); //generates the RSA public and private keys
    }

    /**
     * Returns the PublicKey of the RSAKeyPair
     * @return PublicKey, the publicKey of the person.
     */
    public PublicKey getPublicKey() {
        return RSAKeyPair.getPublic();
    }


    /**
     * Gets the symmetric key that belongs to a computerNumber.
     * @param computerNumber int computerNumber, the computerNumber that belongs to a symmetric key
     * @return SecretKey symmetricKey, the key that belongs to a person (with computerNumber)
     */
    public SecretKey getSymmetricKey(int computerNumber) {
        if (symmetricKeys.containsKey(computerNumber)) {
            return symmetricKeys.get(computerNumber);
        }
        return null;
    }

    /**
     * Adds a symmetric key with a computernumber to the hashMap this.symmetricKeys
     * @param computerNumber int computerNumber, the computerNumber that belongs to the new key
     * @param secretKey SecretKey secretKey, the new key, belonging to a computernumber
     */
    public void addSymmetricKey(int computerNumber, SecretKey secretKey) {
        if (!this.symmetricKeys.containsKey(computerNumber)) {
            this.symmetricKeys.put(computerNumber, secretKey);
        } else {
            this.symmetricKeys.replace(computerNumber, secretKey);
        }
    }

    /**
     * Generates RSA key pairs of 512 bits.
     * Can give a NoAlgorithmException exception when the instance is incorrect.
     */
    private void generateRSAKeyPair() {
        KeyPair kp = null;
        //Generate a key
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(512); //keysize: 512 bits, 64 bytes
            kp = kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            gui.showError("NoSuchAlgoritmException in generateRSAKeyPair(). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        }
        this.RSAKeyPair = kp;
    }

    /**
     * Generates a symmetric key belonging to a computerNumber.
     * Can give a NoSuchAlgorithmException when the instance is incorrect
     * @param computerNumber int computerNumber, the computerNumber to whom the key belongs
     */
    public void generateAESKey(int computerNumber) {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            gui.showError("NoSuchAlgoritmException in generateAESKey(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        }
        assert keyGen != null;
        this.symmetricKeys.put(computerNumber, keyGen.generateKey());
    }

    /**
     * Encrypts a piece of text with a symmetric key.
     * Can give several Exceptions
     * @param text String text, the text to be encrypted
     * @param secretKey SecretKey secretKey, the key to encrypt the text with
     * @return String encryptedText, the encrypted text
     */
    public String encryptSymm(String text, SecretKey secretKey) {
        String encryptedString;
        SecretKeySpec secretKeySpec;
//        byte[] encryptText = text.getBytes();
        Cipher cipher;
        try {

            secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            encryptedString = Base64.encodeBase64String(encrypted);
            return encryptedString;
//            encryptedString = Base64.encodeBase64String(cipher.doFinal(encryptText));

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
//        return encryptedString;
    }

    /**
     * Decrypts a piece of text with a SecretKey.
     * Can throw several exceptions
     * @param text String text, encrypted text to be decrypted
     * @param secretKey SecretKey secretKey, the key to decrypt the data with
     * @return String decryptedText, the decrypted text.
     */
    public String decryptSymm(String text, SecretKey secretKey) {
        Cipher cipher = null;
        String encryptedString;
        byte[] encryptText = null;
        SecretKeySpec skeySpec;
        try {
            skeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            encryptText = Base64.decodeBase64(text);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//            encryptedString = new String(Base64.encodeBase64(cipher.doFinal(encryptText)))  ;
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
                    "Ask Rens . " + "cipher length: " + cipher.getAlgorithm().getBytes().length + " encryptLength: " + encryptText.length +
                    "\nError message: " + e.getMessage());
            return "Error";
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            gui.showError("IllegalBlockSizeException in decryptSymm(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
            return "Error";
            //If this happens, check if String text (input) is actually a decrypted text.
            // Debug this method and see how this method is called
        }
        return encryptedString;
    }

    /**
     * This method encrypts the symmetric key with a public key
     * @param publicKey PublicKey publicKey, the key to encrypt the symmetric keys with
     * @param symmetricKey SecretKey secrteKey, the symmetricKey that has to be encrypted
     * @return byte[] secretKey, the encrypted symmetric key
     */
    public byte[] EncryptSecretKey(PublicKey publicKey, SecretKey symmetricKey) {
        Cipher cipher;
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

    /**
     * A method that decrypts a symmetric key with its own private key
     * @param data byte[] data, the encrypted key
     * @return SecretKey secretKey, the decrypted secret key
     */
    public SecretKey decryptAESKey(byte[] data) {
        //This method decrypts an encrypted AES key with its own private key
        SecretKey key = null;
        PrivateKey privateKey;
        Cipher cipher;
        try {
            privateKey = RSAKeyPair.getPrivate();

            cipher = Cipher.getInstance(xform);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            key = new SecretKeySpec(cipher.doFinal(data), "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            gui.showError("NoSuchAlgorithmException in decryptAESKey(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            gui.showError("InvalidKeyException in decryptAESKey(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            gui.showError("NoSuchPaddingException in decryptAESKey(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (BadPaddingException e) {
            e.printStackTrace();
            gui.showError("BadPaddingException in decryptAESKey(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            gui.showError("NoSuchAlgorithmException in encryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            gui.showError("InvalidKeyException in encryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            gui.showError("NoSuchPaddingException in encryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (BadPaddingException e) {
            e.printStackTrace();
            gui.showError("BadPaddingException in encryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            gui.showError("IllegalBlockSizeException in encryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        }
        return "Error";
    }

    private String decryptRSA(String text) {
        //Uses its own private key
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(xform);
            cipher.init(Cipher.DECRYPT_MODE, RSAKeyPair.getPrivate());
            return new String(cipher.doFinal(text.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            gui.showError("NoSuchAlgorithmException in decryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (InvalidKeyException e) {
            gui.showError("InvalidKeyException in decryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            gui.showError("NoSuchPaddingException in decryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (BadPaddingException e) {
            gui.showError("BadPaddingException in decryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            gui.showError("IllegalBlockSizeException in decryptRSA(..). " +
                    "Ask Rens. " +
                    "\nError message: " + e.getMessage());
        }
        return "Error";
    }
}