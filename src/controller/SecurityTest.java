package controller;

import model.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Scanner;
import org.apache.commons.codec.binary.Base64;

/**
 * Created by Rens on 5-4-2016.
 */
public class SecurityTest {

    static Security security = new Security();


//    public static void main(String[] args) {
//        String secretKey = "XMzDdG4D03CKm2IxIWQw7g==";
//        System.out.println("Enter message to be encrpyted");
//        String message = new Scanner(System.in).nextLine();
//
//
//        String encryptedValue = security.symmetricEncrypt(message, secretKey);
//        System.out.println("Encrypted: " + encryptedValue);
//        String decryptedValue = security.symmetricDecrypt(message, secretKey);
//        System.out.println("Decrypted: " + decryptedValue);
//    }

    public static String symmetricEncrypt(String text, String secretKey) {
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

    public static void main(String[] args) {
        String secretKey = "XMzDdG4D03CKm2IxIWQw7g==";

        while (true) {
            System.out.println("Enter message to be encrpyted");
            String message = new Scanner(System.in).nextLine();

            /**  Ecnryption and decryption of value1 **/
            String encryptedValue = symmetricEncrypt(message, secretKey);
//            String encryptedValue = security.symmetricEncrypt(message, secretKey);
            System.out.println(encryptedValue);
//            String decryptedValue = security.symmetricDecrypt(message, secretKey);
            String decryptedValue = symmetricDecrypt(encryptedValue, secretKey);
            System.out.println(decryptedValue);
            System.out.println();
        }

//        while (true) {
//            System.out.println("Enter message to be encrpyted");
//            String message = new Scanner(System.in).nextLine();
//
//            /**  Ecnryption and decryption of value1 **/
//            String encryptedValue = symmetricEncrypt(message, secretKey);
//            System.out.println(encryptedValue);
//            String decryptedValue = symmetricDecrypt(encryptedValue, secretKey);
//            System.out.println(decryptedValue);
//            System.out.println();
//        }
    }
}

