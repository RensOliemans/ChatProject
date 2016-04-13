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
    private int nextHop;
    private byte[] syn;
    private String msg;
    private final int TEXTPACKET = 0;

    public TextPacket(int sourceAddress, int destinationAddress, byte[] syn, String msg, int nextHop) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.syn = syn;
        this.msg = msg;
        this.nextHop = nextHop;
    }

    public byte[] getTextPacket() {
        byte[] txpkt = new byte[(msg.length() + (4+this.syn.length))+1];

        //add the incation byte that indicates what type of packet this is
        txpkt[0] = (byte) TEXTPACKET;

        //add the source and destination to the packet
        txpkt[1] = (byte) this.sourceAddress;
        txpkt[2] = (byte) this.destinationAddress;

        //add the nextHop to the packet
        txpkt[3] = (byte) this.nextHop;

        //add the SYN to the packet
        for (int i = 0; i < this.syn.length; i++) {
            txpkt[4+i] = this.syn[i];
        }

        //add the message to the packet
        for (int i = (4+this.syn.length); i < (msg.length() + (4+this.syn.length)); i++){
            byte[] array = StringToByte(msg);
            txpkt[i] = array[i-(4+this.syn.length)];
        }

        txpkt[txpkt.length-1] = (byte) 1;

        return txpkt;
    }

    public byte[] StringToByte(String string){
        byte[] b = string.getBytes();
        return b;
    }
}