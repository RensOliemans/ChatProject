package controller;

import com.sun.org.apache.xpath.internal.operations.Mult;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.*;
import model.TCP;

import view.*;
import controller.*;
import model.*;

/**
 * Created by Rens on 5-4-2016.
 */
public class MultiCast implements Runnable{

    //Dit is een voorbeeld van een join methode

    public static final String HOST = "228.0.0.0";
    public static final int PORT = 1234;
//    String host;
//    int port;
    InetAddress group;
    MulticastSocket s;
    TCP tcp;
    TCPReceive tcpreceive;
    Map<Byte, TCPReceive> receivers = new HashMap<>();
    Map<Byte, TCP> senders = new HashMap<>();
    public int computerNumber;


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

//    public void receive() {
//        try {
//            byte[] buf = new byte[1000];
//            DatagramPacket recv = new DatagramPacket(buf, buf.length);
//            this.s.receive(recv);
//            byte[] data = recv.getData();
//            byte[] syn;
//            data = removeRensByte(data);
//            int i = data.length;
//            switch (data[0]) {
//                // textpacket
//                case 0:
//                    syn = new byte[HEADER];
//                    for (int j = 3; j<HEADER+1; j++){
//                        syn[j-3] = data[j];
//                    }
//                    sendAck(data[1], syn);
//                    byte[] message = new byte[data.length - 3 - HEADER];
//                    gui.printMessage(new String(message));
//                    tcpreceive.received.put()
//                    break;
//                // startpacket
//                case 3:
//                    sendAck(data[1], 0);
//                    TCPReceive tcpreceive= new TCPReceive(data[1]);
//                    break;
//                //ackpacket
//                case 4:
//                    syn = new byte[HEADER];
//                    for (int j = 3; j<HEADER+1; j++){
//                        syn[j-3] = data[j];
//                    }
//
//                    if (syn.equals(0)){
//                        firstReceived = true;
//                    }
//                    else if (syn.equals(1)){
//                        finishReceived = true;
//                    }
//                    else {
//                        notReceived.remove(syn);
//                    }
//                    break;
//                //finishpacket
//                case 5:
//                    sendAck(data[1], 1);
//                    break;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


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

    public void send(String msg, int computernumber) {
        tcp = new TCP(computernumber);
        senders.put((byte) whereto, tcp);
        while (!tcp.getFirstReceived()){
            byte[] first = new byte[2];
            first[0] = (byte) computernumber;
            first[1] = (byte) computernumber;
            sendack(first);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
                byte[] packet = new byte[e.getValue().length+1];
                sendack(e.getValue());
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (!tcp.getFinishReceived()){
            byte[] finish = new byte[2];
            finish[0] = (byte) computernumber;
            finish[1] = (byte) 0;
            sendack(finish);
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
