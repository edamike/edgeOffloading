package at.ac.tuwien.offloading.uppaal.entity;

import at.ac.tuwien.offloading.service.UppaalService;
import at.ac.tuwien.offloading.uppaal.util.UppaalUtil;
import com.google.api.gbase.client.Location;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * @author edermic
 * @since 19.03.2019
 */
public class MobileDevice
{
    private static final Logger LOG = LoggerFactory.getLogger(MobileDevice.class);

    private int id;

    private String name;

    private ArrayList<Location> locationList = new ArrayList<>();

    private Location currentLocation;

    private boolean previousOffloaded = UppaalUtil.getRandomBoolean();

    public MobileDevice() {

    }

    public MobileDevice(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public MobileDevice(int id, String name, Location location)
    {
        this.id = id;
        this.name = name;
        this.currentLocation = location;
        if(location != null)
            this.locationList.add(location);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ArrayList<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(ArrayList<Location> locationList) {
        this.locationList = locationList;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public boolean isPreviousOffloaded() {
        return previousOffloaded;
    }

    public void setPreviousOffloaded(boolean previousOffloaded) {
        this.previousOffloaded = previousOffloaded;
    }

    @Override
    public String toString()
    {
        return "MobileDevice{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MobileDevice that = (MobileDevice) o;

        if (id != that.id) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}

