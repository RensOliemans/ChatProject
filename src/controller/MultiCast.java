package controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.*;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
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
    private InetAddress group;
    private MulticastSocket s;
    private TCP tcp;
    private TCPReceive tcpreceive;
    private Map<Byte, TCPReceive> receivers = new HashMap<>();
    private Map<Byte, TCP> senders = new HashMap<>();
    public int computerNumber;

    public int getComputerNumber() {
        return computerNumber;
    }

//    public void setup() {
//        try {
////            this.host = "228.5.6.7";
////            this.port = 1234;
//            this.group = InetAddress.getByName(HOST);
//            this.s = new MulticastSocket(PORT);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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
//    public void joinGroup() {
        try {
            this.s.joinGroup(group);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MultiCast multiCast = new MultiCast();
        byte[] recv = {/*header begin*/1, 0, 0, 1, /*data begin*/ 1, 1, 0, 0, 0, 1, /*data end*/ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        int i = recv.length - (TCP.RENSBYTE);
        while (i-- > 0 && recv[i] == 0) {}
        byte[] data = new byte[i];
        System.arraycopy(recv, 0, data, 0, i);
        for (byte info : data) {
            System.out.println(info);
        }
    }

    public void receive() {
        try {
            TCP tcpr = new TCP(computerNumber);
            byte[] buf = new byte[1000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            this.s.receive(recv);
            byte[] data = recv.getData();
            //Removal of the last 0's (The DatagramPacket is filled with 1000 bytes. If the message is
            // less than 1000 bytes, 0's are added. They are removed here.
            //Last byte is 'Rens byte', always a 1 so no 0's at the end of the message are removed.
//            System.out.println(new String(recv.getData()));
            int i = recv.getLength();
            while (i-- >= 0 && recv.getData()[i] == 0) {}
            data = new byte[i];
            System.arraycopy(recv.getData(), 0, data, 0, i);

            byte[] DataBytes = new byte[Math.min(data.length, 4)];
            System.arraycopy(data, 0, DataBytes, 0, data.length);
            if (!Arrays.equals(DataBytes, ("PING").getBytes())/* && data[1] != computerNumber*/) {
//                System.out.println("yo 1");
                for (Map.Entry<Byte, TCP> e : senders.entrySet()) {
                    if (e.getKey().equals(data[0])) {
                        tcpr = e.getValue();
                    }
                }
                if ( data.length == 2 && (data[1] == 1 || data[1] == 2 || data[1] == 3 || data[1] == 4) && data[0] == data[1]) {
                    System.out.println("yo 2");
                    tcpreceive = new TCPReceive(data[1]);
                    receivers.put(data[1], tcpreceive);
                    sendack(new byte[] { (byte) computerNumber, data[0], 1});
                /*} else if (tcpr == null) {
                    System.out.println("yo 3");
                    tcpreceive = new TCPReceive(data[0]);
                    for (Map.Entry<Byte, TCPReceive> e : receivers.entrySet()) {
                        if (e.getKey().equals(data[0])) {
                            tcpreceive = e.getValue();
                        }
                    }
                    tcpreceive.handleMessage(recv);*/
                } else if ((data.length == 2 && data[1] == 0) || (data.length > tcp.HEADER + 1 && !(tcp.HEADER == 1 && data[1] == 0 && data[2] == 0))) {
                    System.out.println("yo 4");
                    tcpreceive = new TCPReceive(data[0]);
                    for (Map.Entry<Byte, TCPReceive> e : receivers.entrySet()) {
                        if (e.getKey().equals(data[0])) {
                            tcpreceive = e.getValue();
                        }
                    }
                    tcpreceive.handleMessage(data);
                } else {
                    System.out.println("yo 5");
                    System.out.println(recv.getData());
                    tcpr.handleMessage(data);
                }
            }
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
//        try {
            byte[] ping= "PING".getBytes(); //1 is the 'Rens byte'
            byte[] actualping = new byte[ping.length+1];
            System.arraycopy(ping, 0, actualping, 0, ping.length);
            actualping[actualping.length-1] = 1;
            DatagramPacket pingpacket = new DatagramPacket(actualping, actualping.length, group, PORT);
//            this.s.send(pingpacket);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void send(String msg, int whereto) {
        tcp = new TCP(this.computerNumber);
        senders.put((byte) whereto, tcp);
        while (!tcp.getFirstReceived()){
            byte[] first = new byte[2+TCP.RENSBYTE];
            first[0] = (byte) this.computerNumber; //ascii
            first[1] = (byte) this.computerNumber; //ascii
            first[2] = 1;
            sendack(first);
            receive();
            try {
                Thread.sleep(1000);
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
            finish[0] = (byte) this.computerNumber;
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

    public void setComputerNumber(int computerNumber) {
        this.computerNumber = computerNumber;
    }
}
