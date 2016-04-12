package controller;


/**
 * Created by coen on 5-4-2016.
 */
public class Forward {

    int[] forwardingTable = routing.getForwardingTable();

    int sendTo;

    sendTo = forwardingTable[destinationAdres+7];

    if (forwardingTable[destinationAdres+3] == 255){
        return "kan niet berijken";
    }


}
