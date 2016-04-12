package model;


import controller.MultiCast2;

import java.nio.ByteBuffer;
import java.util.*;
import java.net.DatagramPacket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Rens on 5-4-2016.
 */
public class Sender {

    //HashMap with Key: SYN and Value: Message, just message, no header
    public Map<byte[], byte[]> notReceived = new ConcurrentHashMap<byte[], byte[]>();
    int f = 0;
    private int receiver;
    public Lock senderLock = new ReentrantLock();
    public boolean finishReceived;
    public boolean firstReceived;
    public List<byte[]> received;

    public Sender(int receiver) {
        this.received = new ArrayList<>();
        this.receiver = receiver;
        this.notReceived = new HashMap<byte[], byte[]>();
        this.finishReceived = false;
        this.firstReceived = false;
    }

    public synchronized void putNotReceived(byte[] key, byte[] value) {
        this.notReceived.put(key, value);
    }

    public synchronized void removeNotReceived(byte[] key) {
        senderLock.lock();
        byte[] toRemove = null;
//        notReceived.remove(key);
//        System.out.println(notReceived.containsKey(key));
        for (Map.Entry<byte[], byte[]> entry : notReceived.entrySet()) {
            toRemove = entry.getKey();
            Object value = entry.getValue();
            if (MultiCast2.byteToInt(toRemove) == MultiCast2.byteToInt(key)) {
                break;
            }
        }
        notReceived.remove(toRemove);
        senderLock.unlock();
    }

    public Map<byte[], byte[]> getNotReceived() {
        senderLock.lock();
        Map<byte[], byte[]> result = notReceived;
        senderLock.unlock();
        return result;
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