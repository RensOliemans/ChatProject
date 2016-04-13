package model;

/**
 * Created by coen on 8-4-2016.
 */
public class FinishPacket {

    private int source;
    private int destination;
    private int nextHop;
    private final int FINISHPACKET = 5;

    public FinishPacket(int source, int destination, int nextHop){
        this.source = source;
        this.destination = destination;
        this.nextHop = nextHop;
    }

    public byte[] getFinishPacket(){

        byte[] finpacket = new byte[5];

        //add the incation byte that indicates what type of packet this is
        finpacket[0] = intToByte(FINISHPACKET);

        //add the source and destination to the packet
        finpacket[1] = intToByte(this.source);
        finpacket[2] = intToByte(this.destination);

        //add the nextHop to the packet
        finpacket[3] = (byte) this.nextHop;

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        finpacket[4] = intToByte(1);
        System.out.println("finpacket " + finpacket[0] + finpacket[1] + finpacket[2] + finpacket[3]);

        return finpacket;
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
