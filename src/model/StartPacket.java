package model;

/**
 * Created by coen on 8-4-2016.
 */
public class StartPacket {

    private int source;
    private int destination;
    private final int STARTPACKET = 3;
    private int nextHop;

    public StartPacket(int source, int destination, int nextHop){
        this.source = source;
        this.destination = destination;
        this.nextHop = nextHop;
    }

    public byte[] getStartPacket(){

        byte[] startpacket = new byte[5];

        //add the incation byte that indicates what type of packet this is
        startpacket[0] = STARTPACKET;

        //add the source and destination to the packet
        startpacket[1] = (byte) this.source;
        startpacket[2] = (byte) this.destination;
        startpacket[3] = (byte) this.nextHop;

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        startpacket[4] = 1;

        return startpacket;
    }

}
