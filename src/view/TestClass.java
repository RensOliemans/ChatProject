package view;


import controller.MultiCast2;

/**
 * Created by eric on 12-4-16.
 */
public class TestClass {

	public static void main(String[] args) {
		GUI gui = new GUI(4, new MultiCast2());
		try {
			Thread.sleep(500);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		gui.printMessage("joinrequest:chat3;1,2,4", 3);
		try {
			Thread.sleep(500);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		gui.printMessage("joinrequest:chat3;1,2,4", 3);
	}

}
