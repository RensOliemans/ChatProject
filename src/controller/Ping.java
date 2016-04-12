package controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rens on 7-4-2016.
 */
public class Ping implements Runnable{

    private MultiCast2 multiCast = new MultiCast2();
    private int computerNumber;

    private int receivedPing = 0;
    private long seconds1;
    private long seconds2;
    public List presence = new ArrayList<>();
    private boolean responseSent = false;

    public Ping(int computerNumber) {
        this.computerNumber = computerNumber;
    }

    private void ping() {
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            multiCast.sendPing(this.computerNumber, "oooo");
        }

    }

    public int calculateReceivedPings(int data){
        if (!presence.contains(data) && data != 0){
            presence.add(data);
        }
        if (receivedPing == 0){
            seconds1 = System.currentTimeMillis();
            receivedPing ++;
        } else {
            seconds2 = System.currentTimeMillis();
            receivedPing ++;
        }
        if ((seconds2 - seconds1 > 3000) && (receivedPing != 0) && responseSent == false){
            responseSent = true;
            System.out.println("received ping pakkets= " + receivedPing);
            return receivedPing;

        }
        if ((seconds2 - seconds1 > 4500) && (receivedPing != 0)){
            seconds1 = 0;
            seconds2 = 0;
            receivedPing = 0;
            responseSent = false;
            System.out.println("presence lijst is nu als volgt: ");
            for (int x=0; x<presence.size(); x++){
                System.out.println(presence.get(x));
            }
            presence.clear();
        }
        return 0;
    }

    @Override
    public void run() {
        ping();
    }
}
