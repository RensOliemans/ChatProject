package model;

import controller.MultiCast2;

import static controller.MultiCast2.HEADER;

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
    private int nextHop;

    public TextPacket(int sourceAddress, int destinationAddress, byte[] syn, String msg, int nextHop) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.syn = syn;
        this.msg = msg;
        this.nextHop = nextHop;
    }

    public byte[] getTextPacket() {
        byte[] txpkt = new byte[(msg.length() + (4+ HEADER*4))+1];

        //add the incation byte that indicates what type of packet this is
        txpkt[0] = intToByte(TEXTPACKET);

        //add the source and destination to the packet
        txpkt[1] = intToByte(this.sourceAddress);
        txpkt[2] = intToByte(this.destinationAddress);
        txpkt[3] = intToByte(this.nextHop);

        for (int i = 0; i < this.syn.length; i++) {
            txpkt[4+i] = this.syn[i];
        }

        for (int i = 0; i < this.msg.length(); i++) {
            txpkt[4+i+this.syn.length] = this.msg.getBytes()[i];
        }

        txpkt[txpkt.length-1] = intToByte(1);

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