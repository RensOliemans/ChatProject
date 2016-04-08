package model;

/**
 * Created by coen on 8-4-2016.
 */
public class AckPacket {

    private int source;
    private int destination;
    private int ack;
    private final int ACKPACKET = 3;

    public AckPacket(int source, int destination, int ack){
        this.source = source;
        this.destination = destination;
        this.ack = ack;
    }

    public byte[] getAckPacket(){

        byte[] txpkt = new byte[5];

        //add the incation byte that indicates what type of packet this is
        txpkt[0] = intToByte(ACKPACKET);

        //add the source and destination to the packet
        txpkt[1] = intToByte(this.source);
        txpkt[2] = intToByte(this.destination);

        //add the ack to the packet
        txpkt[3] = intToByte(this.ack);

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        txpkt[4] = intToByte(1);

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