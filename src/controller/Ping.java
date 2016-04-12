package controller;


/**
 * Created by Rens on 7-4-2016.
 */
public class Ping implements Runnable{

    private MultiCast2 multiCast = new MultiCast2();
    private int computerNumber;


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
            multiCast.sendPing(this.computerNumber);
        }

    }

    @Override
    public void run() {
        ping();
    }
}
