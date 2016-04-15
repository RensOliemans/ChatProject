package model;

import java.security.PublicKey;

import static controller.MultiCast.HEADER;

/**
 * This object has the data for a Public Key packet and an ACK for a Public Key packet.
 * Created by Rens on 12-4-2016.
 */
public class KeyPacket {

    private int source;
    private int destination;
    private int nextHop;
    private PublicKey publicKey;
    private final int KEYPACKET = 6;
    private final int KEYACKPACKET = 7;
    private boolean isAck;

    public KeyPacket(int source, int destination, int nextHop, PublicKey publicKey, boolean isAck) {
        this.source = source;
        this.destination = destination;
        this.nextHop = nextHop;
        this.publicKey = publicKey;
        this.isAck = isAck;
    }

    public byte[] getKeyPacket() {
        byte[] keyPacket = new byte[publicKey.getEncoded().length + (4 + HEADER*4) + 1];

        //Add the indication byte to show that this is a key packet
        keyPacket[0] = (byte) (!isAck ? KEYPACKET : KEYACKPACKET);

        //Add the source, destination and nextHop to the packet
        keyPacket[1] = (byte) source;
        keyPacket[2] = (byte) destination;
        keyPacket[3] = (byte) nextHop;

        //Add the public key to the packet
        for (int i = 0; i < publicKey.getEncoded().length; i++) {
            keyPacket[4+i] = this.publicKey.getEncoded()[i];
        }

        //Add the 'Rens byte' to the packet
        keyPacket[keyPacket.length-1] = (byte) 1;

        return keyPacket;
    }


}
