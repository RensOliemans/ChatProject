package model;

/**
 * Created by coen on 8-4-2016.
 */
public class AckPacket {

    private int source;
    private int destination;
    private byte[] ack;
    private int nextHop;
    private final int ACKPACKET = 4;

    public AckPacket(int source, int destination, byte[] ack, int nextHop){
        this.source = source;
        this.destination = destination;
        this.ack = ack;
        this.nextHop = nextHop;
    }

    public byte[] getAckPacket(){

        byte[] ackpkt = new byte[4+this.ack.length];

        //add the indication byte that indicates what type of packet this is
        ackpkt[0] = (byte) ACKPACKET;

        //add the source and destination to the packet
        ackpkt[1] = (byte) this.source;
        ackpkt[2] = (byte) this.destination;
        ackpkt[3] = (byte) this.nextHop;

        //add the ack to the packet
        for (int i = 0; i < this.ack.length; i++) {
            ackpkt[4+i] = this.ack[i];
        }

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        ackpkt[ackpkt.length-1] = (byte) 1;

        return ackpkt;
    }

}