package test;

import model.Security;
import org.junit.Before;
import org.junit.Test;
import view.GUI;

import javax.crypto.SecretKey;

import static junit.framework.TestCase.assertEquals;

/**
 * Test class for Security
 * Created by Rens on 15-4-2016.
 */
public class SecurityTest {

    Security security1;
    Security security2;
    private GUI gui; //This is only so the Security constructor can be called

    @Before
    public void setUp() {
        this.security1 = new Security(gui);
        this.security2 = new Security(gui);
        int computerNumber1 = 1;
        int computerNumber2 = 2;
    }

    @Test
    public void RSAEncryption() {
        //First generate a symmetric key so you have a symmetric key with person 2
        security1.generateAESKey(2);
        byte[] encryptedKey = security1.EncryptSecretKey(security1.getPublicKey(), security1.getSymmetricKey(2));
        SecretKey decryptedKey = security1.decryptAESKey(encryptedKey);
        assertEquals(security1.getSymmetricKey(2), decryptedKey);
    }

    @Test
    public void encryptSymmetrically() {
        String textToEncrypt = "This is the best project ever!";
        System.out.println("Text to encrypt: " + textToEncrypt);

        //Generate a key (generated by person 1 for connection between 1-2)
        security1.generateAESKey(2);
        //Save the key generated by person 1
        security2.addSymmetricKey(1, security1.getSymmetricKey(2));

        //Encrypt the text
        String encryptedText = security1.encryptSymm(textToEncrypt, security1.getSymmetricKey(2));
        System.out.println("Encrypted text: " + encryptedText);

        //Decrypt the text, with person 1's key
        String decryptedText = security1.decryptSymm(encryptedText, security1.getSymmetricKey(2));
        System.out.println("Text decrypted by person 1: " + decryptedText);

        assertEquals(textToEncrypt, decryptedText);

        //Now decrypt the text with the key that person 2 has
        String decryptedText2 = security2.decryptSymm(encryptedText, security2.getSymmetricKey(1));
        System.out.println("Text decrypted by person 2: " + decryptedText2);

        assertEquals(textToEncrypt, decryptedText2);

        assertEquals(decryptedText, decryptedText2);
    }

}
