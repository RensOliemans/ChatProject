package model;

import controller.MultiCast;

import java.net.DatagramPacket;
import java.util.List;

/**
 * Created by Rens on 5-4-2016.
 */
public class TCP {

    MultiCast multiCast = new MultiCast();

    public TCP() {
    }


    public byte[] addSendData(byte[] msg) {
        //Add 1 byte SEQ to the front
        //Add 1 byte ACK to the front
        //Rest of message stays the same.
        return null;
    }

    public List<byte[]> splitMessages(String msg) {
        return null;
    }

    public void handleMessage(DatagramPacket recv) {
        byte[] data = recv.getData();
        //byte[0] is ACK
        //byte[1] is SEQ
        this.multiCast.send("",0);
    }
}
