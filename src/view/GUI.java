package view;

import java.util.List;
import java.util.Scanner;

/**
 * Created by Rens on 5-4-2016.
 */
public class GUI {

    private Scanner scanner;

    public GUI() {
        scanner = new Scanner(System.in);
    }


    public void showError(String s) {
        System.out.println("ERROR: " + s);
    }


    public String getHostName() {
        System.out.println("Enter the host name.");
        return scanner.nextLine();
    }

    public int getPortNumber() {
        System.out.println("Enter the port number. Should be an integer between 1 and 65536");
        return scanner.nextInt();
    }

    public void showStartScreen() {
    }

    public String sendMessage() {
        System.out.println("Enter message");
        return scanner.nextLine();
    }

    public void printMessage(List<Byte> goodOrder, int computernumber) {
        for (Byte b : goodOrder) {
            System.out.println(b);
        }
    }

    public void print(String message, int computernumber) {
        System.out.print("Got message: \"");
        
        System.out.print(message + "\", from person with computernumber " + computernumber + "\n");
    }

    public int getDestination() {
        System.out.println("Enter destination");
        return scanner.nextInt();
    }
}