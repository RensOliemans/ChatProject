package controller;

import view.GUI;

import java.net.MulticastSocket;
import java.util.Scanner;

/**
 * Created by Rens on 5-4-2016.
 */
public class TestTCP {

    private static GUI gui = new GUI();
    private static MultiCast multiCast = new MultiCast();
    private static Ping ping;

    public static void main(String[] args) {
        multiCast.setup();
        multiCast.join();
        multiCast.setComputerNumber(2);

        Thread receiveThread = new Thread(multiCast);
        receiveThread.start();

        String message = "Hallo";
        multiCast.send(message, 2);

    }
}