package model;

/**
 * This object has the data for a PINGpacket.
 * Created by coen on 7-4-2016.
 */
public class PingPacket {

    private int sourceAddress;
    private final int PINGPACKET = 2;

    public PingPacket(int sourceAddress){
        this.sourceAddress = sourceAddress;
    }

    public byte[] getPingPacket(){
        byte[] pingPacket = new byte[3];

        //add the indication byte that indicates what type of packet this is
        pingPacket[0] = (byte) PINGPACKET;

        //add the source to the packet
        pingPacket[1] = (byte) this.sourceAddress;

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        pingPacket[2] = (byte) 1;

        return pingPacket;
    }


}