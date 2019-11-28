package at.ac.tuwien.offloading.uppaal.entity;

import com.google.api.gbase.client.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author edermic
 * @since 19.03.2019
 */
public class EdgeNode
{
    private static final Logger LOG = LoggerFactory.getLogger(EdgeNode.class);

    private int id;

    private String name;

    private List<MobileDevice> mobileDeviceList;

    private String query;

    private String message;

    private int currentOffloadings = 0;

    private Location location;

    public EdgeNode() {

    }

    public EdgeNode(int id, String name, List<MobileDevice> mobileDeviceList)
    {
        this.id = id;
        this.name = name;
        this.mobileDeviceList = mobileDeviceList;
    }
    public EdgeNode(int id, String name, List<MobileDevice> mobileDeviceList, Location location)
    {
        this.id = id;
        this.name = name;
        this.mobileDeviceList = mobileDeviceList;
        this.location = location;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<MobileDevice> getMobileDeviceList()
    {
        return mobileDeviceList;
    }

    public void setMobileDeviceList(List<MobileDevice> mobileDeviceList)
    {
        this.mobileDeviceList = mobileDeviceList;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    @Override
    public String toString()
    {
        return "EdgeNode{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mobileDeviceList=" + mobileDeviceList +
                ", message=" + message +
                '}';
    }

    public void incrementCurrentOffloadings(ComputationIntensity computationIntensity) {
        currentOffloadings = currentOffloadings + (1 * computationIntensity.getValue());
    }

    public int getCurrentOffloadings() {
        return currentOffloadings;
    }

    public void resetCurrentOffloadings() {
        currentOffloadings = 0;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * @author edermic
     * @since 08.02.2019
     */
    public static class Probability
    {
        private static final Logger LOG = LoggerFactory.getLogger(Probability.class);

        private Double start;
        private Double end;
        private int runs;

        public Probability(Double start, Double end, int runs)
        {
            this.start = start;
            this.end = end;
            this.runs = runs;
        }

        public Probability()
        {

        }

        public Double getStart()
        {
            return start;
        }

        public void setStart(Double start)
        {
            this.start = start;
        }

        public Double getEnd()
        {
            return end;
        }

        public void setEnd(Double end)
        {
            this.end = end;
        }

        public int getRuns()
        {
            return runs;
        }

        public void setRuns(int runs)
        {
            this.runs = runs;
        }

        @Override
        public String toString()
        {
            return "Probability{" +
                    "start=" + start +
                    ", end=" + end +
                    ", runs=" + runs +
                    '}';
        }
    }
}
