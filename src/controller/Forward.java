package controller;

import model.Packet;

import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;

/**
 * Created by coen on 5-4-2016.
 */
public class Forward extends Thread {

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected Packet input_packet = null;
    protected boolean moreQuotes = true;

    public Forward() throws IOException {
        this("QuoteServerThread");
    }

    public Forward(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);

        try {
            in = new BufferedReader(new FileReader("one-liners.txt"));
        } catch (FileNotFoundException e) {
            System.err.println("Could not open quote file. Serving time instead.");
        }
    }

    public void run() {

        while (moreQuotes) {
            try {
                byte[] buf = new byte[256];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                // figure out response
                String dString = null;
                if (in == null) {
                    System.out.println("in == null");
                    dString = new Date().toString();
                } else {
                    System.out.println("in != null");
                    dString = getNextQuote();
                }

                buf = dString.getBytes();

                // send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                System.out.println("address sender is: " + address);
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                moreQuotes = false;
            }
        }
        System.out.println("closing socket");
        socket.close();
    }

    protected String getNextQuote() {
        String returnValue = null;
        try {
            if ((returnValue = in.readLine()) == null) {
                in.close();
                moreQuotes = false;
                returnValue = "No more quotes. Goodbye.";
            }
        } catch (IOException e) {
            returnValue = "IOException occurred in server.";
        }
        return returnValue;
    }



}
