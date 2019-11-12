package at.ac.tuwien.offloading.uppaal.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author edermic
 * @since 20.03.2019
 */
public class UppaalQueryObject
{
    private static final Logger LOG = LoggerFactory.getLogger(UppaalQueryObject.class);

    private String query;
    private EdgeNode edgeNode;

    public UppaalQueryObject(String query, EdgeNode edgeNode)
    {
        this.query = query;
        this.edgeNode = edgeNode;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public EdgeNode getEdgeNode()
    {
        return edgeNode;
    }

    public void setEdgeNode(EdgeNode edgeNode)
    {
        this.edgeNode = edgeNode;
    }
}
