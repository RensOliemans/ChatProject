package model;

/**
 * Created by coen on 5-4-2016.
 */
public class Routing {

//    private ConcurrentHashMap<Integer, BasicRoute> forwardingTable = new ConcurrentHashMap<Integer, BasicRoute>();

    public void tick(Packet packet) {

        int neighbour = packet.getSourceAddress();
        int destination = packet.getDestinationAddress();
        int rssi = packet.getRssi();
        DataTable dt = packet.getData_table();




    }

}
