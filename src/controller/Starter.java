package controller;

import view.GUI;

import java.net.MulticastSocket;
import java.util.Scanner;

/**
 * Created by Rens on 5-4-2016.
 */
public class Starter {
    private static GUI gui;
    private static MultiCast2 multiCast2 = new MultiCast2();

    public static void main(String[] args) {
        //TODO: change this for the GUI
        System.out.println("Enter computer number");
        int computerNumber = new Scanner(System.in).nextInt();

        multiCast2.setComputerNumber(computerNumber);
        Ping ping = new Ping(computerNumber, multiCast2);

//        gui = new GUI(computerNumber, multiCast2);
        Thread receiveThread = new Thread(multiCast2);
        receiveThread.start();

        Thread pingThread = new Thread(ping);
        pingThread.start();

        while (true) {
            System.out.println("Enter destination");
            int destination = new Scanner(System.in).nextInt();
            System.out.println("Enter message");
            multiCast2.send(new Scanner(System.in).nextLine(), destination);
        }

    }
}