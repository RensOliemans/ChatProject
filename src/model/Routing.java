package model;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Rens on 5-4-2016.
 */
public class Routing {

    //this class needs to receive:
    //een int[2] with source and pings per second in that order

    private int linkcost; //this is the pings per second
    private int source; //this is the source of the ping

    private ConcurrentHashMap<Integer, Integer> forwardingTable = new ConcurrentHashMap<Integer, Integer>();

    public void setTable(int[] info){

        this.linkcost = info[0];
        this.source = info[1];

        if (forwardingTable.containsKey(source)){
            if (forwardingTable.get(source) < linkcost){
                forwardingTable.replace(source,forwardingTable.get(source), linkcost);
            }
        } else {
            forwardingTable.put(source, linkcost);
        }
    }

    public void emptyTable() {
        forwardingTable.replaceAll(null);
    }

    public int[] tableToIntArray(){

    }



}
