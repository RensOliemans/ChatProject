package model;

/**
 * Created by coen on 7-4-2016.
 */
public class TextPacket {

    private int sourceAddress;
    private int destinationAddress;
    private int syn;
    private String msg;
    private final int TEXTPACKET = 0;

    public TextPacket(int sourceAddress, int destinationAddress, int syn, String msg) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.syn = syn;
        this.msg = msg;
    }

    public byte[] getTextPacket() {
        byte[] txpkt = new byte[(3 + msg.length())];

        txpkt[0] = intToByte(TEXTPACKET);
        txpkt[1] = intToByte(this.sourceAddress);
        txpkt[2] = intToByte(this.destinationAddress);
        txpkt[3] = intToByte(this.syn);

        for (int i = 4; i < msg.length() + 4; i++){
            byte[] array = StringToByte(msg);
            txpkt[i] = array[i-4];
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
