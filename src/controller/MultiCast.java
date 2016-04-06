package controller;

import com.sun.org.apache.xpath.internal.operations.Mult;

import java.io.IOException;
import java.net.*;
import java.util.*;
import model.*;

/**
 * Created by Rens on 5-4-2016.
 */
public class MultiCast extends Thread{

    //Dit is een voorbeeld van een join methode


    String host;
    int port;
    InetAddress group;
    MulticastSocket s;
    TCP tcp;

    public void setup() {
        try {
            this.host = "228.5.6.7";
            this.port = 1234;
            this.group = InetAddress.getByName(host);
            this.s = new MulticastSocket(port);
            tcp = new TCP();
            System.out.println("setup complete");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void joinGroup() {
        try {
            this.s.joinGroup(group);
            System.out.println("join complete");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive() {
        try {
            byte[] buf = new byte[1000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            this.s.receive(recv);
            tcp.handleMessage(recv);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String msg) {
        try {
            List<byte[]> splitmessages = tcp.splitMessages(msg);
            for (byte[] message : splitmessages) {
                message = tcp.addSendData(message);
                DatagramPacket hi = new DatagramPacket(message, message.length, group, port);
                this.s.send(hi);
                System.out.println("sending complete");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void leave() {
        try {
            this.s.leaveGroup(this.group);
            System.out.println("left group");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) receive();
    }
}
