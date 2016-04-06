package controller;

import model.Security;

import java.util.Scanner;

/**
 * Created by Rens on 5-4-2016.
 */
public class SecurityTest {

    static Security security = new Security();

    public static void main(String[] args) {
        String henk = "Bar12345Bar12345";
        security.setup(henk);
        String encrypted = security.encrypt(new Scanner(System.in).nextLine());
        System.out.println(encrypted);
        String decrypted = security.decrypt(encrypted);
        System.out.println(decrypted);
    }
}
