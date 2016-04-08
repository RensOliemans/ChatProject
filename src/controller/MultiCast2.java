package controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.*;

import model.TCP;

import model.*;

import javax.xml.crypto.Data;
import javax.xml.soap.Text;

import static model.TCP.HEADER;

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
            byte[] data = recv.getData();
            byte[] syn;
            data = removeRensByte(data);
            int i = data.length;
            switch (data[0]) {
                // textpacket
                case 0:
                    syn = new byte[HEADER];
                    for (int j = 3; j<HEADER+1; j++){
                        syn[j-3] = data[j];
                    }
                    sendAck(data[1], syn);
                    byte[] message = new byte[data.length - 3 - HEADER];
                    gui.printMessage(new String(message));
                    tcpreceive.received.put();
                    break;
                // startpacket
                case 3:
                    sendAck(data[1], 0);
                    TCPReceive tcpreceive= new TCPReceive(data[1]);
                    break;
                //ackpacket
                case 4:
                    syn = new byte[HEADER];
                    for (int j = 3; j<HEADER+1; j++){
                        syn[j-3] = data[j];
                    }
                    if (syn.equals(0)){
                        firstReceived = true;
                    }
                    else if (syn.equals(1)){
                        finishReceived = true;
                    }
                    else {
                        notReceived.remove(syn);
                    }
                    break;
                //finishpacket
                case 5:
                    sendAck(data[1], 1);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] removeRensByte(byte[] data) {
        //De 'Rens' byte is vernoemd naar de geniaalste coder die er bestaat.
        //Het houdt in dat bij het receiven een buffer van 1000 bytes komt, maar bij het binnenkomen wordt
        //de message aangevuld met nullen. De rens byte
        byte[] croppedResult;

        int i = data.length - 1;
        while (i-- > 0 && data[i] == 0) {}
        croppedResult = new byte[i];
        System.arraycopy(data, 0, croppedResult, 0, i);

        return croppedResult;
    }

    public void sendAck(int destination, byte[] ackNumber) {
        AckPacket ackPacket = new AckPacket(computerNumber, destination, ackNumber);
        DatagramPacket ack = new DatagramPacket(ackPacket.getAckPacket(), ackPacket.getAckPacket().length, group, PORT);
        try {
            this.s.send(ack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPing() {

    }

    private void sendFirst(int destination) {
        StartPacket firstPacket = new StartPacket(computerNumber, destination);
        DatagramPacket first = new DatagramPacket(firstPacket.getStartPacket(), firstPacket.getStartPacket().length, group, PORT);
        try {
            this.s.send(first);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFinish(int destination) {
        FinishPacket finishPacket = new FinishPacket(computerNumber, destination);
        DatagramPacket finish = new DatagramPacket(finishPacket.getFinishPacket(), finishPacket.getFinishPacket().length, group, PORT);
        try {
            this.s.send(finish);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String msg, int destination) {
        try {
            //Send the entire message, split and with send data from TCP
            int syn = 2; //SYN starts with 1, because SYN 0 is reserved for the ACK of the START message, and
                            //SYN 1 is reserved for the ACK of the FIN message
            List<byte[]> splitmessages = tcp.splitMessages(msg);
            for (byte[] packet : splitmessages) {
                TextPacket toSend = new TextPacket(computerNumber, destination, syn, msg);
                DatagramPacket messagePacket = new DatagramPacket(toSend.getTextPacket(), toSend.getTextPacket().length, group, PORT);
                this.s.send(messagePacket);
                syn++;
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
