package at.ac.tuwien.offloading.euadatasets;

import com.google.api.gbase.client.Location;

import java.util.ArrayList;
import java.util.List;

public class EuaEdgeServer {

    private int siteId;
    private Location location;
    private String name;
    private double distance;
    private List<EuaUser> euaUserList = new ArrayList<>();

    public EuaEdgeServer(int siteId, Location location, String name) {
        this.siteId = siteId;
        this.location = location;
        this.name = name;
    }

    public void addEuaUserList(EuaUser e) {
        euaUserList.add(e);
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public List<EuaUser> getEuaUserList() {
        return euaUserList;
    }

    public void setEuaUserList(List<EuaUser> euaUserList) {
        this.euaUserList = euaUserList;
    }

}
