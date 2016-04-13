package controller;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalTime;
import java.util.*;

import model.Sender;

import model.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import view.GUI;

import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import javax.xml.crypto.Data;
import javax.xml.soap.Text;


/**
 * Created by Rens on 5-4-2016.
 * This is the main class that does the sending and the receiving.
 * Every 'person' that is connected has an instance of MultiCast.
 */
public class MultiCast2 implements Runnable {

    private static final String HOST = "228.0.0.0";
    private static final int PORT = 1234;
    private InetAddress group;
    private MulticastSocket s;
    private Sender sender;
    private Receiver receiver;
    private GUI gui;
    private Security security;
    private Map<Integer, PublicKey> publicKeys = new HashMap<>(); //HashMap with K: computerNumber and V: their public keys
    private Map<Byte, Receiver> receivers = new HashMap<>();
    private Map<Byte, Sender> senders = new HashMap<>();
    private int computerNumber;
    private static final int DATASIZE = 1024;
    public static final int HEADER = 1;
    private int synint;

    Routing routing;

    private int receivedPings;

    Ping ping1 = new Ping(computerNumber, this);
    Ping ping2 = new Ping(computerNumber, this);
    Ping ping3 = new Ping(computerNumber, this);
    Ping ping4 = new Ping(computerNumber, this);
    public List presence = new ArrayList<>();


    /*
     * Getter for computerNumber
     *
     * @return the computerNumber of the person belonging to MultiCast
     */
    public int getNextHop(int destination) {
        int[] forwardingtable = routing.getForwardingTable();
        int nextHop;
        if (forwardingtable[destination + 7] == computerNumber || forwardingtable[destination + 7] == 0) {
            nextHop = destination;
        } else {
            nextHop = forwardingtable[destination + 7];
        }
        return nextHop;


    }

    public int getComputerNumber() {
        return computerNumber;
    }

    public static byte[] intToByte(int number) {
        return ByteBuffer.allocate(4).putInt(number).array();
    }

    public static int byteToInt(byte[] array) {
        return (array[0] << 24) & 0xff000000 |
                (array[1] << 16) & 0x00ff0000 |
                (array[2] << 8) & 0x0000ff00 |
                (array[3] << 0) & 0x000000ff;
    }

    public MultiCast2() {
        try {
            this.group = InetAddress.getByName(HOST);
            this.s = new MulticastSocket(PORT);
//            gui = new GUI(computerNumber, this);
            join();
        } catch (UnknownHostException e) {
            gui.showError("UnkownHostException in constructor of MultiCast. " +
                    "Check HOST (final field in MultiCast. " +
                    "\nError message: " + e.getMessage());
        } catch (IOException e) {
            gui.showError("IOException in constructor of MultiCast. " +
                    "Check PORT (final field in MultiCast. " +
                    "\nError message: " + e.getMessage());
        }
    }

    public void generateKeys() {
        security = new Security(this.gui);
    }

    /*
     * This joins the group of the Internet Address
     * */
    public void join() {
        try {
            this.s.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
            gui.showError("IOException in join(). " +
                    "Check your internet connection and if you are correctly connected to the ad-hoc network." +
                    "\nError message: " + e.getMessage());
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
            byte[] buf = new byte[2000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            this.s.receive(recv);
            byte[] data = recv.getData();
            byte[] seq;
            int seqint;
            int i = data.length;
            for (Map.Entry<Byte, Sender> e : senders.entrySet()) {
                if (e.getKey() == data[1]) {
                    sender = e.getValue();
                }
            }
            for (Map.Entry<Byte, Receiver> e : receivers.entrySet()) {
                if (e.getKey() == data[1]) {
                    receiver = e.getValue();
                }
            }
            //data[1] == source
            //data[2] == destination
            //data[3] == nexthop
            if (computerNumber != data[1] && (data[0] == 1 || data[0] == 2)) {
                if (data[0] == 1) {
                    if (data[2] == computerNumber) {
                        routing.setSourceAddress(data[1]);
                        routing.setLinkCost(data[3]);
                        byte[] bArray = new byte[12];
                        for (int k = 0; k < 12; k++) {
                            bArray[k] = data[k + 4];
                        }
                        System.out.println("received routing table: ");
                        int[] receivedtable = routing.byteArrayToIngerArray(bArray);
                        for (int j = 0; j < 12; j++) {
                            System.out.print(receivedtable[j] + " ");
                        }
                        System.out.println("");
                        System.out.println("old routing table: ");
                        int[] oldtable = routing.getForwardingTable();
                        for (int j = 0; j < 12; j++) {
                            System.out.print(oldtable[j] + " ");
                        }
                        System.out.println("");
                        routing.setForwardingTable(routing.byteArrayToIngerArray(bArray));
                    }
                }
                if (data[0] == 2) {
                    if (data[1] == 1) {
                        receivedPings = ping1.calculateReceivedPings(data[1]);
                        if (receivedPings != 0) {
                            sendRoutingPacket(data[1], receivedPings, routing.getForwardingTable());
                            System.out.println("received ping pakkets= " + receivedPings);
                        }
                    }
                    if (data[1] == 2) {
                        receivedPings = ping2.calculateReceivedPings(data[1]);
                        if (receivedPings != 0) {
                            sendRoutingPacket(data[1], receivedPings, routing.getForwardingTable());
                            System.out.println("received ping pakkets= " + receivedPings);
                        }
                    }
                    if (data[1] == 3) {
                        receivedPings = ping3.calculateReceivedPings(data[1]);
                        if (receivedPings != 0) {
                            sendRoutingPacket(data[1], receivedPings, routing.getForwardingTable());
                            System.out.println("received ping pakkets= " + receivedPings);
                        }
                    }
                    if (data[1] == 4) {
                        receivedPings = ping4.calculateReceivedPings(data[1]);
                        if (receivedPings != 0) {
                            sendRoutingPacket(data[1], receivedPings, routing.getForwardingTable());
                            System.out.println("received ping pakkets= " + receivedPings);
                        }
                    }
                }
            }
            if (computerNumber != data[1] && computerNumber == data[2] && computerNumber == data[3]) {
                data = removeRensByte(data);
                switch (data[0]) {
                    //textPacket
                    //Only receiver gets these
                    case 0:
                        System.out.println("TEXT");
                        seq = new byte[HEADER * 4];
                        System.arraycopy(data, 4, seq, 0, HEADER * 4);
                        sendAck(data[1], seq);
                        byte[] encryptedData = new byte[data.length - 4 - seq.length];
                        System.arraycopy(data, 4 + seq.length, encryptedData, 0, encryptedData.length);
                        System.out.println(new String(encryptedData));
                        String encryptedDataString = new String(encryptedData);
                        String decryptedDataString = this.security.decryptSymm(encryptedDataString, this.security.getSymmetricKey(data[1]));
                        byte[] decryptedData = decryptedDataString.getBytes();
//                        byte[] decryptedData = this.security.decryptSymm(new String(encryptedData), this.security.getSymmetricKey(data[1])).getBytes();
                        receiver.putReceived(seq, decryptedData);

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
                        seq = new byte[HEADER * 4];
                        System.arraycopy(data, 4, seq, 0, HEADER * 4);
                        seqint = byteToInt(seq);
                        if (seqint == 0) {
                            System.out.println("Start ack received");
                            sender.setFirstReceivedTrue();
                        } else if (seqint == 1) {
                            System.out.println("Finish ack received");
                            sender.setFinishReceivedTrue();
                        } else if (seqint == 2) {
                            System.out.println("Key ack received");
                            sender.setKeysRecievedTrue();
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
//                        receiver.showImage(byteArray);
                        System.out.println(new String(byteArray));
                        break;
                    case 6:
                        //This is the packet for the request of one's public key.
                        // Only the receiver gets these
                        byte[] senderKeyBytes = new byte[data.length - 4 /*because header length is 4*/];
                        System.arraycopy(data, 4, senderKeyBytes, 0, senderKeyBytes.length);
                        PublicKey senderKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(senderKeyBytes));
                        this.publicKeys.put((int) data[1], senderKey);
                        sendPublicKey(data[1], true);
                        break;
                    case 7:
                        //This is the ack packet of the public key message, and also has a public key.
                        // Only the sender gets these
                        byte[] receiverKeyBytes = new byte[data.length - 4];
                        System.arraycopy(data, 4, receiverKeyBytes, 0, receiverKeyBytes.length);
                        PublicKey receiverKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(receiverKeyBytes));
                        this.publicKeys.put((int) data[1], receiverKey);
                        //Both parties know the public keys of the other side.
                        //Sender now generates and sends the AES key, encrypted with the public key of the receiver
                        security.generateAESKey(data[1]);
                        sendAESKey(data[1]);
                        break;
                    case 8:
                        //This is the encrypted AES key.
                        // Only the receiver gets this.
                        byte[] encryptedAESKeyBytes = new byte[data.length - 4];
                        System.arraycopy(data, 4, encryptedAESKeyBytes, 0, encryptedAESKeyBytes.length);
//                        System.out.println(security == null);
//                        System.out.println(encryptedAESKeyBytes.length);
                        SecretKey key = security.decryptAESKey(encryptedAESKeyBytes);
                        security.addSymmetricKey(data[1], key);
                        sendAck(data[1], intToByte(2));
                        break;
                }
            } else if (computerNumber != data[1] && computerNumber == data[3]) {
                int tussenHop = getNextHop(data[2]);
                data[3] = (byte) tussenHop;
                DatagramPacket datagramdata = new DatagramPacket(data, data.length, group, PORT);
                this.s.send(datagramdata);
            }
        } catch (IOException e) {
            gui.showError("IOException in receive(). " +
                    "The sending or receiving of a DatagramPacket went wrong. " +
                    "Check internet connection or if you are in the ad-hoc network (try leaving and entering network in admin mode). " +
                    "\nError message: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            gui.showError("NoSuchAlgorithmException in receive(). " +
                    "The security algorithm wasn't found. see cases:" +
                    "data[0] == 7 || data[0] == 6. Ask Rens. " +
                    "\nError message: " + e.getMessage());
        } catch (InvalidKeySpecException e) {
            gui.showError("InvalidKeySpecException in receive(). " +
                    "The receiving of the public key went wrong. See cases: " +
                    "data[0] == 6 || data[0] == 7. Ask Rens. " +
                    "\nError message: " + e.getMessage());
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
        while (i-- > 0 && data[i] == 0) ;
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
//        System.out.println("comp + dest + ack[3] : " + computerNumber + ", " + destination + ", " + ackNumber[3]);
        byte[] packet = ackPacket.getAckPacket();
        DatagramPacket ack = new DatagramPacket(packet, packet.length, group, PORT);
        try {
            this.s.send(ack);
        } catch (IOException e) {
            gui.showError("IOException in sendAck(..). " +
                    "The sending of a DatagramPacket went wrong. " +
                    "Check internet connection or if you are in the ad-hoc network (try leaving and entering network in admin mode). " +
                    "\nError message: " + e.getMessage());
        }
    }

    /*
     * Sends a ping to everyone on the network.
     */
    public void sendPing(int computerNumber) {
        System.out.println("computernumber given with pingpackets= " + computerNumber);
        for (int i = 0; i < 255; i++) {
            PingPacket burstPacket = new PingPacket(computerNumber);
            DatagramPacket burst = new DatagramPacket(burstPacket.getPingPacket(), burstPacket.getPingPacket().length, group, PORT);
//            try {
//                this.s.send(burst);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
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
            gui.showError("IOException in sendFirst(..). " +
                    "The sending of a DatagramPacket went wrong. " +
                    "Check internet connection or if you are in the ad-hoc network (try leaving and entering network in admin mode). " +
                    "\nError message: " + e.getMessage());
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
            gui.showError("IOException in sendFinish(..). " +
                    "The sending of a DatagramPacket went wrong. " +
                    "Check internet connection or if you are in the ad-hoc network (try leaving and entering network in admin mode). " +
                    "\nError message: " + e.getMessage());
        }
    }

    private void sendRoutingPacket(int destinationAddress, int linkcost, int[] data_table) {
        RoutingPacket routingPacket = new RoutingPacket(computerNumber, destinationAddress, 256 - linkcost, data_table);
        DatagramPacket routing = new DatagramPacket(routingPacket.getRoutingPacket(), routingPacket.getRoutingPacket().length, group, PORT);
        try {
            this.s.send(routing);
        } catch (IOException e) {
            gui.showError("IOException in sendRoutingPacket(..). " +
                    "The sending of a DatagramPacket went wrong. " +
                    "Check internet connection or if you are in the ad-hoc network (try leaving and entering network in admin mode). " +
                    "\nError message: " + e.getMessage());
        }
    }

    private void sendPublicKey(int destination, boolean isAck) {
        KeyPacket keyPacket = new KeyPacket(computerNumber, destination, getNextHop(destination), security.getPublicKey(), isAck);
        DatagramPacket packet = new DatagramPacket(keyPacket.getKeyPacket(), keyPacket.getKeyPacket().length, group, PORT);
        try {
            this.s.send(packet);
        } catch (IOException e) {
            gui.showError("IOException in sendPublicKey(..). " +
                    "The sending of a DatagramPacket went wrong. " +
                    "Check internet connection or if you are in the ad-hoc network (try leaving and entering network in admin mode). " +
                    "\nError message: " + e.getMessage());
        }
    }


    private void sendAESKey(int destination) {
        SecretKey AESKey = security.getSymmetricKey(destination);
        if (AESKey != null) {
            //Encrypt the AES key with the public key of the receiver
            byte[] encryptedAESKey = security.getEncryptedAESKey(this.publicKeys.get(destination), AESKey);
            AESPacket aesPacket = new AESPacket(computerNumber, destination, getNextHop(destination), encryptedAESKey);
            DatagramPacket packet = new DatagramPacket(aesPacket.getAESPacket(), aesPacket.getAESPacket().length, group, PORT);
            try {
                this.s.send(packet);
            } catch (IOException e) {
                gui.showError("IOException in sendAESKey(..). " +
                        "The sending of a DatagramPacket went wrong. " +
                        "Check internet connection or if you are in the ad-hoc network (try leaving and entering network in admin mode). " +
                        "\nError message: " + e.getMessage());
            }
        }

    }


    public void sendImage(String imageName, int destination) {
        try {
            //open image
            File image = new File(imageName);
            BufferedImage bufferedImage = ImageIO.read(image);

            //get DataBufferBytes from raster
            WritableRaster raster = bufferedImage.getRaster();
            DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

            //send the image
            sendMessage(data.getData(), destination);
        } catch (IOException e) {
            gui.showError("IOException in sendImage(..). " +
                    "Reading of image file went wrong. " +
                    "Is it a .jpg file? Otherwise, ask Rens. " +
                    "\nError message: " + e.getMessage());
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
        while (messagelength > DATASIZE) {
            byte[] packet = new byte[DATASIZE];
            System.arraycopy(msg, msg.length - messagelength, packet, 0, DATASIZE);
            result.add(packet);
            messagelength = messagelength - DATASIZE;
        }
        if (messagelength > 0) {
            byte[] packet = new byte[messagelength];
            System.arraycopy(msg, msg.length - messagelength, packet, 0, messagelength);
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
            int seqint = 3;
            //SEQ starts with 2, because SEQ 0 is reserved for the ACK of the START message, and
            //SEQ 1 is reserved for the ACK of the FIN message
            List<byte[]> splitmessages = splitMessages(msg);
            for (byte[] packet : splitmessages) {
                byte[] seq = intToByte(seqint);
                byte[] encryptedData = this.security.encryptSymm(new String(packet), this.security.getSymmetricKey(destination)).getBytes();
                TextPacket toSend = new TextPacket(computerNumber, destination, seq, new String(encryptedData), getNextHop(destination));
                System.out.println(new String(toSend.getTextPacket()));
//                System.out.println("seq[0] + seq[1] + seq[2] + seq[3]: " + seq[0] + seq[1] + seq[2] + seq[3]);
//                DatagramPacket messagePacket = new DatagramPacket(encryptedData, encryptedData.length, group, PORT);
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
                if (!alAanwezig) {
                    sender.putNotReceived(seq, packet);
                }
                seqint++;
            }
        } catch (IOException e) {
            gui.showError("IOException in sendMessage(..). " +
                    "The sending of a DatagramPacket went wrong. " +
                    "Check internet connection or if you are in the ad-hoc network (try leaving and entering network in admin mode). " +
                    "\nError message: " + e.getMessage());
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            gui.showError("InterruptedException in sendMessage(..). " +
                    "Happened while waiting for ACKs (first time). " +
                    "Shouldn't happen. Error message: " + e.getMessage());
        }

        //If packets have been lost (not acked after 100ms), resend them until everything has been acked
        while (sender.getNotReceived().size() > 0) {
            System.out.println("nog niet leeg");
            Map<byte[], byte[]> notreceived = sender.getNotReceived();
            for (Map.Entry<byte[], byte[]> e : notreceived.entrySet()) {
                sendMessage(e.getValue(), destination);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                gui.showError("InterruptedException in sendMessage(..). " +
                        "Happened while waiting for ACKs (not first time). " +
                        "Shouldn't happen. Error message: " + e.getMessage());
            }
        }
    }

    public void send(String msg, int destination) {
        sender = new Sender(destination);
        senders.put((byte) destination, sender);

//        First send the 'First' message
        while (!sender.firstReceived) {
            sendFirst(destination);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                gui.showError("InterruptedException in send(..). " +
                        "Happened while waiting for ACK of first received. " +
                        "Shouldn't happen. Error message: " + e.getMessage());
            }
        }

        while (!sender.keysReceived) {
            sendPublicKey(destination, false);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                gui.showError("InterruptedException in send(..). " +
                        "Happened while waiting for ACK of keysReceived. " +
                        "Shouldn't happen. Error message: " + e.getMessage());
            }
        }

//        System.out.println("SUCCESS");

        //If the receiver received their 'First' message and replied with an ack, send the message
        System.out.println("Het firstreceived zetten is goed gegaan");
        sendMessage(msg.getBytes(), destination);
//        sendImage(msg, destination);

        //After the message has been sent, send the 'Finish' message and wait for ack
        while (!sender.finishReceived) {
            sendFinish(destination);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                gui.showError("InterruptedException in send(..). " +
                        "Happened while waiting for finish ACK. " +
                        "Shouldn't happen. Error message: " + e.getMessage());
            }
        }
        System.out.println("finish is received");
    }

    public void leave() {
        try {
            this.s.leaveGroup(this.group);
        } catch (IOException e) {
            gui.showError("IOException in leave(). " +
                    "Check if the ad-hoc network is set up correctly (both your side and 'server' side. " +
                    "\nError message: " + e.getMessage());
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
        return (array[0] << 24) & 0xff000000 |
                (array[1] << 16) & 0x00ff0000 |
                (array[2] << 8) & 0x0000ff00 |
                (array[3] << 0) & 0x000000ff;
    }
}