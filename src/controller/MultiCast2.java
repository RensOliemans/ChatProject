package controller;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
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
import javax.imageio.ImageIO;
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

    Routing routing;

    private int receivedPing1 = 0;
    private int receivedPing2 = 0;
    private int receivedPing3 = 0;
    private int receivedPing4 = 0;
    private long seconds1;
    private long seconds2;
    private long seconds3;
    private long seconds4;
    private long seconds5;
    private long seconds6;
    private long seconds7;
    private long seconds8;
    public List presence = new ArrayList<>();


    /*
     * Getter for computerNumber
     *
     * @return the computerNumber of the person belonging to MultiCast
     */
    public int getNextHop(int destination){
        int[] forwardingtable = routing.getForwardingTable();
        int nextHop = forwardingtable[destination+7];
        return nextHop;
    }

    public int getComputerNumber() {
        return computerNumber;
        routing = new Routing(computerNumber);
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

    public void setComputerNumber(int computerNumber) {
        this.computerNumber = computerNumber;
        routing = new Routing(computerNumber);
    }

    /*
     * This receives the message. It first calls MultiCastSocket.receive and fills a DatagramPacket.
     * After the packet has been received, it receives the excess buffer data (0's at the end) and handles the message
     *  by sending an ACK if it received a DataPacket, creating a new Receiver object (and acking) if it received a StartPacket
     *  and forwarding the messages to the GUI and deleting the Receiver object if it received a FinPacket.
     */
    private void receive() {
        try {
            byte[] buf = new byte[1032];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            this.s.receive(recv);
            byte[] data = recv.getData();
            byte[] seq;
            int seqint;
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
            if (computerNumber != data[1] && computerNumber == data[2] && computerNumber == data[3]) {
                data = removeRensByte(data);
                switch (data[0]) {
                    // textpacket
                    //Only receiver gets these
                    case 0:
                        System.out.println("TEXT");
                        seq = new byte[HEADER*4];
                        System.arraycopy(data, 4, seq, 0, HEADER*4);
                        sendAck(data[1], seq);
                        byte[] message = new byte[data.length - 4 - seq.length];
                        System.arraycopy(data, 4 + seq.length, message, 0, message.length);
                        receiver.received.put(seq, message);
                        break;

                    //RoutingPacket
                    case 1:
                        if (data[2] == computerNumber){
                            routing.setSourceAddress(data[1]);
                            routing.setLinkCost(data[3]);
                            byte[] bArray = new byte[12];
                            for (int k=0; k<12; k++){
                                bArray[k] = data[k+4];
                            }
                            System.out.println("received routing table: ");
                            int[] receivedtable = routing.byteArrayToIngerArray(bArray);
                            for (int j=0; j<12; j++){
                                System.out.println(receivedtable[j]);
                            }
                            System.out.println("old routing table: ");
                            int[] oldtable = routing.getForwardingTable();
                            for (int j=0; j<12; j++){
                                System.out.println(oldtable[j]);
                            }
                            routing.setForwardingTable(routing.byteArrayToIngerArray(bArray));
                        }
                        break;

                    //pingPacket
                    case 2:
                        if (data[1] == 1){
                            if (!presence.contains(data[1]) && data[1] != 0){
                                presence.add(data[1]);
                            }
                            if (receivedPing1 == 0){
                                seconds1 = System.currentTimeMillis();
                                receivedPing1 ++;
                            } else {
                                seconds2 = System.currentTimeMillis();
                                receivedPing1 ++;
                            }
                            if ((seconds2 - seconds1 > 3000) && (receivedPing1 != 0)){
                                sendRoutingPacket(data[1], receivedPing1, routing.getForwardingTable());
                                System.out.println("received ping pakkets= " + receivedPing1);
                            }
                            if ((seconds2 - seconds1 > 4500) && (receivedPing1 != 0)){
                                seconds1 = 0;
                                seconds2 = 0;
                                receivedPing1 = 0;
                                System.out.println("presence lijst is nu als volgt: ");
                                for (int x=0; x<presence.size(); x++){
                                    System.out.println(presence.get(x));
                                }
                                presence.clear();
                            }
                        }
                        if (data[1] == 2){
                            if (!presence.contains(data[1]) && data[1] != 0){
                                presence.add(data[1]);
                            }
                            if (receivedPing2 == 0){
                                seconds3 = System.currentTimeMillis();
                                receivedPing2 ++;
                            } else {
                                seconds4 = System.currentTimeMillis();
                                receivedPing2 ++;
                            }
                            if ((seconds4 - seconds3 > 3000) && (receivedPing2 != 0)){
                                int[] emptyForwardingTable = new int[12];
                                sendRoutingPacket(data[1], receivedPing2, routing.getForwardingTable());
                                System.out.println("received ping pakkets= " + receivedPing2);
                            }
                            if ((seconds4 - seconds3 > 4500) && (receivedPing2 != 0)){
                                seconds3 = 0;
                                seconds4 = 0;
                                receivedPing2 = 0;
                                System.out.println("presence lijst is nu als volgt: ");
                                for (int x=0; x<presence.size(); x++){
                                    System.out.println(presence.get(x));
                                }
                                presence.clear();
                            }
                        }

                        if (data[1] == 3){
                            if (!presence.contains(data[1]) && data[1] != 0){
                                presence.add(data[1]);
                            }
                            if (receivedPing3 == 0){
                                seconds5 = System.currentTimeMillis();
                                receivedPing3 ++;
                            } else {
                                seconds6 = System.currentTimeMillis();
                                receivedPing3 ++;
                            }
                            if ((seconds6 - seconds5 > 3000) && (receivedPing3 != 0)){
                                sendRoutingPacket(data[1], receivedPing3, routing.getForwardingTable());
                                System.out.println("received ping pakkets= " + receivedPing3);
                            }
                            if ((seconds6 - seconds5 > 4500) && (receivedPing3 != 0)){
                                seconds5 = 0;
                                seconds6 = 0;
                                receivedPing3 = 0;
                                System.out.println("presence lijst is nu als volgt: ");
                                for (int x=0; x<presence.size(); x++){
                                    System.out.println(presence.get(x));
                                }
                                presence.clear();
                            }
                        }

                        if (data[1] == 4){
                            if (!presence.contains(data[1]) && data[1] != 0){
                                presence.add(data[1]);
                            }
                            if (receivedPing4 == 0){
                                seconds7 = System.currentTimeMillis();
                                receivedPing4 ++;
                            } else {
                                seconds8 = System.currentTimeMillis();
                                receivedPing4 ++;
                            }
                            if ((seconds8 - seconds7 > 3000) && (receivedPing4 != 0)){
                                sendRoutingPacket(data[1], receivedPing4, routing.getForwardingTable());
                                System.out.println("received ping pakkets= " + receivedPing4);
                            }
                            if ((seconds8 - seconds7 > 4500) && (receivedPing4 != 0)){
                                seconds7 = 0;
                                seconds8 = 0;
                                receivedPing4 = 0;
                                System.out.println("presence lijst is nu als volgt: ");
                                for (int x=0; x<presence.size(); x++){
                                    System.out.println(presence.get(x));
                                }
                                presence.clear();
                            }
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

                        System.arraycopy(data, 4, seq, 0, HEADER*4);
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
                        Byte[] dataArray = receiver.goodOrder.toArray(new Byte[receiver.goodOrder.size()]);
                        byte[] byteArray = new byte[dataArray.length];
                        for (int j = 0; j < dataArray.length; j++) {
                            byteArray[j] = dataArray[j];
                        }
                        receiver.showImage(byteArray);
                        System.out.println(String.valueOf(receiver.goodOrder));
                        break;
                    //case 6:
                        //
                }
            }
            else if (computerNumber != data[1] && computerNumber == data[3]){
                int tussenHop = getNextHop(data[2]);
                data[3] = (byte) tussenHop;
                DatagramPacket datagramdata = new DatagramPacket(data, data.length, group, PORT);
                this.s.send(datagramdata);
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
        AckPacket ackPacket = new AckPacket(computerNumber, destination, ackNumber, getNextHop(destination));
        System.out.println("comp + dest + ack[3] : " + computerNumber + ", " + destination + ", " + ackNumber[3]);
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
    public void sendPing(int computerNumber) {
        System.out.println("computernumber given with pingpackets= " + computerNumber);
        for (int i = 0; i < 255; i++){
            PingPacket burstPacket1 = new PingPacket(computerNumber, (computerNumber%4)+1, getNextHop((computerNumber%4)+1));
            PingPacket burstPacket2 = new PingPacket(computerNumber, (computerNumber%4)+2, getNextHop((computerNumber%4)+2));
            PingPacket burstPacket3 = new PingPacket(computerNumber, (computerNumber%4)+3, getNextHop((computerNumber%4)+3));
            DatagramPacket burst1 = new DatagramPacket(burstPacket1.getPingPacket(), burstPacket1.getPingPacket().length, group, PORT);
            DatagramPacket burst2 = new DatagramPacket(burstPacket2.getPingPacket(), burstPacket2.getPingPacket().length, group, PORT);
            DatagramPacket burst3 = new DatagramPacket(burstPacket3.getPingPacket(), burstPacket3.getPingPacket().length, group, PORT);
            try {
                this.s.send(burst1);
                this.s.send(burst2);
                this.s.send(burst3);
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
        StartPacket firstPacket = new StartPacket(computerNumber, destination, getNextHop(destination));
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
        FinishPacket finishPacket = new FinishPacket(computerNumber, destination, getNextHop(destination));
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

    private void sendImage(String imageName, int destination) {
        try {
            //open image
            File imgPath = new File(imageName);
            BufferedImage bufferedImage = ImageIO.read(imgPath);

            //get DataBufferBytes from raster
            WritableRaster raster = bufferedImage.getRaster();
            DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

            //send the image
            sendMessage(data.getData(), destination);
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
            //SEQ starts with 2, because SEQ 0 is reserved for the ACK of the START message, and
            //SEQ 1 is reserved for the ACK of the FIN message
            List<byte[]> splitmessages = splitMessages(msg);
            for (byte[] packet : splitmessages) {
                byte[] seq = intToByte(seqint);
                TextPacket toSend = new TextPacket(computerNumber, destination, seq, new String(packet), getNextHop(destination));
                System.out.println("seq[0] + seq[1] + seq[2] + seq[3]: " + seq[0] + seq[1] + seq[2] + seq[3]);
                DatagramPacket messagePacket = new DatagramPacket(toSend.getTextPacket(), toSend.getTextPacket().length, group, PORT);
                this.s.send(messagePacket);
                boolean alAanwezig = false;
                synchronized (sender) {
                    for (Map.Entry<byte[], byte[]> e : sender.getNotReceived().entrySet()) {
                        if (java.util.Arrays.equals(e.getKey(), seq)) {
                            alAanwezig = true;
                        }
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
        while (sender.getNotReceived().size() > 0){
            System.out.println("nog niet leeg");
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

    @Override
    public void run() {
        while (true) receive();
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