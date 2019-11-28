package at.ac.tuwien.offloading.euadatasets;

import com.google.api.gbase.client.Location;

import java.util.ArrayList;
import java.util.List;


public class EuaUser {

    private int id;
    private String name;
    private Location location;
    private double distance;
    private List<String> connectedEdgeNodes = new ArrayList<>();

    public EuaUser(Location location, int id, String name) {
        this.location = location;
        this.id = id;
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public List<String> getConnectedEdgeNodes() {
        return connectedEdgeNodes;
    }

    public void setConnectedEdgeNodes(List<String> connectedEdgeNodes) {
        this.connectedEdgeNodes = connectedEdgeNodes;
    }
}
