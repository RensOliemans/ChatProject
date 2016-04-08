package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by coen on 7-4-2016.
 */
public class TextPacket {

    private int sourceAddress;
    private int destinationAddress;
    private int syn;
    private String msg;
    public static final int DATASIZE = 128;

    public TextPacket(int sourceAddress, int destinationAddress, int syn, String msg) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.syn = syn;
        this.msg = msg;
    }

    public byte[] getTextPacket() {
        byte[] txpkt = new byte[(4 + msg.length())];

        txpkt[0] = (byte) 0;
        txpkt[1] = intToByte(this.sourceAddress);
        txpkt[2] = intToByte(this.destinationAddress);
        txpkt[3] = intToByte(this.syn);

        for (int i = 3; i < msg.length() + 3; i++){
            byte[] array = StringToByte(msg);
            txpkt[i] = array[i-3];
        }
        return txpkt;
    }

    public byte intToByte(int val){
        byte b = (byte)val;
        return b;
    }

    public byte[] StringToByte(String string){
        byte[] b = string.getBytes();
        return b;
    }
}
