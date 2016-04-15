package model;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import controller.MultiCast;
import view.GUI;

import javax.imageio.ImageIO;

/**
 * This class is the class that handles the receiving of messages.
 * While the method MultiCast.receive() handles the actual messages, this class has functionality to
 *  order the messages that came in and put files that were received in a HashMap.
 * For every connection, there is a Receiver instance.
 * Created by Birte on 7-4-2016.
 */
public class Receiver {

    private Boolean allReceived = false;
    private GUI gui;
    private Map<byte[], byte[]> received = new HashMap<>();
    private int sender;
    private List<Byte> goodOrderList = null;
    public byte[] goodOrder;

    public Receiver(int sender) {
        this.sender = sender;
        this.received = new HashMap<>();
        this.allReceived = false;
        this.goodOrder = null;
    }

    public void order() {
        List<Byte> result = new ArrayList<>();
        for (int i = 3; i < this.received.size() + 3; i++) {
            byte[] j = MultiCast.intToByte(i);
            for (Map.Entry<byte[], byte[]> e : this.received.entrySet()) {
//                System.out.println("seq nummer " + MultiCast.byteToInt(e.getKey()));
                if (Arrays.equals(e.getKey(), j)) {
                    byte[] packet = e.getValue();
                    for (byte aPacket : packet) {
                        result.add(aPacket);
                    }
                }
            }
        }
        goodOrderList = result;
        Byte[] dataArray = goodOrderList.toArray(new Byte[goodOrderList.size()]);
        this.goodOrder = new byte[dataArray.length];
        for (int j = 0; j < dataArray.length; j++) {
            this.goodOrder[j] = dataArray[j];
        }
        int i = 0;
        for (Byte b: this.goodOrder){
            this.goodOrder[i] = b;
            i++;
        }
    }

    public void showImage(byte[] imageData) {
        try {
            InputStream in = new ByteArrayInputStream(imageData);
            BufferedImage image = ImageIO.read(in);
            ImageIO.write(image, "jpg", new File("output.jpg"));
        } catch (IOException e) {
            gui.showError("IOException in showImage(..), Receiver class. " +
                    "Error while reading/writing to a file. " +
                    "\nError message: " + e.getMessage());
        }
    }

    public void putReceived(byte[] seq, byte[] decryptedData) {
        boolean nietAanwezig = true;
        for (Map.Entry<byte[], byte[]> e: received.entrySet()){
            if (MultiCast.byteToInt(e.getKey()) == MultiCast.byteToInt(seq)){
                nietAanwezig = false;
            }
        }
        if (nietAanwezig){
            received.put(seq, decryptedData);
        }
    }
}