package controller;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.LocalTime;
import java.util.*;

import model.Sender;

import model.*;
import view.GUI;

import javax.crypto.SecretKey;
import javax.xml.crypto.Data;
import javax.xml.soap.Text;

import static com.oracle.jrockit.jfr.ContentType.Bytes;

/**
 * Created by Rens on 5-4-2016.
 * This is the main class that does the sending and the receiving.
 * Every 'person' that is connected has an instance of MultiCast.
 */
public class MultiCast2 implements Runnable{

    //Dit is een voorbeeld van een join methode

    private static final String HOST = "228.0.0.0";
    private static final int PORT = 1234;
    //    String host;
    //    int port;
    private InetAddress group;
    private MulticastSocket s;
    private Sender sender;
    private Receiver receiver;
    private GUI gui;
    private Security security;
    private Map<Integer, SecretKey> symmetricKeys = new HashMap<>(); //HashMap with K: computerNumber and V: our symmetric key
    private Map<Integer, PublicKey> publicKeys = new HashMap<>(); //HashMap with K: computerNumber and V: their public keys
    private Map<Byte, Receiver> receivers = new HashMap<>();
    //Byte is the destination, sender is you
    private Map<Byte, Sender> senders = new HashMap<>();
    private int computerNumber;
    private static final int DATASIZE=128;
    public static final int HEADER = 1;
    private int synint;

    private int receivedPing = 0;
    private long seconds1;
    private long seconds2;
    public List presence = new ArrayList<>();

    /*
     * Getter for computerNumber
     *
     * @return the computerNumber of the person belonging to MultiCast
     */
    public int getComputerNumber() {
        return computerNumber;
    }

    public static byte[] intToByte(int number) {
        return ByteBuffer.allocate(4).putInt(number).array();
    }

    public static int byteToInt(byte[] array) {
        return (array[0]<<24)&0xff000000|
                (array[1]<<16)&0x00ff0000|
                (array[2]<< 8)&0x0000ff00|
                (array[3]<< 0)&0x000000ff;
    }

    public MultiCast2() {
        try {
            this.group = InetAddress.getByName(HOST);
            this.s = new MulticastSocket(PORT);
//            gui = new GUI(computerNumber, this);
            join();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateKeys() {
        security = new Security();
    }

    /*
     * This joins the group of the Internet Address
     * */
    public void join() {
        try {
            this.s.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * This receives the message. It first calls MultiCastSocket.receive and fills a DatagramPacket.
     * After the packet has been received, it receives the excess buffer data (0's at the end) and handles the message
     *  by sending an ACK if it received a DataPacket, creating a new Receiver object (and acking) if it received a StartPacket
     *  and forwarding the messages to the GUI and deleting the Receiver object if it received a FinPacket.
     */
    private void receive() {
        try {
            byte[] buf = new byte[1000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            this.s.receive(recv);
            byte[] data = recv.getData();
            byte[] seq;
            int seqint;
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
                switch (data[0]) {
                    // textpacket
                    //Only receiver gets these
                    case 0:
                        System.out.println("TEXT");
                        seq = new byte[HEADER*4];
                        System.arraycopy(data, 3, seq, 0, HEADER*4);
                        sendAck(data[1], seq);
                        byte[] message = new byte[data.length - 3 - seq.length];
                        System.arraycopy(data, 3 + seq.length, message, 0, message.length);
                        receiver.received.put(seq, message);
                        break;

                    //RoutingPacket
                    case 1:
                        Routing routing = new Routing(computerNumber);
                        routing.setSourceAddress(data[1]);
                        routing.setLinkCost(data[3]);
                        byte[] bArray = new byte[8];
                        for (int k=0; k<8; k++){
                            bArray[k] = data[k+4];
                        }
                        routing.setForwardingTable(routing.byteArrayToIngerArray(bArray));
                        break;

                    //pingPacket
                    case 2:
                        if (!presence.contains(data[1]) && data[1] != 0){
                            presence.add(data[1]);
                        }
                        if (receivedPing == 0){
                            seconds1 = System.currentTimeMillis();
                            receivedPing ++;
                        } else {
                            seconds2 = System.currentTimeMillis();
                            receivedPing ++;
                        }
                        if ((seconds2 - seconds1 > 3000) && (receivedPing != 0)){
                            int[] emptyForwardingTable = new int[8];
                            sendRoutingPacket(data[1], receivedPing, emptyForwardingTable);
                            System.out.println("received ping pakkets= " + receivedPing);
                        }
                        if ((seconds2 - seconds1 > 4500) && (receivedPing != 0)){
                            seconds1 = 0;
                            seconds2 = 0;
                            receivedPing = 0;
                            System.out.println("presence lijst is nu als volgt: ");
                            for (int x=0; x<presence.size(); x++){
                                System.out.println(presence.get(x));
                            }
                            presence.clear();
                        }


                        break;

                    // startpacket
                    //Only receiver gets these
                    case 3:
                        System.out.println("START");
                        byte[] nul = intToByte(0);
                        sendAck(data[1], nul);
                        receiver = new Receiver(data[1]);
                        receivers.put(data[1], receiver);
                        break;
                    //ackpacket
                    //Only sender gets these
                    case 4:
                        System.out.println("ACK");
                        seq = new byte[HEADER*4];

                        System.arraycopy(data, 3, seq, 0, HEADER*4);
                        seqint = byteToInt(seq);
                        if (seqint == 0) {
                            System.out.println("Start ack received");
                            sender.setFirstReceivedTrue();
                        } else if (seqint == 1) {
                            System.out.println("Finish ack received");
                            sender.setFinishReceivedTrue();
                        } else {
                            sender.removeNotReceived(seq);
                        }
                        break;
                        //finishpacket
                        //Only receiver gets these
                    case 5:
                        seq = intToByte(1);
                        sendAck(data[1], seq);
                        System.out.println("Hij gaat nu in order");
                        receiver.order();
                        System.out.println(new String (String.valueOf(receiver.goodOrder)));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Removes the Rens byte of a message and all of the 0's after it
     *
     * @param byte[] data, the uncropped message
     * @result byte[] croppedResult, the cropped message
     */
    private byte[] removeRensByte(byte[] data) {
        //De 'Rens byte' is vernoemd naar de geniaalste coder die er bestaat.
        //Bij het receiven krijgt de DatagramPacket een buffer van 1000 bytes, en bij het binnenkomen wordt
        //de message aangevuld met nullen. De Rens byte plaatst een 1 achter het daadwerkelijke message, zodat
        //bij het binnenkomen alle laatste nullen, inclusief de eerste 1 (Rens byte), zodat de message overblijft
        byte[] croppedResult;

        int i = data.length - 1;
        //i wordt de lengte van de message
        while (i-- > 0 && data[i] == 0);
        croppedResult = new byte[i];
        System.arraycopy(data, 0, croppedResult, 0, i);

        return croppedResult;
    }

    /*
     * Sends an ACK message by creating an AckPacket and sending it using MultiCastSocket.send(DatagramPacket).
     *
     * @param int destination, the computernumber of the player to send the message to
     * @param byte[] acknumber, the acknumber to send in the ACK message
     */
    private void sendAck(int destination, byte[] ackNumber) {
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

    /*
     * Sends a ping to everyone on the network.
     */
    public void sendPing(int computerNumber, String name) {
        System.out.println("computernumber given with pingpackets= " + computerNumber);
        for (int i = 0; i < 255; i++){
            PingPacket burstPacket = new PingPacket(computerNumber, name);
            DatagramPacket burst = new DatagramPacket(burstPacket.getPingPacket(), burstPacket.getPingPacket().length, group, PORT);
            try {
                this.s.send(burst);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * Sends a FIRST message to make sure that the receiver can receive messages.
     *
     * @param int destination, the computernumber of the player to send the message to
     */
    private void sendFirst(int destination) {
        StartPacket firstPacket = new StartPacket(computerNumber, destination);
        DatagramPacket first = new DatagramPacket(firstPacket.getStartPacket(), firstPacket.getStartPacket().length, group, PORT);
        try {
            this.s.send(first);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
     * Sends a FINISH message to notify the receiver that every packet has been sent
     *
     * @param int destination, the computernumber of the player to send the message to
     */
    private void sendFinish(int destination) {
        FinishPacket finishPacket = new FinishPacket(computerNumber, destination);
        DatagramPacket finish = new DatagramPacket(finishPacket.getFinishPacket(), finishPacket.getFinishPacket().length, group, PORT);
        try {
            this.s.send(finish);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRoutingPacket(int destinationAddress, int linkcost, int[] data_table){
        RoutingPacket routingPacket = new RoutingPacket(computerNumber, destinationAddress, 256-linkcost, data_table);
        DatagramPacket routing = new DatagramPacket(routingPacket.getRoutingPacket(), routingPacket.getRoutingPacket().length, group, PORT);
        try {
            this.s.send(routing);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * This method splits a message so it fits in packets of size DATASIZE.
     *
     * @param String msg, the unsplitted message in String
     * @result List<byte[]> result, a List of byte arrays of sizes DATASIZE
     */
    private List<byte[]> splitMessages(byte[] msg) {
        List<byte[]> result = new ArrayList<byte[]>();
        int messagelength = msg.length;
        while (messagelength > DATASIZE){
            byte[] packet = new byte[DATASIZE];
            System.arraycopy(msg, msg.length-messagelength, packet, 0, DATASIZE);
            result.add(packet);
            messagelength = messagelength - DATASIZE;
        }
        if (messagelength > 0){
            byte[] packet = new byte[messagelength];
            System.arraycopy(msg, msg.length-messagelength, packet, 0, messagelength);
            result.add(packet);
        }
        return result;
    }

    /*
     * This message sends a message (
     */
    private void sendMessage(byte[] msg, int destination) {
        try {
            //Send the entire message, split and with send data from TCP
            int seqint = 2;
            byte[] seq = intToByte(seqint);
            //SYN starts with 1, because SYN 0 is reserved for the ACK of the START message, and
            //SYN 1 is reserved for the ACK of the FIN message
            List<byte[]> splitmessages = splitMessages(msg);
            for (byte[] packet : splitmessages) {
                TextPacket toSend = new TextPacket(computerNumber, destination, seq, new String(packet));
                System.out.println("sequence number is= " + seq[0] + "" + seq[1] + "" + seq[2] + "" + seq[3]);
                DatagramPacket messagePacket = new DatagramPacket(toSend.getTextPacket(), toSend.getTextPacket().length, group, PORT);
                this.s.send(messagePacket);
                boolean alAanwezig = false;
                for (Map.Entry<byte[], byte[]> e: sender.getNotReceived().entrySet()){
                    if (java.util.Arrays.equals(e.getKey(), seq)){
                        alAanwezig = true;
                    }
                }
                if (!alAanwezig){
                    sender.putNotReceived(seq, packet);
                }
                seqint++;
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
            System.out.println("notreceived is nog niet leeg");
            Map<byte[], byte[]> notreceived = sender.getNotReceived();
            for (Map.Entry<byte[], byte[]> e : notreceived.entrySet()){
                sendMessage(e.getValue(), destination);
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
        sendMessage(msg.getBytes(), destination);

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

    public void requestPublicKeys(int destination) {

    }

    private void hoi() {

    }

    @Override
    public void run() {
        while (true) receive();
    }

    public void setComputerNumber(int computerNumber) {
        this.computerNumber = computerNumber;
    }

    public byte[] intToByteArray(int number) {
        return ByteBuffer.allocate(4).putInt(number).array();
    }

    public int byteArrayToInt(byte[] array) {
        return (array[0]<<24)&0xff000000|
                (array[1]<<16)&0x00ff0000|
                (array[2]<< 8)&0x0000ff00|
                (array[3]<< 0)&0x000000ff;
    }
}