package controller;

/**
 * Created by Rens on 7-4-2016.
 */
public class Ping implements Runnable{

    private int computerNumber;

    private MultiCast multiCast = new MultiCast();

    public Ping(int computerNumber) {
        this.computerNumber = computerNumber;
    }


    private void ping() {
//        multiCast.setup();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            multiCast.sendPing();
        }

    }

    @Override
    public void run() {
        ping();
    }
}
