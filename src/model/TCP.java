package model;

import controller.MultiCast;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rens on 5-4-2016.
 */
public class TCP {

    MultiCast multiCast = new MultiCast();
    Map<byte[], byte[]> berichten = new HashMap<byte[], byte[]>();
    static final int DATASIZE=128;

    public TCP(Map<byte[], byte[]> berichten) {
        this.berichten = berichten;
    }


    public byte[] addSendData(List<byte[]> msg) {
        for (int i = 0; i < msg.size(); i++){
            TCPHeader header = new TCPHeader(1,2);
        }

        //A d 1 byte SEQ to the front
        //Ades1 byte ACK to the front
        //Rest of message stays the same.
        return null;
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
        if (data[0]!=0){

        }
        //byte[0] is ACK
        //byte[1] is SEQ
        this.multiCast.send("");
    }
}
