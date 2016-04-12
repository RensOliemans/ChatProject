package model;

import controller.MultiCast2;

import java.nio.channels.MulticastChannel;

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
        startpacket[0] = intToByte(STARTPACKET);

        //add the source and destination to the packet
        startpacket[1] = intToByte(this.source);
        startpacket[2] = intToByte(this.destination);
        startpacket[3] = intToByte(this.nextHop);

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        byte[] seq = MultiCast2.intToByte(1);

        return startpacket;
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
