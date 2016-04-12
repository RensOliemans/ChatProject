package model;

/**
 * Created by coen on 7-4-2016.
 */
public class RoutingPacket {

    private int sourceAddress;
    private int destinationAddress;
    private int linkcost;
    private int[] data_table;
    private final int ROUTINGPACKET = 1;

    public RoutingPacket(int sourceAddress, int destinationAddress, int linkcost, int[] data_table) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.linkcost = linkcost;
        this.data_table = data_table;
    }

    public byte[] getRoutingPacket() {
        byte[] txpkt = new byte[17];

        //add the incation byte that indicates what type of packet this is
        txpkt[0] = intToByte(ROUTINGPACKET);

        //add the source and destination to the packet
        txpkt[1] = intToByte(this.sourceAddress);
        txpkt[2] = intToByte(this.destinationAddress);

        //add the linkcost to the packet
        txpkt[3] = intToByte(this.linkcost);

        //add the data_table to the packet
        for (int i = 4; i<16; i++){
            txpkt[i] = intToByte(this.data_table[i-4]);
        }

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        txpkt[16] = intToByte(1);

        return txpkt;
    }

    public byte[] intArrayToByteArray(int[] iArray){
        byte[] bArray = new byte[iArray.length];
        for (int i = 0; i<iArray.length; i++){
            bArray[i] = (byte)iArray[i];
        }
        return bArray;
    }

    public byte intToByte(int val){
        byte b = (byte)val;
        return b;
    }

}
