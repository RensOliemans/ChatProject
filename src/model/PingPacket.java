package model;

/**
 * Created by coen on 7-4-2016.
 */
public class PingPacket {

    private int sourceAddress;
    private String name;

    public PingPacket(int sourceAddress, String name){
        this.sourceAddress = sourceAddress;
        this.name = name;
    }

    public byte[] getPingPacket(){
        byte[] txpkt = new byte[(1 + name.length())];

        txpkt[0] = intToByte(this.sourceAddress);

        for (int i = 1; i < name.length() + 3; i++){
            byte[] array = StringToByte(name);
            txpkt[i] = array[i-1];
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
