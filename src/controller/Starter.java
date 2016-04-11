package controller;

import view.GUIold;

/**
 * Created by Rens on 5-4-2016.
 */
public class Starter {
    private static GUIold GUIold;
    private static MultiCast multiCast = new MultiCast();

    public static void main(String[] args) {
        GUIold = new GUIold(4, multiCast);
//        multiCast.setup();
//        while (true) {
//            String message = GUIold.sendMessage();
//            multiCast.sendCheat(message);
//        }
    }
}