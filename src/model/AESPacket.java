package model;

import static controller.MultiCast2.HEADER;

/**
 * Created by Rens on 12-4-2016.
 */
public class AESPacket {

    private int source;
    private int destination;
    private int nextHop;
    private byte[] EncryptedAESKey;
    private final int AESKEYPACKET = 8;

    public AESPacket(int source, int destination, int nextHop, byte[] AESKey) {
        this.source = source;
        this.destination = destination;
        this.nextHop = nextHop;
        this.EncryptedAESKey = AESKey;
    }

    public byte[] getAESPacket() {
        byte[] AESPacket = new byte[EncryptedAESKey.length + 4 + 1];

        //Add the indication byte to show that it is an AESpacket
        AESPacket[0] = (byte) AESKEYPACKET;

        //Add the source, destination and nextHop
        AESPacket[1] = (byte) source;
        AESPacket[2] = (byte) destination;
        AESPacket[3] = (byte) nextHop;

        //Add the AES key to the packet
        for (int i = 0; i < EncryptedAESKey.length; i++) {
            AESPacket[4+i] = EncryptedAESKey[i];
        }

        //Add the 'Rens byte' to the end of the packet
        AESPacket[AESPacket.length-1] = (byte) 1;

        return AESPacket;
    }
}