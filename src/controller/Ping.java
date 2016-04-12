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

    @Override
    public void run() {
        ping();
    }
}
