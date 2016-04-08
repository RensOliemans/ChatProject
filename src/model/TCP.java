package model;

import controller.MultiCast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.DatagramPacket;
import java.util.List;
import java.util.Map;
import controller.MultiCast;

/**
 * Created by Rens on 5-4-2016.
 */
public class TCP {

    MultiCast multiCast = new MultiCast();
    Map<byte[], byte[]> notreceived = new HashMap<byte[], byte[]>();
    public static final int DATASIZE=128;
    public static final int HEADER = 1;
    int computernumber;
    boolean finishReceived;
    boolean firstReceived;

    public TCP(int computernumber) {
        this.computernumber = computernumber;
        this.notreceived = new HashMap<byte[], byte[]>();
        this.finishReceived = false;
        this.firstReceived = false;
    }

    public Map<byte[], byte[]> getNotreceived(){
        return notreceived;
    }

    public boolean getFinishReceived(){
        return finishReceived;
    }

    public int getComputernumber(){
        return computernumber;
    }

    public boolean getFirstReceived(){
        return firstReceived;
    }

    public List<byte[]> addSendData(List<byte[]> msg) {
        int i = 5;
        List<byte[]> result = new ArrayList<byte[]>();
        for (byte[] part: msg) {
            byte[] packet = new byte[DATASIZE + HEADER + 1];
            packet[0] = (byte) computernumber;
            byte[] header;
            header = ByteBuffer.allocate(HEADER).putInt(i).array();
            for (int k = 1; k < HEADER+1; k++){
                packet[k] = header[k];
            }
            int j = HEADER+1;
            for (byte a : part) {
                packet[j] = a;
                j = j + 1;
            }
            result.add(packet);
            notreceived.put(header, packet);
            i = i+1;
        }
        return result;
    }

    public List<byte[]> splitMessages(String msg) {
        List<byte[]> result = new ArrayList<byte[]>();
        byte[] message = msg.getBytes();
        int messagelength = message.length;
        while (messagelength > DATASIZE){
            byte[] packet = new byte[DATASIZE];
            System.arraycopy(message, message.length-messagelength, packet, 0, messagelength);
            result.add(packet);
            messagelength = messagelength - DATASIZE;
        }
        if (messagelength > 0){
            byte[] packet = new byte[messagelength];
            System.arraycopy(message, message.length-messagelength, packet, 0, messagelength);
            result.add(packet);
        }
        return result;
    }

    public void handleMessage(DatagramPacket recv) {
        byte[] data = recv.getData();
        byte[] finish = new byte[1];
        finish[0] = (byte) 0;
        if (data.equals(finish)){
            finishReceived = true;
        }
        byte[] een = new byte[1];
        een[0] = (byte) 1;
        byte[] twee = new byte[1];
        twee[0] = (byte) 2;
        byte[] drie = new byte[1];
        drie[0] = (byte) 3;
        byte[] vier = new byte[1];
        vier[0] = (byte) 4;
        if (data.equals(een)||data.equals(twee)||data.equals(drie)||data.equals(vier)){
            firstReceived = true;
        }
        else {
            notreceived.remove(data);
        }
    }
}
