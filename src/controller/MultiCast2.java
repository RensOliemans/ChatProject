package controller;

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
    private Map<Byte, Receiver> receivers = new HashMap<>();
    //Byte is the destination, sender is you
    private Map<Byte, Sender> senders = new HashMap<>();
    private int computerNumber;
    private static final int DATASIZE=128;
    public static final int HEADER = 1;
    private int synint;

    /*
     * Getter for computerNumber
     *
     * @return the computerNumber of the person belonging to MultiCast
     */
    public int getComputerNumber() {
        return computerNumber;
    }


    /*
     * Constructor. Is packageLocal, as only Starter and Ping call this.
     * Sets up the Internet address with the HOST, sets up the MultiCastSocket with the PORT and initializes a GUI.
     */
    public MultiCast2() {
        try {
            this.group = InetAddress.getByName(HOST);
            this.s = new MulticastSocket(PORT);
            gui = new GUI();
            join();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
     * This joins the group of the Internet Address
     * */
    private void join() {
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
            //You get a packet. This is of the following format:
            //data[0] = indication byte
            //data[1] = source of the sender
            //data[2] = destination of the sender - you (compare with computernumber)
            //data[3-x] = data/ack number/ack indication bit (with a start/fin message
            //At the end of the data and the whole packet, rens byte.
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
    public void sendPing() {

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

    /*
     * This method splits a message so it fits in packets of size DATASIZE.
     *
     * @param String msg, the unsplitted message in String
     * @result List<byte[]> result, a List of byte arrays of sizes DATASIZE
     */
    private List<byte[]> splitMessages(byte[] msg) {
        List<byte[]> result = new ArrayList<byte[]>();
        int messageLength = msg.length;
        while (messageLength > DATASIZE){
            byte[] packet = new byte[DATASIZE];
            System.arraycopy(msg, msg.length-messageLength, packet, 0, messageLength);
            result.add(packet);
            messageLength = messageLength - DATASIZE;
        }
        if (messageLength > 0){
            byte[] packet = new byte[messageLength];
            System.arraycopy(msg, msg.length-messageLength, packet, 0, messageLength);
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