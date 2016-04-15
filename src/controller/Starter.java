package controller;

/**
 * Created by Rens on 5-4-2016.
 * This is the class with the main method, calls the multiCast constructor which creates a new GUI.
 * Also creates a Ping thread, which pings every 5 seconds.
 * Also creates a Receive thread, which continuously receives data
 */
public class Starter {
    private static MultiCast multiCast;
    private static final int computerNumber = 1;    //Your computerNumber

    public static void main(String[] args) {multiCast = new MultiCast(computerNumber);

        Ping ping = new Ping(computerNumber, multiCast);

        //Starts a new thread so you can receive and send
        Thread receiveThread = new Thread(multiCast);
        receiveThread.start();

        //Starts a new thread so every 5000 milliseconds (5 seconds) a burst of ping packets is sent
        //For routing purposes
        Thread pingThread = new Thread(ping);
        pingThread.start();

    }
}