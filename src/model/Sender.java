package model;


import controller.MultiCast2;

import java.nio.ByteBuffer;
import java.util.*;
import java.net.DatagramPacket;

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

    public synchronized void removeNotReceived(byte[] key) {
//        List<byte[]> toremove = new ArrayList<>();


        Iterator<Map.Entry<byte[], byte[]>> iter = notReceived.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<byte[], byte[]> keyToCheck = iter.next();
            if (MultiCast2.byteToInt())
        }
        byte[] toRemove = new byte[key.length];
        for (Map.Entry<byte[], byte[]> e: notReceived.entrySet()){
            if (java.util.Arrays.equals(e.getKey(), key)){
                toRemove = e.getKey();
                break;
            }
        }
        notReceived.remove(toRemove);
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