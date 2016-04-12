package model;

/**
 * Created by coen on 7-4-2016.
 */
public class PingPacket {

    private int sourceAddress;
    private final int PINGPACKET = 2;

    public PingPacket(int sourceAddress){
        this.sourceAddress = sourceAddress;
    }

    public byte[] getPingPacket(){
        byte[] pingpacket = new byte[3];

        //add the indication byte that indicates what type of packet this is
        pingpacket[0] = intToByte(PINGPACKET);

        //add the source to the packet
        pingpacket[1] = intToByte(this.sourceAddress);

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        pingpacket[2] = intToByte(1);

        return pingpacket;
    }

    public byte intToByte(int val){
        byte b = (byte)val;
        return b;
    }

}