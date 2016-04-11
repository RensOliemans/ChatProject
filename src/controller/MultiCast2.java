package controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import model.Sender;

import model.*;
import view.GUI;

import javax.xml.crypto.Data;
import javax.xml.soap.Text;

import static com.oracle.jrockit.jfr.ContentType.Bytes;

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
    private Sender sender;
    private Receiver receiver;
    private GUI gui;
    private Map<Byte, Receiver> receivers = new HashMap<>();
    //Byte is the destination, sender is you
    private Map<Byte, Sender> senders = new HashMap<>();
    public int computerNumber;
    public static final int DATASIZE=128;
    public static final int HEADER = 1;
    private int synint;


    public int getComputerNumber() {
        return computerNumber;
    }


    public MultiCast2() {
        try {
            this.group = InetAddress.getByName(HOST);
            this.s = new MulticastSocket(PORT);
            gui = new GUI();
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
            for (Map.Entry<Byte, Sender> e: senders.entrySet()){
                if (e.getKey() == data[1]){
                    sender = e.getValue();
                }
            }
            for (Map.Entry<Byte, Receiver> e: receivers.entrySet()){
                if (e.getKey() == data[1]){
                    receiver = e.getValue();
                }
            }
            if (computerNumber != data[1]) {
                for (byte b : data) {
                    System.out.print(b);
                }
                switch (data[0]) {
                    // textpacket
                    //Only receiver gets these
                    case 0:
                        System.out.println("TEXT");
                        syn = new byte[HEADER];
                        for (int j = 0; j < HEADER; j++) {
                            syn[j] = data[j+3];
                        }
                        sendAck(data[1], syn);
                        byte[] message = new byte[data.length - 3 - HEADER];
                        System.arraycopy(data, 3 + HEADER, message, 0, message.length);
                        receiver.received.put(syn, message);
//                        gui.print(new String(message), data[1]);
                        break;
                    // startpacket
                    //Only receiver gets these
                    case 3:
                        System.out.println("START");
                        byte[] nul = new byte[HEADER];
                        for (int j = 0; j<HEADER; j++){
                            nul[j] = 0;
                        }
                        sendAck(data[1], nul);
                        receiver = new Receiver(data[1]);
                        receivers.put(data[1], receiver);
                        break;
                    //ackpacket
                    //Only sender gets these
                    case 4:
                        System.out.println("ACK");
                        syn = new byte[HEADER];
                        System.out.println(HEADER);
                        for (int j = 0; j < HEADER; j++) {
                            syn[j] = data[j+3];
                        }
                        if (HEADER == 1){
                            synint = syn[0];
                        }
                        else {
                            synint = ByteBuffer.wrap(syn).getInt();
                        }
                        System.out.println(synint);
                        if (synint == 0) {
                            System.out.println("Start ack received");
                            sender.setFirstReceivedTrue();
                        } else if (synint == 1) {
                            sender.setFinishReceivedTrue();
                        } else {
                            System.out.println("before remove " + sender.getNotReceived());
                            sender.removeNotReceived(syn);
                            System.out.println("after remove " + sender.getNotReceived());
                        }
                        break;
                        //finishpacket
                        //Only receiver gets these
                    case 5:
                        byte[] een = new byte[HEADER];
                        for (int j = 0; j<HEADER; j++){
                            if (j < HEADER -1){
                                een[j] = 0;
                            }
                            else if (j < HEADER){
                                een[j] = 1;
                            }
                        }
                        sendAck(data[1], een);
                        receiver.order();
                        System.out.println("hoi");
                        System.out.println(receiver==null);
                        System.out.println(receiver.received);
                        System.out.println(new String (String.valueOf(receiver.goodOrder)));
                        break;
                }
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
        System.out.println(computerNumber + ", " + destination + ", " + ackNumber[0]);
        byte[] packet = ackPacket.getAckPacket();
        DatagramPacket ack = new DatagramPacket(packet, packet.length, group, PORT);
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

    public List<byte[]> splitMessages(String msg) {
        List<byte[]> result = new ArrayList<byte[]>();
        byte[] message = msg.getBytes();
        int messagelength = message.length;
        while (messagelength > DATASIZE){
            byte[] packet = new byte[DATASIZE];
            System.arraycopy(message, message.length-messagelength, packet, 0, messagelength);
            result.add(packet);
            messagelength = messagelength - DATASIZE;
        }
        if (messagelength > 0){
            byte[] packet = new byte[messagelength];
            System.arraycopy(message, message.length-messagelength, packet, 0, messagelength);
            result.add(packet);
        }
        return result;
    }
//
//    public void intToByte (int i){
//        BigInteger bigInt = BigInteger.valueOf(i);
//        byte[] bytes = bigInt.toByteArray();
//        System.out.println(bytes);
//    }

    private void sendMessage(String msg, int destination) {
        try {
            //Send the entire message, split and with send data from TCP
            int syn = 2; //SYN starts with 1, because SYN 0 is reserved for the ACK of the START message, and
            //SYN 1 is reserved for the ACK of the FIN message
            List<byte[]> splitmessages = splitMessages(msg);
            for (byte[] packet : splitmessages) {
                TextPacket toSend = new TextPacket(computerNumber, destination, syn, msg);
                System.out.println(syn);
                DatagramPacket messagePacket = new DatagramPacket(toSend.getTextPacket(), toSend.getTextPacket().length, group, PORT);
                this.s.send(messagePacket);
                byte[] synmap = new byte[HEADER];
                synmap[0] = (byte) syn;
                sender.putNotReceived(synmap, packet);
                System.out.println(synmap[0] + " " + packet.toString());
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
        while (sender.getNotReceived().size()>0){
            System.out.println("nog niet leeg");
            Map<byte[], byte[]> notreceived = sender.getNotReceived();
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
        sender = new Sender(destination);
        senders.put((byte) destination, sender);

//        First send the 'First' message
        while (!sender.firstReceived){
            sendFirst(destination);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //If the receiver received their 'First' message and replied with an ack, send the message
        System.out.println("Het firstreceived zetten is goed gegaan");
        sendMessage(msg, destination);

        //After the message has been sent, send the 'Finish' message and wait for ack
        while (!sender.finishReceived){
            sendFinish(destination);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println ("finish is received");
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