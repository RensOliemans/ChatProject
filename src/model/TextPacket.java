package model;

import static controller.MultiCast.HEADER;

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
        this.nextHop = nextHop;
        this.syn = syn;
        this.msg = msg;
    }

    public byte[] getTextPacket() {
        byte[] txpkt = new byte[(msg.length() + (4+ HEADER*4))+1];

        //add the incation byte that indicates what type of packet this is
        txpkt[0] = TEXTPACKET;

        //add the source and destination to the packet
        txpkt[1] = (byte) this.sourceAddress;
        txpkt[2] = (byte) this.destinationAddress;

        //add the nextHop to the packet
        txpkt[3] = (byte) this.nextHop;

        //add the SYN to the packet
        System.arraycopy(this.syn, 0, txpkt, 4, this.syn.length);

        for (int i = 0; i < this.msg.length(); i++) {
            txpkt[4+i+this.syn.length] = this.msg.getBytes()[i];
        }

        txpkt[txpkt.length-1] = 1;

        return txpkt;
    }
}