package model;

import controller.MultiCast2;

/**
 * Created by Rens on 5-4-2016.
 */
public class Routing implements Runnable{

    private int linkcost;
    private int sourceAdress;
    private int[] forwardingTable = new int[8];
    private MultiCast2 multiCast;

    public Routing(int computerNumber) {
        multiCast = new MultiCast2();
        multiCast.setComputerNumber(computerNumber);
    }


    public void setLinkCost(int receivedInt){
        this.linkcost = 1000 - receivedInt;
        forwardingTable[this.sourceAdress-1] = this.sourceAdress;
        forwardingTable[this.sourceAdress+3] = this.linkcost;
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

    }

    public int[] getForwardingTable(){
        return this.forwardingTable;
    }

    @Override
    public void run() {
        while(true) {
            multiCast.sendPing();

            //wait 5 seconds
            try {
                Thread.sleep(50000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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
