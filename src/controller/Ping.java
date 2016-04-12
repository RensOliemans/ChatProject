package controller;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rens on 7-4-2016.
 */
public class Ping implements Runnable{

    private MultiCast2 multiCast;
    private int computerNumber;

    private long seconds1;
    private long seconds2;
    private int receivedPing1;
    private boolean sent = false;
    public List presence = new ArrayList<>();

    public Ping(int computerNumber, MultiCast2 multiCast) {
        this.multiCast = multiCast;
        this.computerNumber = computerNumber;
    }

    private void ping() {
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            multiCast.sendPing(this.computerNumber);
        }

    }

    public int calculateReceivedPings(int data){
        if (!presence.contains(data) && data != 0) {
            presence.add(data);
        }
        if (receivedPing1 == 0) {
            seconds1 = System.currentTimeMillis();
            receivedPing1++;
        } else {
            seconds2 = System.currentTimeMillis();
            receivedPing1++;
        }
        if ((seconds2 - seconds1 > 3000) && (receivedPing1 != 0) && (sent == false)) {
            sent = true;
            return receivedPing1;
        }
        if ((seconds2 - seconds1 > 4500) && (receivedPing1 != 0)) {
            seconds1 = 0;
            seconds2 = 0;
            receivedPing1 = 0;
            sent = false;
            System.out.println("presence lijst is nu als volgt: ");
            for (int i = 0; i < presence.size(); i++) {
                System.out.println(presence.get(i));
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
