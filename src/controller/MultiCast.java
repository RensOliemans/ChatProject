package controller;

import com.sun.org.apache.xpath.internal.operations.Mult;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.*;

import view.*;
import controller.*;
import model.*;

/**
 * Created by Rens on 5-4-2016.
 */
public class MultiCast implements Runnable{

    public static final String HOST = "228.0.0.0";
    public static final int PORT = 1234;
//    private String host;
//    private int port;
    private InetAddress group;
    private MulticastSocket s;
    private TCP tcp;
    private int computerNumber;

    public void setup() {
        try {
//            this.host = "228.5.6.7";
//            this.port = 1234;
            this.group = InetAddress.getByName(HOST);
            this.s = new MulticastSocket(PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MultiCast() {
        try {
            this.group = InetAddress.getByName(HOST);
            this.s = new MulticastSocket(PORT);
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
        MultiCast multiCast = new MultiCast();
//        multiCast.setup();
        tcp = new TCP();
        try {
            byte[] buf = new byte[1000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            this.s.receive(recv);
            tcp.handleMessage(recv);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendack(byte[] msg) {
        try {
            DatagramPacket hi = new DatagramPacket(msg, msg.length, group, PORT);
            this.s.send(hi);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPing() {
        try {
            byte[] ping= "PING".getBytes();
            DatagramPacket pingpacket = new DatagramPacket(ping, ping.length, group, PORT);
            this.s.send(pingpacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String msg) {
        tcp = new TCP();
        try {
            List<byte[]> splitmessages = tcp.splitMessages(msg);
            List<byte[]> message = tcp.addSendData(splitmessages);
            for (byte[] packet : message) {
                DatagramPacket hi = new DatagramPacket(packet, packet.length, group, PORT);
                this.s.send(hi);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (tcp.getNotreceived()!=null){
            Map<byte[], byte[]> notreceived = tcp.getNotreceived();
            for (Map.Entry<byte[], byte[]> e : notreceived.entrySet()){
                sendack(e.getValue());
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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


    public void setComputerNumber(int i) {
        this.computerNumber = i;
    }
}
