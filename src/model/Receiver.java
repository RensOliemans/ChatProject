package model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.MultiCast2;

import javax.imageio.ImageIO;

/**
 * Created by Birte on 7-4-2016.
 */
public class Receiver {

    public Boolean allReceived = false;
    public Map<byte[], byte[]> received = new HashMap<byte[], byte[]>();
    public int sender;
    public List<E> goodOrder = null;

    public Receiver(int sender) {
        this.sender = sender;
        this.received = new HashMap<byte[], byte[]>();
        this.allReceived = false;
        this.goodOrder = null;
    }

    public void order() {
        List<Object> result = new ArrayList<>();
        for (int i = 2; i < this.received.size() + 2; i++) {
            byte[] j = MultiCast2.intToByte(i);
            for (Map.Entry<byte[], byte[]> e : this.received.entrySet()) {
                System.out.println("seq nummer " + MultiCast2.byteToInt(e.getKey()));
                if (MultiCast2.byteToInt(e.getKey()) == MultiCast2.byteToInt(j)) {
                    byte[] packet = e.getValue();
                    for (int k = 0; k < packet.length; k++) {
                        result.add(packet[k]);
                    }
                }
            }
        }
        goodOrder = result;
    }

    public void showImage(byte[] imageData) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            ImageIO.write(image, "jpg", new File("image.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}