package model;

/**
 * Created by coen on 7-4-2016.
 */
public class PingPacket {

    private int destination;
    private int nextHop;
    private int sourceAddress;
    private final int PINGPACKET = 2;

    public PingPacket(int sourceAddress, int destination, int nextHop){
        this.sourceAddress = sourceAddress;
        this.destination = destination;
        this.nextHop = nextHop;
    }

    public byte[] getPingPacket(){
        byte[] pingpacket = new byte[5];

        //add the indication byte that indicates what type of packet this is
        pingpacket[0] = intToByte(PINGPACKET);

        //add the source to the packet
        pingpacket[1] = intToByte(this.sourceAddress);
        pingpacket[2] = intToByte(this.destination);
        pingpacket[3] = intToByte(this.nextHop);

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        pingpacket[4] = intToByte(1);

        return pingpacket;
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
