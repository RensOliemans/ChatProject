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

    public TextPacket(int sourceAddress, int destinationAddress, byte[] syn, String msg) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.syn = syn;
        this.msg = msg;
    }

    public byte[] getTextPacket() {
        byte[] txpkt = new byte[(msg.length() + (3+ HEADER*4))+1];

        //add the incation byte that indicates what type of packet this is
        txpkt[0] = intToByte(TEXTPACKET);

        //add the source and destination to the packet
        txpkt[1] = intToByte(this.sourceAddress);
        txpkt[2] = intToByte(this.destinationAddress);

        for (int i = 0; i < this.syn.length; i++) {
            txpkt[3+i] = this.syn[i];
        }

        //add the SYN number to the packet (not sure if this works)
//        if (HEADER == 1){
//            txpkt[2+ HEADER] = intToByte(this.syn - ((HEADER-1)*256));
//        } else {
//            for (int j = 3; j< HEADER + 2; j++){
//                txpkt[j] = intToByte(256);
//                txpkt[2+ HEADER] = intToByte(this.syn - ((HEADER-1)*256));
//            }
//        }

        /*
        //add the SYN number to the packet (sure that this works, but this isn't scalable)
        if(MultiCast2.HEADER == 1){
            txpkt[3] = intToByte(this.syn);
        }
        if(MultiCast2.HEADER == 2){
            txpkt[3] = intToByte(256);
            txpkt[4] = intToByte(this.syn - 256);
        }
        if(MultiCast2.HEADER == 3){
            txpkt[3] = intToByte(256);
            txpkt[4] = intToByte(256);
            txpkt[5] = intToByte(this.syn - 512);
        }
        if(MultiCast2.HEADER == 4){
            txpkt[3] = intToByte(256);
            txpkt[4] = intToByte(256);
            txpkt[5] = intToByte(256);
            txpkt[6] = intToByte(this.syn - 768);
        }
        if(MultiCast2.HEADER > 4){
            throw new IndexOutOfBoundsException();
        }
        */

        //add message into the packet
        for (int i = (3+HEADER*4); i < (msg.length() + (3+HEADER*4)); i++){
            byte[] array = msg.getBytes();
            txpkt[i] = array[i-(3+ HEADER*4)];
        }

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
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