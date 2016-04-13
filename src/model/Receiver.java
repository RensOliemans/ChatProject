package model;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

import controller.MultiCast2;
import view.GUI;

import javax.imageio.ImageIO;

/**
 * Created by Birte on 7-4-2016.
 */
public class Receiver {

    public Boolean allReceived = false;
    private GUI gui;
    public Map<byte[], byte[]> received = new HashMap<byte[], byte[]>();
    public int sender;
    public List<Byte> goodOrderList = null;
    public byte[] goodOrder;

    public Receiver(int sender) {
        this.sender = sender;
        this.received = new HashMap<byte[], byte[]>();
        this.allReceived = false;
        this.goodOrder = null;
    }

    public void order() {
        List<Byte> result = new ArrayList<>();
        for (int i = 3; i < this.received.size() + 3; i++) {
            byte[] j = MultiCast2.intToByte(i);
            for (Map.Entry<byte[], byte[]> e : this.received.entrySet()) {
//                System.out.println("seq nummer " + MultiCast2.byteToInt(e.getKey()));
                if (Arrays.equals(e.getKey(), j)) {
                    byte[] packet = e.getValue();
                    for (int k = 0; k < packet.length; k++) {
                        result.add(packet[k]);
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
            if (MultiCast2.byteToInt(e.getKey()) == MultiCast2.byteToInt(seq)){
                nietAanwezig = false;
            }
        }
        if (nietAanwezig){
            received.put(seq, decryptedData);
        }
    }
}