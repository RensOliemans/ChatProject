package controller;

import view.GUI;

import java.net.MulticastSocket;
import java.util.Scanner;

/**
 * Created by Rens on 5-4-2016.
 */
public class Starter {
    private static GUI gui = new GUI();
    private static MultiCast multiCast = new MultiCast();
    private static Ping ping;

    public static void main(String[] args) {
        if (args.length != 1) {
            gui.showError("Wrong usage, should be \"int computernumber\"");
            System.exit(0);
        }

        ping = new Ping(Integer.parseInt(args[0]));

//        multiCast.setup();
        multiCast.join();
        multiCast.setComputerNumber(Integer.parseInt(args[0]));

        Thread receiveThread = new Thread(multiCast);
        receiveThread.start();

        Thread pingThread = new Thread(ping);
        pingThread.start();

        while (true) {
            String message = gui.sendMessage();
            multiCast.send(message);
        }
    }
}