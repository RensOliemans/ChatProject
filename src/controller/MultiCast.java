package controller;

import com.sun.org.apache.xpath.internal.operations.Mult;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import view.*;
import controller.*;
import model.*;

import javax.xml.crypto.Data;

/**
 * Created by Rens on 5-4-2016.
 */
public class MultiCast implements Runnable{

    //Dit is een voorbeeld van een join methode

    private List<DatagramPacket> packets = new ArrayList<>();
    private String host;
    private int port;
    private InetAddress group;
    private MulticastSocket s;
    private TCP tcp;

    public void setup(int portNumber, String macAddress) {
        try {
            this.host = macAddress;
            this.port = portNumber;
            this.group = InetAddress.getByName(host);
            this.s = new MulticastSocket(port);
            tcp = new TCP();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void join() {
        try {
            this.s.joinGroup(group);
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
                this.s.send(hi);
            }
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
            this.s.leaveGroup(this.group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) receive();
    }
}
