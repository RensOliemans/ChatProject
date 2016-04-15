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
    private int[] presence = new int[4];
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
    }

    public byte[] getRoutingPacket() {

        setPresence(receivedList);

        byte[] txpkt = new byte[21];

        //add the incation byte that indicates what type of packet this is
        txpkt[0] = (byte) ROUTINGPACKET;

        //add the source and destination to the packet
        txpkt[1] = (byte) this.sourceAddress;
        txpkt[2] = (byte) this.destinationAddress;

        //add the linkcost to the packet
        txpkt[3] = (byte) this.linkcost;

        //add the data_table to the packet
        for (int i = 4; i<16; i++){
            txpkt[i] = (byte) this.data_table[i-4];
        }

        //add the receivedList to the packet
        for(int i=16; i<20; i++){
            txpkt[i] = (byte) presence[i-16];
        }

        //add the "Rens-bit" as last bit to the packet
        //this is for padding purposes
        txpkt[20] = (byte) 1;

        return txpkt;
    }

}
