package controller;


/**
 * This class handles the sending of pings.
 * Created by Rens on 7-4-2016.
 */
class Ping implements Runnable {

	private MultiCast multiCast;
	private int computerNumber;

	private long seconds1;
	private long seconds2;
	private int receivedPing1;
	private boolean sent = false;

	public Ping(int computerNumber, MultiCast multiCast) {
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

	public int calculateReceivedPings(Integer data) {
		if (receivedPing1 == 0) {
			seconds1 = System.currentTimeMillis();
			receivedPing1++;
		} else {
			seconds2 = System.currentTimeMillis();
			receivedPing1++;
		}
		if ((seconds2 - seconds1 > 3000) && (receivedPing1 != 0) && (!sent)) {
			sent = true;
			return receivedPing1;
		}
		if ((seconds2 - seconds1 > 4500) && (receivedPing1 != 0)) {
			seconds1 = 0;
			seconds2 = 0;
			receivedPing1 = 0;
			sent = false;
		}
		return 0;
	}

	@Override
	public void run() {
		ping();
	}
}
