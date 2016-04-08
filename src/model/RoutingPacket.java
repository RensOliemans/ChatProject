package model;

/**
 * Created by coen on 7-4-2016.
 */
public class RoutingPacket {

    private int sourceAddress;
    private int destinationAddress;
    private int linkcost;
    private DataTable data_table;
    private final int ROUTINGPACKET = 2;

    public RoutingPacket(int sourceAddress, int destinationAddress, int rssi, DataTable data_table) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.linkcost = linkcost;
        this.data_table = data_table;
    }

    public byte[] getRoutingPacket() {
        byte[] routingpacket = new byte[0];

        //add the incation byte that indicates what type of packet this is
        routingpacket[0] = intToByte(ROUTINGPACKET);

        //add the source and destination to the packet
        routingpacket[1] = intToByte(this.sourceAddress);
        routingpacket[2] = intToByte(this.destinationAddress);

        //add the linkcost to the packet
        routingpacket[3] = intToByte(this.linkcost);



        //TODO: covert data_table to bytes



        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
//        routingpacket[/*laaste byte*/] rens = intToByte(1);

        return routingpacket;
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

    public byte[] StringToByte(String string){
        byte[] b = string.getBytes();
        return b;
    }

}
