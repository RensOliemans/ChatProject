package model;

/**
 * Created by coen on 5-4-2016.
 */
public class Packet {

    private int sourceAddress;
    private int destinationAddress;
    private DataTable data;

    public Packet(int sourceAddress, int destinationAddress, DataTable data) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.data = data;
    }

    public int getSourceAddress() {
        return sourceAddress;
    }

    public int getDestinationAddress() {
        return destinationAddress;
    }

    public DataTable getData() {
        return data;
    }

}
