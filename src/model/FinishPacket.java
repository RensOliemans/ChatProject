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

        byte[] txpkt = new byte[4];

        //add the incation byte that indicates what type of packet this is
        txpkt[0] = intToByte(FINISHPACKET);

        //add the source and destination to the packet
        txpkt[1] = intToByte(this.source);
        txpkt[2] = intToByte(this.destination);

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        txpkt[3] = intToByte(1);

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