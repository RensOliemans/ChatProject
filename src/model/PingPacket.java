package model;

/**
 * Created by coen on 7-4-2016.
 */
public class PingPacket {

    private int sourceAddress;
    private String name;
    private final int PINGPACKET = 1;

    public PingPacket(int sourceAddress, String name){
        this.sourceAddress = sourceAddress;
        this.name = name;
    }

    public byte[] getPingPacket(){
        byte[] pingPacket = new byte[(1 + name.length())];

        //add the incation byte that indicates what type of packet this is
        pingPacket[0] = (byte) PINGPACKET;

        //add the source to the packet
        pingPacket[1] = (byte) this.sourceAddress;

        //add the username to the packet
        for (int i = 2; i < (name.length() + 2); i++){
            byte[] array = name.getBytes();
            pingPacket[i] = array[i-2];
        }

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        pingPacket[name.length()+2] = (byte) 1;

        return pingPacket;
    }

//    public byte intToByte(int val){
//        byte b = (byte)val;
//        return b;
//    }
//
//    public byte[] StringToByte(String string){
//        byte[] b = string.getBytes();
//        return b;
//    }

}
