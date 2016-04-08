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
        byte[] txpkt = new byte[(1 + name.length())];

        //add the incation byte that indicates what type of packet this is
        txpkt[0] = intToByte(PINGPACKET);

        //add the source to the packet
        txpkt[1] = intToByte(this.sourceAddress);

        //add the username to the packet
        for (int i = 2; i < (name.length() + 2); i++){
            byte[] array = StringToByte(name);
            txpkt[i] = array[i-2];
        }

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        txpkt[name.length()+2] = intToByte(1);

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
