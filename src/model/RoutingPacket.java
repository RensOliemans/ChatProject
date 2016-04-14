package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by coen on 7-4-2016.
 */
public class RoutingPacket {

    private int sourceAddress;
    private int destinationAddress;
    private int linkcost;
    private int[] presence;
    private int[] data_table;
    private final int ROUTINGPACKET = 1;
    private List<Integer> receivedList = new ArrayList<>();

    public RoutingPacket(int sourceAddress, int destinationAddress, int linkcost, int[] data_table, List<Integer> receivedList) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.linkcost = linkcost;
        this.data_table = data_table;
        this.receivedList = receivedList;
    }

    private void setPresence(List<Integer> list){
        for (int i=0; i<list.size(); i++){
            presence[i] = list.get(i);
        }
        if (presence.length<4){
            for (int i=0; i<4-presence.length; i++){
                presence[presence.length+i]=0;
            }
        }
    }

    public byte[] getRoutingPacket() {

        setPresence(receivedList);

        byte[] txpkt = new byte[21];

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

        //add the receivedList to the packet
        for(int i=16; i<20; i++){
            txpkt[i] = (byte) presence[i-16];
        }

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        txpkt[20] = intToByte(1);

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
