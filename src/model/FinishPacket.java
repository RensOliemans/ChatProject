package model;

/**
 * Created by coen on 8-4-2016.
 */
public class FinishPacket {

    private int source;
    private int destination;
    private final int FINISHPACKET = 5;

    public FinishPacket(int source, int destination){
        this.source = source;
        this.destination = destination;
    }

    public byte[] getFinishPacket(){

        byte[] finpacket = new byte[4];

        //add the incation byte that indicates what type of packet this is
        finpacket[0] = intToByte(FINISHPACKET);

        //add the source and destination to the packet
        finpacket[1] = intToByte(this.source);
        finpacket[2] = intToByte(this.destination);

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        finpacket[3] = intToByte(1);

        return finpacket;
    }

    public byte intToByte(int val){
        byte b = (byte)val;
        return b;
    }

}
