package model;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.DatagramPacket;
import java.util.List;
import java.util.Map;

/**
 * Created by Rens on 5-4-2016.
 */
public class Sender {

    //HashMap with Key: SYN and Value: Message, just message, no header
    private Map<byte[], byte[]> notReceived = new HashMap<byte[], byte[]>();
    private int receiver;
    public boolean finishReceived;
    public boolean firstReceived;

    public Sender(int receiver) {
        this.receiver = receiver;
        this.notReceived = new HashMap<byte[], byte[]>();
        this.finishReceived = false;
        this.firstReceived = false;
    }

    public void putNotReceived(byte[] key, byte[] value) {
        this.notReceived.put(key, value);
    }

    public void removeNotReceived(byte[] key) {
        for (Map.Entry<byte[], byte[]> e: notReceived.entrySet()){
            if (java.util.Arrays.equals(e.getKey(), key)){
                notReceived.remove(e.getKey());
            }
        }
        //this.notReceived.remove(key);
    }

    public Map<byte[], byte[]> getNotReceived() {
        return notReceived;
    }

    public void setFirstReceivedTrue(){
        this.firstReceived = true;
    }

    public void setFinishReceivedTrue(){
        this.finishReceived = true;
    }

    //    public void handleMessage(byte[] data) {
////        byte[] data = recv.getData();
//        byte[] finish = new byte[1];
//        finish[0] = (byte) 0;
//        if (data.equals(finish)){
//            finishReceived = true;
//        }
//        byte[] een = new byte[1];
//        een[0] = (byte) 1;
//        byte[] twee = new byte[1];
//        twee[0] = (byte) 2;
//        byte[] drie = new byte[1];
//        drie[0] = (byte) 3;
//        byte[] vier = new byte[1];
//        vier[0] = (byte) 4;
//        if (data[1] == 1||data.equals(twee)||data.equals(drie)||data.equals(vier)){
//            firstReceived = true;
//        }
//        else {
//            notreceived.remove(data);
//        }
//    }
}