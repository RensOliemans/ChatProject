package model;

import static controller.MultiCast.HEADER;

/**
 * This object has the data for an File packet.
 * Created by Rens on 13-4-2016.
 */
class FilePacket {

    private int sourceAddress;
    private int destinationAddress;
    private byte[] syn;
    private byte[] data;
    private final int FILEPACKET = 9;
    private int nextHop;

    public FilePacket(int sourceAddress, int destinationAddress, byte[] syn, byte[] data, int nextHop) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.syn = syn;
        this.data = data;
        this.nextHop = nextHop;
    }

    public byte[] getFilePacket() {
        byte[] filePkt = new byte[(data.length) + 4 + HEADER*4 + 1];

        //Add the indication byte that indicates what type of packet this is
        filePkt[0] = (byte) FILEPACKET;

        //Add the source and destination to the packet
        filePkt[1] = (byte) this.sourceAddress;
        filePkt[2] = (byte) this.destinationAddress;

        //Add the nexthop to the packet
        filePkt[3] = (byte) this.nextHop;

        //Add the SYN to the packet
        System.arraycopy(syn, 0, filePkt, 4, syn.length);

        //Add the data to the packet
        System.arraycopy(data, 0, filePkt, 4 + syn.length, data.length);

        //Add the 'Rens byte' to the packet
        filePkt[filePkt.length-1] = (byte) 1;

        return filePkt;
    }
}
