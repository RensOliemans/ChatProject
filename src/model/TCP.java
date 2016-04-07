package model;

import controller.MultiCast;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import controller.MultiCast;

/**
 * Created by Rens on 5-4-2016.
 */
public class TCP {

    MultiCast multiCast = new MultiCast();
    Map<byte[], byte[]> notreceived = new HashMap<byte[], byte[]>();
    Map<byte[], byte[]> received = new HashMap<byte[], byte[]>();
    static final int DATASIZE=128;
    static final int HEADER = 1;

    public TCP() {
        this.notreceived = new HashMap<byte[], byte[]>();
        this.received = new HashMap<byte[], byte[]>();
    }

    public Map<byte[], byte[]> getNotreceived(){
        return notreceived;
    }

    public Map<byte[], byte[]> getReceived(){
        return received;
    }

    public List<byte[]> addSendData(List<byte[]> msg) {
        int i = 1;
        byte[] packet = new byte[DATASIZE + HEADER];
        List<byte[]> result = new ArrayList<byte[]>();
        for (byte[] part: msg) {
            packet = new byte[DATASIZE + HEADER];
            byte[] header;
            header = ByteBuffer.allocate(HEADER*4).putInt(i).array();
            for (int k = 0; k < HEADER; k++){
                packet[k] = header[k];
            }
            int j = HEADER;
            for (byte a : part) {
                packet[j] = a;
                j = j + 1;
            }
            i = i+1;
         }
        result.add(packet);
        byte[] bericht = new byte[HEADER];
        for (int j = 0; j < HEADER; j++) {
            bericht[j] = packet[j];
        }
        notreceived.put(bericht, packet);
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
        if (data.equals((byte)0)){
            List<Byte> bericht = order();

        }
        if (data.length > HEADER) {
            byte[] header = new byte[HEADER];
            for (int i = 0; i < HEADER; i++){
                header[i] = data[i];
            }
            received.put(header, data);
            this.multiCast.sendack(header);
        }
        else{
            notreceived.remove(data);
            if (notreceived == null){
                byte[] finish = new byte[1];
                finish[0]=(byte)0;
                this.multiCast.sendack(finish);
            }
        }
    }
}
