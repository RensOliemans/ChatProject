package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Created by Rens on 5-4-2016.
 */
public class MultiCast {

    //Dit is een voorbeeld van een join methode

    public void join() {

        try {
            String host = "228.5.6.7";
            int port = 1234;
            String msg = "Hello";
            InetAddress group = InetAddress.getByName(host);
            MulticastSocket s = new MulticastSocket(port);
            s.joinGroup(group);
            DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, port);
            s.send(hi);

            //Get their response

            byte[] buf = new byte[1000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            s.receive(recv);

            s.leaveGroup(group);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
