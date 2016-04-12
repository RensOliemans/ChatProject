package model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import controller.MultiCast2;
import view.GUI;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Created by Birte on 7-4-2016.
 */
public class Receiver {

    public Boolean allReceived = false;
    private GUI gui;
    public Map<byte[], byte[]> received = new HashMap<byte[], byte[]>();
    public int sender;
    public List<Byte> goodOrder = null;

    public Receiver(int sender) {
        this.sender = sender;
        this.received = new HashMap<byte[], byte[]>();
        this.allReceived = false;
        this.goodOrder = null;
    }

    public void order() {
        List<Byte> result = new ArrayList<>();
        for (int i = 2; i < this.received.size() + 2; i++) {
            byte[] j = MultiCast2.intToByte(i);
            for (Map.Entry<byte[], byte[]> e : this.received.entrySet()) {
//                System.out.println("seq nummer " + MultiCast2.byteToInt(e.getKey()));
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
            InputStream in = new ByteArrayInputStream(imageData);
            BufferedImage image = ImageIO.read(in);
            ImageIO.write(image, "jpg", new File("output.jpg"));
        } catch (IOException e) {
            gui.showError("IOException in showImage(..), Receiver class. " +
                    "Error while reading/writing to a file. " +
                    "\nError message: " + e.getMessage());
        }
    }
}