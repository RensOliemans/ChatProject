package model;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import controller.MultiCast;
import view.GUI;

/**
 * Created by Birte on 7-4-2016.
 */
public class TCPReceive {

    MultiCast multiCast = new MultiCast();
    Boolean allReceived = false;
    Map<byte[], byte[]> received = new HashMap<byte[], byte[]>();
    static final int HEADER = 1;
    private int computernumber;
    List<Byte> goodOrder = null;
    private GUI gui = new GUI();

    public TCPReceive(int computernumber){
        this.computernumber = computernumber;
        this.received = new HashMap<byte[], byte[]>();
        this.allReceived = false;
        this.goodOrder = null;
    }

    public Map<byte[], byte[]> getReceived(){
        return received;
    }

    public boolean getAllReceived(){
        return allReceived;
    }

    public int getComputernumber(){
        return computernumber;
    }

    public List<Byte> order(){
        List<Byte> result = new ArrayList<Byte>();
        for (int i = 1; i < getReceived().size(); i++) {
            for (Map.Entry<byte[], byte[]> e : getReceived().entrySet()) {
                if (e.getKey().equals((ByteBuffer.allocate(HEADER).putInt(i).array()))) {
                    byte[] packet = new byte[e.getValue().length - HEADER];
                    for (int k = HEADER; k < e.getValue().length; k++) {
                        result.add(e.getValue()[k]);
                    }
                }
            }
        }
        return result;
    }

    public void handleMessage(DatagramPacket recv) {
        byte[] data = recv.getData();
        byte[] finish = new byte[3];
        finish[0] = (byte) multiCast.getComputerNumber();
        finish[1] = 0;
        finish[2] = 0;
        if (data[1] == 0 && data.length ==2){
            this.goodOrder = order();
            allReceived = true;
            this.multiCast.sendack(finish);
            gui.printMessage(this.goodOrder, getComputernumber());
        } else if ((data[0] == 1 || data[0] == 2 || data[0] == 3 || data[0] == 4) && data[1] == data[0] && data.length == 2) {

        } else {
            byte[] header = new byte[HEADER];
            for (int i = 0; i < HEADER; i++){
                header[i] = data[i];
            }
            received.put(header, data);
            this.multiCast.sendack(header);
        }
    }
}


