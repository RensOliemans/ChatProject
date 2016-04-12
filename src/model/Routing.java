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
    private static GUI gui = new GUI();
    private static Ping ping;
    private int[] forwardingTable = new int[12];
    private int computerNumber;

    public Routing(int computerNumber) {
        this.computerNumber = computerNumber;
    }


    public void setLinkCost(int receivedInt){
        this.linkcost = receivedInt;
        forwardingTable[this.sourceAdress-1] = this.sourceAdress;
        forwardingTable[this.sourceAdress+3] = this.linkcost;
 //       System.out.println("linkcost to " + this.sourceAdress + " is now: " + receivedInt);
    }

    public void setSourceAddress(int sourceAdress){
        this.sourceAdress = sourceAdress;
    }

    public void setForwardingTable(int[] receivedTable){

        //set the fist 4 value's of the forwardingTable to the computerNumbers
        for (int i=0; i<4; i++){
            forwardingTable[i] = i+1;
        }

        //check if the received table contains cheaper routes and update accordingly
        for (int i=4; i<8; i++){
            if (receivedTable[i] + this.linkcost < forwardingTable[i]){
                forwardingTable[i] = receivedTable[i] + this.linkcost;
                forwardingTable[i+4] = this.sourceAdress;
            }
            if (forwardingTable[i] == 0){
                forwardingTable[i] = 255;
            }
        }

        //set the empty nextHops in the forwardingTable to own computerNumber
        for (int i=8; i<12; i++){
            if (forwardingTable[i] == 0){
                forwardingTable[i] = this.computerNumber;
            }
        }

        //set own linkCost in the forwardingTable to 0
        forwardingTable[this.computerNumber+3] = 0;

        //print the new forwardingTable
        System.out.println("the forwarding table is now as followed: ");
        for (int h = 0; h<12; h++){
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
