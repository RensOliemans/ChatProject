package controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import view.*;
import model.*;

/**
 * Created by Rens on 5-4-2016.
 */
public class MultiCast extends Thread {

    //Dit is een voorbeeld van een join methode

    private List<DatagramPacket> packets;
    private String host;
    private int port;
    private InetAddress group;
    private MulticastSocket multicastSocket;
    private TCP tcp;
    private GUI gui;

    public void setup() {
        try {
            this.gui = new GUI();
            String macAddress = gui.getHostName();
            int portNumber = gui.getPortNumber();
            this.host = macAddress;
            this.port = portNumber;
            this.group = InetAddress.getByName(host);
            this.multicastSocket = new MulticastSocket(port);
            this.tcp = new TCP();
            packets = new ArrayList<>();
        } catch (UnknownHostException e) {
            gui.showError("Incorrect host name");
            //recursive call to reenter host name and port number
            setup();
        } catch (IOException e) {
            gui.showError("Incorrect port");
            //recursive call to reenter host name and port number
            setup();
        }
        this.start();
    }

    public void joinGroup() {
        try {
            this.multicastSocket.joinGroup(group);
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
            this.multicastSocket.receive(recv);
            packets.add(recv);
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
                this.multicastSocket.send(hi);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCheat(String msg) {
        try {
            DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, port);
            this.multicastSocket.send(hi);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DatagramPacket getFirstPacket() {
        DatagramPacket packet = null;
        if (!packets.isEmpty()) {
            packet = packets.get(0);
            packets.remove(0);
        }
        return packet;
    }

    public void leave() {
        try {
            this.multicastSocket.leaveGroup(this.group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) receive();
    }
}
