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

    public static void main(String[] args) {
        gui.showStartScreen();
        multiCast.setup();
        multiCast.joinGroup();
        while (true) {
            String message = gui.sendMessage();
            multiCast.sendCheat(message);
        }
    }
}