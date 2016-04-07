package model;

/**
 * Created by coen on 7-4-2016.
 */
public class ForwardingPacket {

    private int sourceAddress;
    private int destinationAddress;
    private int rssi;
    private DataTable data_table;
    private final int FORWARDINGPACKET = 2;

    public ForwardingPacket(int sourceAddress, int destinationAddress, int rssi, DataTable data_table) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.rssi = rssi;
        this.data_table = data_table;
    }

    public byte[] getForwardingPacket() {
        byte[] txpkt = new byte[0];

        txpkt[0] = intToByte(FORWARDINGPACKET);
        txpkt[1] = intToByte(this.sourceAddress);
        txpkt[2] = intToByte(this.destinationAddress);
        txpkt[3] = intToByte(this.rssi);

        //TO DO: covert data_table to bytes

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
