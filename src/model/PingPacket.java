package model;

/**
 * Created by coen on 7-4-2016.
 */
public class PingPacket {

    private int sourceAddress;
    private String name;
    private final int PINGPACKET = 2;

    public PingPacket(int sourceAddress, String name){
        this.sourceAddress = sourceAddress;
        this.name = name;
    }

    public byte[] getPingPacket(){
        byte[] pingpacket = new byte[(1 + name.length())];

        //add the indication byte that indicates what type of packet this is
        pingpacket[0] = intToByte(PINGPACKET);

        //add the source to the packet
        pingpacket[1] = intToByte(this.sourceAddress);

        //add the username to the packet
        for (int i = 2; i < (name.length() + 2); i++){
            byte[] array = StringToByte(name);
            pingpacket[i] = array[i-2];
        }

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        pingpacket[name.length()+2] = intToByte(1);

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
