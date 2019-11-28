package at.ac.tuwien.offloading.uppaal.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author edermic
 * @since 07.03.2019
 */
public class UppaalResponse
{
    private static final Logger LOG = LoggerFactory.getLogger(UppaalResponse.class);

    private List<String> uppaalErrors = new ArrayList<>();

    private boolean error;

    private List<EdgeNode> edgeNodes = new ArrayList<>();

    public UppaalResponse() {

    }

    public UppaalResponse(List<String> uppaalErrors, boolean error)
    {
        this.uppaalErrors = uppaalErrors;
        this.error = error;
    }

    public List<String> getUppaalErrors()
    {
        return uppaalErrors;
    }

    public void setUppaalErrors(List<String> uppaalErrors)
    {
        this.uppaalErrors = uppaalErrors;
    }

    public boolean isError()
    {
        return error;
    }

    public void setError(boolean error)
    {
        this.error = error;
    }

    public List<EdgeNode> getEdgeNodes()
    {
        return edgeNodes;
    }

    public void setEdgeNodes(List<EdgeNode> edgeNodes)
    {
        this.edgeNodes = edgeNodes;
    }

    @Override
    public String toString()
    {
        return "UppaalResponse{" +
                "uppaalErrors=" + uppaalErrors +
                ", error=" + error +
                ", edgeNodes=" + edgeNodes +
                '}';
    }
}
