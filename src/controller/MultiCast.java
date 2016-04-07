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


    String host;
    int port;
    InetAddress group;
    MulticastSocket s;
    TCP tcp;
    TCPReceive tcpreceive;
    Map<Byte, TCPReceive> receivers = new HashMap<>();
    Map<Byte, TCP> senders = new HashMap<>();
    public int computerNumber;


    public void setup() {
        try {
            this.host = "228.5.6.7";
            this.port = 1234;
            this.group = InetAddress.getByName(host);
            this.s = new MulticastSocket(port);
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
            TCP tcpr = null;
            byte[] buf = new byte[1000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            this.s.receive(recv);
            byte[] data = recv.getData();
            for (Map.Entry<Byte, TCP> e : senders.entrySet()){
                if (e.getKey().equals(data[0])){
                    tcpr = e.getValue();
                }
            }
            if ((data[1] == 1 || data[1] == 2 || data[1] == 3 || data[1] == 4) && data[0] == data[1] && data.length == 2){
                tcpreceive = new TCPReceive(data[1]);
                receivers.put(data[1], tcpreceive);
                tcpreceive.handleMessage(recv);
            }
            else if (tcpr == null){
                for (Map.Entry<Byte, TCPReceive> e : receivers.entrySet()){
                    if (e.getKey().equals(data[0])){
                        tcpreceive = e.getValue();
                    }
                }
                tcpreceive.handleMessage(recv);
            }
            else if ((data[1] == 0 && data.length == 2) || (data.length > tcp.HEADER+1 && !(tcp.HEADER == 1 && data[1] == 0 && data[2] == 0))){
                for (Map.Entry<Byte, TCPReceive> e : receivers.entrySet()){
                    if (e.getKey().equals(data[0])){
                        tcpreceive = e.getValue();
                    }
                }
                tcpreceive.handleMessage(recv);
            }
            else {
                tcpr.handleMessage(recv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendack(byte[] msg) {
        try {
            DatagramPacket hi = new DatagramPacket(msg, msg.length, group, port);
            this.s.send(hi);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String msg, int computernumber, int whereto) {
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
                DatagramPacket hi = new DatagramPacket(packet, packet.length, group, port);
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
}
