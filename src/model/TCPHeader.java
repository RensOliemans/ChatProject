package model;

/**
 * Created by Rens on 5-4-2016.
 */
public class TCPHeader {

    private int seq;
    private int ack;

    public TCPHeader(int seq, int ack) {
        this.seq = seq;
        this.ack = ack;
    }

    public int getSeq() {
        return seq;
    }

    public int getAck() {
        return ack;
    }
}
