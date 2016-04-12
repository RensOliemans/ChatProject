package model;

import controller.MultiCast2;
import controller.Ping;
import view.GUI;

/**
 * Created by Rens on 5-4-2016.
 */
public class Routing /*implements Runnable*/{

    private int linkcost;
    private int sourceAdress;
    private int[] forwardingTable = new int[8];

    private static GUI gui = new GUI();
//    private static MultiCast2 multiCast2 = new MultiCast2();
    private static Ping ping;

//    public static void main(String[] args) {
//        MultiCast2 multiCast2 = new MultiCast2();
//        multiCast2.join();
//        System.out.println("Enter computer number");
//        int computerNumber = new Scanner(System.in).nextInt();
//        Routing routing = new Routing(computerNumber);
//        Thread jekanechthelemaalniks = new Thread(routing);
//        jekanechthelemaalniks.start();
//        Thread receiveThread = new Thread(multiCast2);
//        receiveThread.start();
//        multiCast2.setComputerNumber(computerNumber);
//        while (true){
//        }
//    }

    public Routing() {
    }


    public void setLinkCost(int receivedInt){
        this.linkcost = receivedInt;
        forwardingTable[this.sourceAdress-1] = this.sourceAdress;
        forwardingTable[this.sourceAdress+3] = this.linkcost;
        System.out.println("linkcost to " + this.sourceAdress + " is now: " + receivedInt);
    }

    public void setSourceAddress(int sourceAdress){
        this.sourceAdress = sourceAdress;
    }

    public void setForwardingTable(int[] receivedTable){
        for (int j = 1; j < 5; j++){
            if (forwardingTable[j-1] == j){
                if (receivedTable[j+3] < forwardingTable[j+3]){
                    forwardingTable[j+3] = receivedTable[j+3];
                }
            } else {
                forwardingTable[j-1] = j;
                forwardingTable[j+3] = receivedTable[j+3];
            }
        }

        System.out.println("the forwarding table is now as followed: ");
        for (int h = 0; h<8; h++){
            System.out.println(forwardingTable[h]);
        }

    }

    public int[] getForwardingTable(){
        return this.forwardingTable;
    }

    public int[] byteArrayToIngerArray(byte[] bArray){
        int[] iArray = new int[bArray.length];
        for (int g = 0; g<bArray.length; g++){
            if((int)bArray[g] < 0){
                iArray[g] = 256+(int)bArray[g];
            } else {
                iArray[g] = (int)bArray[g];
            }
        }
        return iArray;
    }
}
