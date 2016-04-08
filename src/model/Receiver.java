package model;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import view.GUI;

/**
 * Created by Birte on 7-4-2016.
 */
public class Receiver {

    public Boolean allReceived = false;
    public Map<byte[], byte[]> received = new HashMap<byte[], byte[]>();
    public int sender;
    public List<Byte> goodOrder = null;

    public Receiver(int sender) {
        this.sender = sender;
        this.received = new HashMap<byte[], byte[]>();
        this.allReceived = false;
        this.goodOrder = null;
    }

    public void order(){
        List<Byte> result = new ArrayList<>();
        for (int i = 2; i < this.received.size()+1; i++) {
            for (Map.Entry<byte[], byte[]> e : this.received.entrySet()) {
                if (e.getKey().equals(i)) {
                    byte[] packet = e.getValue();
                    for (int k = 0; k < packet.length; k++) {
                        result.add(packet[k]);
                    }
                }
            }
        }
        goodOrder = result;
    }
}

