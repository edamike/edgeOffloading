package at.ac.tuwien.offloading.service;

import at.ac.tuwien.offloading.euadatasets.EuaEdgeServer;
import at.ac.tuwien.offloading.euadatasets.EuaUser;
import at.ac.tuwien.offloading.uppaal.entity.*;
import com.google.api.gbase.client.Location;
import com.opencsv.CSVReader;
import com.uppaal.model.core2.Edge;
import on.U;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dataset from: https://github.com/swinedge/eua-dataset
 *
 */

@Component
public class TestDataService {


    private static final Logger LOG = LoggerFactory.getLogger(TestDataService.class);

    public UppaalRequest readTestDataEua(double maxDistance, int numberEdges, int numberUsers) throws IOException {

        List<List<String>> records = new ArrayList<List<String>>();
        InputStream inputStream = TestDataService.class.getResourceAsStream("/eua-datasets/edge-servers/site-optus-melbCBD.csv");
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
        }
        boolean heading = true;
        int i = 0;
        List<EuaEdgeServer> euaEdgeServers = new ArrayList<>();
        for(List<String> r : records)
        {
            if(heading) {
                heading = false;
                continue;
            }
            else
            {
                euaEdgeServers.add(new EuaEdgeServer(i, new Location("", (float)Float.parseFloat(r.get(1)), Float.parseFloat(r.get(2))), "edge" + i));
                i++;
            }
            if(i == numberEdges)
                break;
        }

        records = new ArrayList<List<String>>();
        inputStream = TestDataService.class.getResourceAsStream("/eua-datasets/users/users-melbcbd-generated.csv");
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
        }
        heading = true;
        int id = 0;
        List<EuaUser> euaUsers = new ArrayList<>();
        for(List<String> r : records)
        {
            if(heading) {
                heading = false;
                continue;
            }else {

                Location loc = new Location("", Float.parseFloat(r.get(0)), Float.parseFloat(r.get(1)));

                EuaUser temp = new EuaUser(loc, id, String.valueOf(id));
                findAllEuaServersWithinDistance(loc, euaEdgeServers, maxDistance, temp);

                euaUsers.add(temp);
            }
            if(id == numberUsers)
                break;
            id++;
        }

        for(EuaUser u : euaUsers)
        {
            for(EuaEdgeServer s : euaEdgeServers)
            {
                if(s.getEuaUserList().stream().anyMatch(a -> a.getId() == u.getId()))
                    u.getConnectedEdgeNodes().add(s.getName());
            }
        }

        UppaalRequest request = new UppaalRequest();
        request.setStrategyEnum(Strategy.UNPREDICTABLE);
        request.setComputationIntensity(ComputationIntensity.LOW);
        request.setConsideredTimeUnits(10);
        request.setRequiredRuns(100);

        List<MobileDevice> mobileDevices = new ArrayList<>();
        for(EuaUser e : euaUsers)
        {
            mobileDevices.add( new MobileDevice(e.getId(), e.getName(), e.getLocation()));

        }
        request.setAllAvailableDevices(mobileDevices);
        List<EdgeNode> edgeNodes = new ArrayList<>();
        for(EuaEdgeServer euaEdgeServer : euaEdgeServers)
        {
            edgeNodes.add(new EdgeNode(euaEdgeServer.getSiteId(), euaEdgeServer.getName(),
                    euaEdgeServer.getEuaUserList().stream().map(a -> new MobileDevice(a.getId(), a.getName())).collect(Collectors.toList()),
                    euaEdgeServer.getLocation()));
        }
        edgeNodes = edgeNodes.stream().sorted(Comparator.comparing(EdgeNode::getId)).collect(Collectors.toList());

        request.setEdgeNodeList(edgeNodes);

        HashMap<Integer, Integer> count = new HashMap<>();
        for(EdgeNode n : request.getEdgeNodeList())
        {
            for(MobileDevice m : n.getMobileDeviceList())
            {
                int c = 1;
                if(count.get(m.getId()) != null)
                    c = count.get(m.getId()) + 1;
                count.put(m.getId(), c);
            }
        }

        for(Integer key : count.keySet())
        {
            LOG.info("Mobile-Device-ID: " + key + ", Connected Edge-Nodes: " + count.get(key));
        }

        return request;
    }

    public UppaalRequest getSyntheticTestData()
    {
        MobileDevice mobileDevice0 = new MobileDevice(0, "mobileDevice0");
        MobileDevice mobileDevice1 = new MobileDevice(1, "mobileDevice1");
        MobileDevice mobileDevice2 = new MobileDevice(2, "mobileDevice2");
        MobileDevice mobileDevice3 = new MobileDevice(3, "mobileDevice3");
        MobileDevice mobileDevice4 = new MobileDevice(4, "mobileDevice4");

        EdgeNode edgeNode1 = new EdgeNode(0, "edgeNode1", Arrays.asList(mobileDevice0, mobileDevice1, mobileDevice2));
        EdgeNode edgeNode2 = new EdgeNode(1, "edgeNode2", Arrays.asList(mobileDevice2, mobileDevice3, mobileDevice4));

        UppaalRequest uppaalRequest = new UppaalRequest();
        uppaalRequest.setEdgeNodeList(Arrays.asList(edgeNode1, edgeNode2));
        uppaalRequest.setAllAvailableDevices(Arrays.asList(mobileDevice0, mobileDevice1, mobileDevice2, mobileDevice3, mobileDevice4));
        return uppaalRequest;
    }

    public UppaalRequest getMotivatingExample() {

        MobileDevice mobileDevice1 = new MobileDevice(0, "mobileDevice1");
        MobileDevice mobileDevice2 = new MobileDevice(1, "mobileDevice2");
        MobileDevice mobileDevice3 = new MobileDevice(2, "mobileDevice3");
        MobileDevice mobileDevice4 = new MobileDevice(3, "mobileDevice4");
        MobileDevice mobileDevice5 = new MobileDevice(4, "mobileDevice5");

        EdgeNode edgeNode1 = new EdgeNode(0, "edgeNode1", Arrays.asList(mobileDevice1, mobileDevice2, mobileDevice3, mobileDevice4));
        EdgeNode edgeNode2 = new EdgeNode(1, "edgeNode2", Arrays.asList(mobileDevice2, mobileDevice3, mobileDevice4, mobileDevice5));

        UppaalRequest uppaalRequest = new UppaalRequest();
        uppaalRequest.setEdgeNodeList(Arrays.asList(edgeNode1, edgeNode2));
        uppaalRequest.setAllAvailableDevices(Arrays.asList(mobileDevice1, mobileDevice2, mobileDevice3, mobileDevice4, mobileDevice5));
        return uppaalRequest;

    }

    private void findAllEuaServersWithinDistance(Location loc, List<EuaEdgeServer> allServers, double maxRange, EuaUser temp) {

        for(EuaEdgeServer s : allServers)
        {
            s.setDistance(distance(loc.getLatitude(), s.getLocation().getLatitude(),
                    loc.getLongitude(), s.getLocation().getLongitude(),0.0, 0.0));
            temp.setDistance(s.getDistance());
        }
        Collections.sort(allServers, Comparator.comparing(EuaEdgeServer::getDistance));

        allServers.stream().filter(a -> a.getDistance() <= maxRange).forEach(i -> temp.setDistance(i.getDistance()));
        allServers.stream().filter(a -> a.getDistance() <= maxRange).forEach(i -> i.addEuaUserList(temp));
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
