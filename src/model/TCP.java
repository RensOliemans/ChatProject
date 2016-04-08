package model;

import controller.MultiCast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rens on 5-4-2016.
 */
public class TCP {

    MultiCast multiCast = new MultiCast();
    Map<byte[], byte[]> notreceived = new HashMap<byte[], byte[]>();
    public static final int DATASIZE=128;
    public static final int HEADER = 3;
    public static final int RENSBYTE = 1; //Byte placed at the end of a message for removal of padding
    int destination;
    boolean finishReceived;
    boolean firstReceived;

    public TCP(int destination) {
        this.destination = destination;
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

    public int getDestination(){
        return destination;
    }

    public boolean getFirstReceived(){
        return firstReceived;
    }

    public List<byte[]> addSendData(List<byte[]> msg) {
        int i = 5;
        List<byte[]> result = new ArrayList<byte[]>();
        for (byte[] part: msg) {
            byte[] packet = new byte[DATASIZE + HEADER + 1];
            packet[0] = (byte) destination;
            byte[] syn;
            syn = ByteBuffer.allocate(HEADER*4).putInt(i).array();
            for (int k = 1; k < HEADER+1; k++){
                packet[k] = syn[k];
            }
            int j = HEADER+1;
            for (byte a : part) {
                packet[j] = a;
                j = j + 1;
            }
            result.add(packet);
            notreceived.put(syn, part);
//            notreceived.put(syn, packet);
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

    public void handleMessage(byte[] data) {
//        byte[] data = recv.getData();
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
        for (byte b : data) {
            System.out.println(b);
        }
        if (data[1] == this.destination) {
            System.out.println("FirstReceived = true");
            firstReceived = true;
        }
        else {
            notreceived.remove(data);
        }
    }
}
