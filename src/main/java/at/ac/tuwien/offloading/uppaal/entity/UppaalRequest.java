package at.ac.tuwien.offloading.uppaal.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author edermic
 * @since 19.03.2019
 */
public class UppaalRequest
{
    private static final Logger LOG = LoggerFactory.getLogger(UppaalRequest.class);

    private List<EdgeNode> edgeNodeList = new ArrayList<>();

    private List<MobileDevice> allAvailableDevices = new ArrayList<>();

    private Strategy strategy;

    private int consideredTimeUnits;

    private int requiredRuns;

    private ComputationIntensity computationIntensity;

    public List<MobileDevice> getAllAvailableDevices()
    {
        return allAvailableDevices;
    }

    public void setAllAvailableDevices(List<MobileDevice> allAvailableDevices)
    {
        this.allAvailableDevices = allAvailableDevices;
    }

    public int getDeviceSize()
    {
        return allAvailableDevices.size();
    }

    public List<EdgeNode> getEdgeNodeList()
    {
        return edgeNodeList;
    }

    public void setEdgeNodeList(List<EdgeNode> edgeNodeList)
    {
        this.edgeNodeList = edgeNodeList;
    }

    public Strategy getStrategy()
    {
        return strategy;
    }

    public void setStrategyEnum(Strategy strategy)
    {
        this.strategy = strategy;
    }

    public void setStrategy(Strategy strategy)
    {
        this.strategy = strategy;
    }

    public int getConsideredTimeUnits()
    {
        return consideredTimeUnits;
    }

    public void setConsideredTimeUnits(int consideredTimeUnits)
    {
        this.consideredTimeUnits = consideredTimeUnits;
    }

    public int getRequiredRuns()
    {
        return requiredRuns;
    }

    public void setRequiredRuns(int requiredRuns)
    {
        this.requiredRuns = requiredRuns;
    }

    public ComputationIntensity getComputationIntensity()
    {
        return computationIntensity;
    }

    public void setComputationIntensity(ComputationIntensity computationIntensity)
    {
        this.computationIntensity = computationIntensity;
    }
}
