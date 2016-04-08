package model;

/**
 * Created by coen on 8-4-2016.
 */
public class AckPacket {

    private int source;
    private int destination;
    private byte[] ack;
    private final int ACKPACKET = 4;

    public AckPacket(int source, int destination, byte[] ack){
        this.source = source;
        this.destination = destination;
        this.ack = ack;
    }

    public byte[] getAckPacket(){

        byte[] txpkt = new byte[4+this.ack.length];

        //add the indication byte that indicates what type of packet this is
        txpkt[0] = (byte) ACKPACKET;

        //add the source and destination to the packet
        txpkt[1] = (byte) this.source;
        txpkt[2] = (byte) this.destination;

        //add the ack to the packet
        for (int i = 0; i < this.ack.length; i++) {
            txpkt[3+i] = this.ack[i];
        }

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        txpkt[txpkt.length-1] = (byte) 1;

        return txpkt;
    }
//
//    public byte intToByte(int val){
//        byte b = (byte)val;
//        return b;
//    }
//
//    public byte[] StringToByte(String string){
//        byte[] b = string.getBytes();
//        return b;
//    }

}
