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

        txpkt[0] = intToByte(PINGPACKET);
        txpkt[1] = intToByte(this.sourceAddress);

        for (int i = 2; i < name.length() + 2; i++){
            byte[] array = StringToByte(name);
            txpkt[i] = array[i-2];
        }
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
