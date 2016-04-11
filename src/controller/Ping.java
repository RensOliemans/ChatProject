package controller;

/**
 * Created by Rens on 7-4-2016.
 */
public class Ping implements Runnable{

    private int computerNumber;

    private MultiCast2 multiCast = new MultiCast2();

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
//            TODO: do something
//            multiCast.sendPing();
        }

    }

    @Override
    public void run() {
        ping();
    }
}
