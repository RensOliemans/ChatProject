package model;

/**
 * Created by coen on 5-4-2016.
 */
public class Packet {

    private int sourceAddress;
    private int destinationAddress;
    private DataTable data_table;
    private int rssi;
    private String text;

    public Packet(int sourceAddress, int destinationAddress, DataTable data_table, int rssi, String text) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.data_table = data_table;
        this.rssi = rssi;
        this.text = text;
    }

    public int getSourceAddress() {
        return sourceAddress;
    }

    public int getDestinationAddress() {
        return destinationAddress;
    }

    public DataTable getData_table() {
        return data_table;
    }

    public int getRssi() {return rssi;}

    public String getText() {return text;}

}
