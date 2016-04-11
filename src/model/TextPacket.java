package model;

import controller.MultiCast2;

/**
 * Created by coen on 7-4-2016.
 * coen is best wel goed
 */

public class TextPacket {

    private int sourceAddress;
    private int destinationAddress;
    private byte[] syn;
    private String msg;
    private final int TEXTPACKET = 0;

    public TextPacket(int sourceAddress, int destinationAddress, byte[] syn, String msg) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.syn = syn;
        this.msg = msg;
    }

    public byte[] getTextPacket() {
        byte[] txpkt = new byte[(3 + msg.length()) + MultiCast2.HEADER*4 + 1];

        //add the incation byte that indicates what type of packet this is
        txpkt[0] = intToByte(TEXTPACKET);

        //add the source and destination to the packet
        txpkt[1] = intToByte(this.sourceAddress);
        txpkt[2] = intToByte(this.destinationAddress);
        for (int i = 0; i < this.syn.length; i++) {
            txpkt[3+i] = this.syn[i];
        }

        //add message into the packet
        for (int i = (3+MultiCast2.HEADER*4); i < (msg.length() + (3+MultiCast2.HEADER*4)); i++){
            byte[] array = StringToByte(msg);
            System.out.println(i);
            txpkt[i] = array[i-(3+MultiCast2.HEADER*4)];
        }

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        txpkt[(msg.length() + (3+MultiCast2.HEADER*4))] = intToByte(1);

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
