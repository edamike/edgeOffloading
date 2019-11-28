package at.ac.tuwien.offloading.uppaal.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author edermic
 * @since 20.03.2019
 */
public class ModelCheckingResponse
{
    private static final Logger LOG = LoggerFactory.getLogger(ModelCheckingResponse.class);

    private List<EdgeNode> edgeNodeList;

    public List<EdgeNode> getEdgeNodeList()
    {
        return edgeNodeList;
    }

    public void setEdgeNodeList(List<EdgeNode> edgeNodeList)
    {
        this.edgeNodeList = edgeNodeList;
    }
}
