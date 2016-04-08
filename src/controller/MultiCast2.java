package controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.*;

import model.TCP;

import model.*;

import javax.xml.soap.Text;

/**
 * Created by Rens on 5-4-2016.
 */
public class MultiCast2 implements Runnable{

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

    public MultiCast2() {
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

//    public static void main(String[] args) {
//        MultiCast multiCast = new MultiCast();
//        byte[] recv = {/*header begin*/1, 0, 0, 1, /*data begin*/ 1, 1, 0, 0, 0, 1, /*data end*/ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//
//        int i = recv.length - (TCP.RENSBYTE);
//        while (i-- > 0 && recv[i] == 0) {}
//        byte[] data = new byte[i];
//        System.arraycopy(recv, 0, data, 0, i);
//        for (byte info : data) {
//            System.out.println(info);
//        }
//    }

    public void receive() {

    }

    public void sendack(int destination) {
        byte[] ack = new byte[3];
        ack[0] = (byte) 4; //Value of the 'ack' indication byte
    }

    public void sendPing() {

    }

    private void sendFirst(int destination) {
        byte[] first = new byte[3];
        first[0] = (byte) 3; //Value of the 'first' indication byte
        first[1] = (byte) computerNumber;
        first[2] = (byte) destination;
        DatagramPacket firstPacket = new DatagramPacket(first, first.length, group, PORT);
        try {
            this.s.send(firstPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFinish(int destination) {
        byte[] finish = new byte[3];
        finish[0] = (byte) 5;
        finish[1] = (byte) computerNumber;
        finish[2] = (byte) destination;
        DatagramPacket finishPacket = new DatagramPacket(finish, finish.length, group, PORT);
        try {
            this.s.send(finishPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String msg, int destination) {
        try {
            //Send the entire message, split and with send data from TCP
            List<byte[]> splitmessages = tcp.splitMessages(msg);
            List<byte[]> message = tcp.addSendData(splitmessages);
            for (byte[] packet : message) {
                DatagramPacket messagePacket = new DatagramPacket(packet, packet.length, group, PORT);
                this.s.send(messagePacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //If packets have been lost (not acked after 100ms), resend them until everything has been acked
        while (tcp.getNotreceived()!= null){
            Map<byte[], byte[]> notreceived = tcp.getNotreceived();
            for (Map.Entry<byte[], byte[]> e : notreceived.entrySet()){
                sendMessage(new String(e.getValue()), destination);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String msg, int destination) {
        tcp = new TCP(destination);
        senders.put((byte) destination, tcp);

        //First send the 'First' message
        while (!tcp.getFirstReceived()){
            sendFirst(destination);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //If the receiver received their 'First' message and replied with an ack, send the message
        sendMessage(msg, destination);

        //After the message has been sent, send the 'Finish' message and wait for ack
        while (!tcp.getFinishReceived()){
            sendFinish(destination);
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
