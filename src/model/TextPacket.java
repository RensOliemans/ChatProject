package model;

import controller.MultiCast2;

/**
 * Created by coen on 7-4-2016.
 * coen is best wel goed
 */

public class TextPacket {

    private int sourceAddress;
    private int destinationAddress;
    private int syn;
    private byte[] msg;
    private final int TEXTPACKET = 0;

    public TextPacket(int sourceAddress, int destinationAddress, int syn, byte[] msg) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.syn = syn;
        this.msg = msg;
    }

    public byte[] getTextPacket() {
        byte[] txpkt = new byte[(3 + msg.length) + MultiCast2.HEADER + 1];

        //add the incation byte that indicates what type of packet this is
        txpkt[0] = intToByte(TEXTPACKET);

        //add the source and destination to the packet
        txpkt[1] = intToByte(this.sourceAddress);
        txpkt[2] = intToByte(this.destinationAddress);

        //add the SYN number to the packet (not sure if this works)
        for (int j = 3; j< MultiCast2.HEADER + 2; j++){
            txpkt[j] = intToByte(256);
            txpkt[2+MultiCast2.HEADER] = intToByte(this.syn - ((MultiCast2.HEADER-1)*256));
        }

        /*
        //add the SYN number to the packet (sure that this works, but this isn't scalable)
        if(TCP.HEADER == 1){
            txpkt[3] = intToByte(this.syn);
        }
        if(TCP.HEADER == 2){
            txpkt[3] = intToByte(256);
            txpkt[4] = intToByte(this.syn - 256);
        }
        if(TCP.HEADER == 3){
            txpkt[3] = intToByte(256);
            txpkt[4] = intToByte(256);
            txpkt[5] = intToByte(this.syn - 512);
        }
        if(TCP.HEADER == 4){
            txpkt[3] = intToByte(256);
            txpkt[4] = intToByte(256);
            txpkt[5] = intToByte(256);
            txpkt[6] = intToByte(this.syn - 768);
        }
        if(TCP.HEADER > 4){
            throw new IndexOutOfBoundsException();
        }
        */

        //add message into the packet
        for (int i = (3+MultiCast2.HEADER); i < (msg.length + (3+MultiCast2.HEADER)); i++){
            byte[] array = msg;
            txpkt[i] = array[i-(3+MultiCast2.HEADER)];
        }

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        txpkt[(msg.length + (3+MultiCast2.HEADER))] = intToByte(1);

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
