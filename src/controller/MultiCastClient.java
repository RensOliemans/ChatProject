package controller;

import java.io.IOException;
import java.net.*;

/**
 * Created by coen on 6-4-2016.
 */
public class MultiCastClient {

    //TODO: what does this class do?

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Usage: java MultiCastClient <coen>");
            System.out.println(args.length);
            return;
        }

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

        // send request
        byte[] buf = new byte[256];
        InetAddress address = InetAddress.getByName(args[0]);
        System.out.println("address sender is: " + address);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Received: " + received);

        System.out.println("closing socket");
        socket.close();
    }

}
