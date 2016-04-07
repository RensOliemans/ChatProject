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
        while (true) {
            try {
                Thread.sleep(100);
                multiCast.send(/*ping packet*/"");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void run() {
        ping();
    }
}
