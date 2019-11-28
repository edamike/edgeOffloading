package at.ac.tuwien.offloading;

import at.ac.tuwien.offloading.rest.UppaalRestController;
import at.ac.tuwien.offloading.service.TestDataService;
import at.ac.tuwien.offloading.service.UppaalService;
import at.ac.tuwien.offloading.uppaal.entity.*;
import com.google.api.gbase.client.Location;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EuaDatasetTests {

    private static final Logger LOG = LoggerFactory.getLogger(EuaDatasetTests.class);

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private UppaalRestController uppaalRestController;

    @Autowired
    private TestUtil testUtil;

    @Test
    public void test7() throws IOException {

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("test");

        Row headerRow = sheet.createRow(0);

        Cell cell = headerRow.createCell(0);
        cell.setCellValue("Number of Devices");

        cell = headerRow.createCell(1);
        cell.setCellValue("STATIC");

        cell = headerRow.createCell(2);
        cell.setCellValue("PERIODIC");

        cell = headerRow.createCell(3);
        cell.setCellValue("UNPREDICTABLE");

        int count = 0;
        do
        {
            count = count + 10;
            Row row = sheet.createRow((count/10));
            cell = row.createCell(0);
            cell.setCellValue(count);
            makeTestSingleEdge(row, count);
        } while(count <= 100);

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("test.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
    }

    @Test
    public void test8() throws IOException {

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("test");

        Row headerRow = sheet.createRow(0);

        Cell cell = headerRow.createCell(0);
        cell.setCellValue("Number of Devices");

        cell = headerRow.createCell(1);
        cell.setCellValue("STATIC");

        cell = headerRow.createCell(2);
        cell.setCellValue("PERIODIC");

        cell = headerRow.createCell(3);
        cell.setCellValue("UNPREDICTABLE");

        cell = headerRow.createCell(4);
        cell.setCellValue("WORST-CASE");

        cell = headerRow.createCell(5);
        cell.setCellValue("BEST-CASE STATIC");

        cell = headerRow.createCell(6);
        cell.setCellValue("BEST-CASE PERIODIC");

        cell = headerRow.createCell(7);
        cell.setCellValue("BEST-CASE UNPREDICTABLE");


        int count = 0;
        do
        {
            count = count + 10;
            Row row = sheet.createRow((count/10));
            cell = row.createCell(0);
            cell.setCellValue(count);


            makeTest(row, count, 10, 50);
        } while(count <= 80);

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("test1.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
    }

    @Test
    public void testPrediction() throws IOException {

        double maxDistance = 250.0;
        int deviceSize = 100;
        double a = 0.2;
        Strategy strategy = Strategy.PERIODIC;

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("analysis");
        Sheet devices = workbook.createSheet("devices");
        Sheet edges = workbook.createSheet("edges");
        Row headerRow = sheet.createRow(0);

        Cell cell = headerRow.createCell(0);
        cell.setCellValue("Edge-Ids");

        UppaalRequest uppaalRequest = testDataService.readTestDataEua(maxDistance, 15, deviceSize);
        //UppaalRequest uppaalRequest = testDataService.getMotivatingExample();
        uppaalRequest.setStrategyEnum(strategy);

        uppaalRequest.setComputationIntensity(ComputationIntensity.LOW);
        uppaalRequest.setRequiredRuns(50);
        uppaalRequest.setConsideredTimeUnits(uppaalRequest.getAllAvailableDevices().size());


        UppaalResponse erg = uppaalRestController.analyzeModel(uppaalRequest);

        testUtil.logDevicesToExcel(uppaalRequest.getAllAvailableDevices(), erg.getEdgeNodes(), devices, 0);
        testUtil.logEdgesToExcel(erg.getEdgeNodes(), edges, 0);

        testUtil.logErgToExcel(erg, sheet, 1);

        //Offloading time x+1

        for(int t = 1; t <= 10; t++)
        {
            predictMobility(uppaalRequest, erg, a, t, maxDistance, sheet, null, devices, edges);
        }

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("testEua.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();

    }

    @Test
    public void testTime() throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("time");

        sheet.createRow(0).createCell(0).setCellValue("NumberOfDevices");
        sheet.getRow(0).createCell(1).setCellValue("TimeMillis");
        int i = 1;
        for(int deviceSize=350; deviceSize <= 350; )
        {
            sheet.createRow(i).createCell(0).setCellValue(deviceSize);

            UppaalRequest uppaalRequest = testDataService.readTestDataEua(505.0, 15, deviceSize);
            //UppaalRequest uppaalRequest = testDataService.getMotivatingExample();
            uppaalRequest.setStrategyEnum(Strategy.STATIC);

            long start = System.currentTimeMillis();

            makeAnalysis(uppaalRequest, 10, 50);

            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            System.out.println("STATIC: millis: " + timeElapsed + ", seconds: " + timeElapsed/1000);
            sheet.getRow(i).createCell(1).setCellValue(timeElapsed);

            deviceSize += 10;
            i++;
        }

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("testTime.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();

    }

    @Test
    public void testSensitivity() throws IOException {

        double maxDistance = 250.0;
        int deviceSize = 100;
        int consideredTimeUnits = 10;
        int requiredRuns = 100;
        double a = 0.01;
        Strategy strategy = Strategy.PERIODIC;

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("analysis");
        Sheet devices = workbook.createSheet("devices");
        Sheet edges = workbook.createSheet("edges");
        Workbook workbookSensitivity = new XSSFWorkbook();
        Sheet sensitvity = workbookSensitivity.createSheet("sensitivty");
        Sheet statistics = workbookSensitivity.createSheet("statistics");

        Row headerRow = sheet.createRow(0);

        Cell cell = headerRow.createCell(0);
        cell.setCellValue("Edge-Ids");

        UppaalRequest uppaalRequest = testDataService.readTestDataEua(maxDistance, 15, deviceSize);
        //UppaalRequest uppaalRequest = testDataService.getMotivatingExample();
        uppaalRequest.setStrategyEnum(strategy);

        for (; consideredTimeUnits <= 150; consideredTimeUnits += 10)
        {
            uppaalRequest.setComputationIntensity(ComputationIntensity.LOW);
            uppaalRequest.setRequiredRuns(requiredRuns);
            uppaalRequest.setConsideredTimeUnits(consideredTimeUnits);


            UppaalResponse erg = uppaalRestController.analyzeModel(uppaalRequest);

            testUtil.logDevicesToExcel(uppaalRequest.getAllAvailableDevices(), erg.getEdgeNodes(), devices, 0);
            testUtil.logEdgesToExcel(erg.getEdgeNodes(), edges, 0);

            testUtil.logErgToExcel(erg, sheet, 1);

            //Offloading time x+1

            for(int t = 1; t <= 10; t++)
            {
                predictMobility(uppaalRequest, erg, a, t, maxDistance, sheet, sensitvity, devices, edges);
            }
            testUtil.logStatistics(uppaalRequest, sensitvity, statistics);
            // Write the output to a file
            //FileOutputStream fileOut = new FileOutputStream("testEua_sensitivity_" + a + "_" + consideredTimeUnits + ".xlsx");
            //workbook.write(fileOut);
            //fileOut.close();
        }

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("testEua_sensitivity_" + a + "_" + strategy + ".xlsx");
        workbookSensitivity.write(fileOut);
        fileOut.close();
        // Closing the workbook
        //workbook.close();
        workbookSensitivity.close();


    }

    private Collection<? extends MobileDevice> findAllDevicesInDistance(List<MobileDevice> allAvailableDevices, EdgeNode edgeNode, double maxDistance) {
        List<MobileDevice> deviceList = new ArrayList<>();
        for(MobileDevice m : allAvailableDevices)
        {
            double distance = TestDataService.distance(m.getCurrentLocation().getLatitude(), edgeNode.getLocation().getLatitude(),
                    m.getCurrentLocation().getLongitude(), edgeNode.getLocation().getLongitude(), 0.0, 0.0);
            if(distance <= maxDistance)
                deviceList.add(m);

        }
        return deviceList;
    }

    private Location findNewPosition(TreeMap<Double, EdgeNode> test, MobileDevice m) {

        for(EdgeNode n : test.values()) {
            if (!n.getMobileDeviceList().contains(m)) {
                return n.getLocation();
            }
        }
        return m.getCurrentLocation();
    }

    private TreeMap<Double, EdgeNode> findNearestEdgeNodes(Location location, List<EdgeNode> edgeNodes) {

        TreeMap<Double, EdgeNode> distances = new TreeMap<>();
        for(EdgeNode n : edgeNodes)
        {
            distances.put(TestDataService.distance(location.getLatitude(), n.getLocation().getLatitude(),
                    location.getLongitude(), n.getLocation().getLongitude(), 0.0, 0.0),
                    n);
        }

        LOG.info("ready");
        return distances;

    }

    public void makeTest(Row row, int deviceSize, int consideredTimeUnits, int requiredRuns) throws IOException {

        UppaalRequest uppaalRequest = testDataService.readTestDataEua(505.0, 15, deviceSize);
        //UppaalRequest uppaalRequest = testDataService.getMotivatingExample();
        uppaalRequest.setStrategyEnum(Strategy.STATIC);

        long start = System.currentTimeMillis();

        double staticResources = makeAnalysis(uppaalRequest, consideredTimeUnits, requiredRuns);
        Cell cell = row.createCell(1);
        cell.setCellValue(staticResources);


        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("STATIC: millis: " + timeElapsed + ", seconds: " + timeElapsed/1000);

        start = System.currentTimeMillis();
        uppaalRequest.setStrategyEnum(Strategy.PERIODIC);
        double periodicResources = makeAnalysis(uppaalRequest, consideredTimeUnits, requiredRuns);
        cell = row.createCell(2);
        cell.setCellValue(periodicResources);
        finish = System.currentTimeMillis();
        timeElapsed = finish - start;
        System.out.println("PERIODIC: millis: " + timeElapsed + ", seconds: " + timeElapsed/1000);

        start = System.currentTimeMillis();
        uppaalRequest.setStrategyEnum(Strategy.UNPREDICTABLE);
        double unpredictableResources = makeAnalysis(uppaalRequest, consideredTimeUnits, requiredRuns);
        cell = row.createCell(3);
        cell.setCellValue(unpredictableResources);
        finish = System.currentTimeMillis();
        timeElapsed = finish - start;
        System.out.println("UNPREDICTABLE: millis: " + timeElapsed + ", seconds: " + timeElapsed/1000);


        int worstCase = testUtil.worstCaseAnalysis(uppaalRequest);
        cell = row.createCell(4);
        cell.setCellValue(worstCase);

        uppaalRequest.setStrategyEnum(Strategy.STATIC);
        double bestCase = testUtil.bestCaseAnalysis(uppaalRequest);
        cell = row.createCell(5);
        cell.setCellValue(bestCase);

        uppaalRequest.setStrategyEnum(Strategy.PERIODIC);
        bestCase = testUtil.bestCaseAnalysis(uppaalRequest);
        cell = row.createCell(6);
        cell.setCellValue(bestCase);

        uppaalRequest.setStrategyEnum(Strategy.UNPREDICTABLE);
        bestCase = testUtil.bestCaseAnalysis(uppaalRequest);
        cell = row.createCell(7);
        cell.setCellValue(bestCase);
    }


    private double makeAnalysis(UppaalRequest uppaalRequest, int consideredTimeUnits, int requiredRuns)
    {
        uppaalRequest.setComputationIntensity(ComputationIntensity.LOW);
        //50
        uppaalRequest.setRequiredRuns(requiredRuns);
        //10
        uppaalRequest.setConsideredTimeUnits(consideredTimeUnits);

        UppaalResponse erg = uppaalRestController.analyzeModel(uppaalRequest);

        double maxUsedResources = 0;
        for(EdgeNode n : erg.getEdgeNodes())
        {
            maxUsedResources += TestUtil.round(Double.parseDouble(n.getMessage().substring(0,
                    n.getMessage().indexOf('±'))),0);

        }

        System.out.println("STATIC: " + maxUsedResources);
        //STATIC: 349,7
        //PERIODIC: 308,51
        //UNPREDICTABLE: 190
        return maxUsedResources;
    }


    private void makeTestSingleEdge(Row row, int deviceSize)
    {
        UppaalRequest request = new UppaalRequest();
        request.setComputationIntensity(ComputationIntensity.LOW);
        request.setRequiredRuns(100);
        request.setConsideredTimeUnits(10);
        request.setStrategyEnum(Strategy.STATIC);

        List<UppaalResponse> list = new ArrayList<>();
        list.add(performAnalysis(request, deviceSize));

        request.setStrategyEnum(Strategy.PERIODIC);

        list.add(performAnalysis(request, deviceSize));

        request.setStrategyEnum(Strategy.UNPREDICTABLE);

        list.add(performAnalysis(request, deviceSize));

        Cell cell = null;
        int i = 1;
        for(UppaalResponse erg : list)
        {
            double maxUsedResources = TestUtil.round(Double.parseDouble(erg.getEdgeNodes().get(0).getMessage().substring(0,
                    erg.getEdgeNodes().get(0).getMessage().indexOf('±'))),0);
            if(i==1) {
                LOG.info("STATIC");
                cell = row.createCell(1);
            }
            if(i==2) {
                LOG.info("PERIODIC");
                cell = row.createCell(2);
            }
            if(i==3) {
                LOG.info("UNPREDICTABLE");
                cell = row.createCell(3);
            }
            cell.setCellValue(maxUsedResources);
            for(EdgeNode e : erg.getEdgeNodes())
            {
                LOG.info("Edge " + e.getId() +  " : " + e.getMessage());
            }
            i++;
        }
    }

    private UppaalResponse performAnalysis(UppaalRequest request, int deviceSize)
    {
        List<MobileDevice> mobileDeviceList = new ArrayList<>();
        for(int i = 0; i < deviceSize ; i++)
        {
            mobileDeviceList.add(new MobileDevice(i, "mobileDevice" + i));
        }
        request.setAllAvailableDevices(mobileDeviceList);
        List<EdgeNode> edgeNodeList = new ArrayList<>();
        for(int i = 0; i < 1; i++)
        {
            edgeNodeList.add(new EdgeNode(i, "edge" + i, null));
        }
        edgeNodeList.get(0).setMobileDeviceList(mobileDeviceList);

        request.setEdgeNodeList(edgeNodeList);
        request.setAllAvailableDevices(mobileDeviceList);
        UppaalResponse erg = uppaalRestController.analyzeModel(request);


        return erg;
    }

    private void predictMobility(UppaalRequest uppaalRequest, UppaalResponse erg, double a, int t, double maxDistance, Sheet sheet, Sheet sensitivity, Sheet devices, Sheet edges) {


        for(MobileDevice m : uppaalRequest.getAllAvailableDevices())
        {
            //Move to new point? P_new = a * t^-0.3
            double p_new = a * Math.pow(t,-0.3);
            double rand1 = Math.random();
            if(rand1 < p_new)
            {
                //Make move
                LOG.info("MOVE");
                //find next edge nodes
                TreeMap<Double, EdgeNode> test = findNearestEdgeNodes(m.getCurrentLocation(), erg.getEdgeNodes());
                Location loc = findNewPosition(test, m);
                m.getLocationList().add(loc);
                m.setCurrentLocation(loc);
            }
            else {
                LOG.info("NO MOVE");
                //P_return = 1 - (P_new + P_stay)
                //P_stay = 0.445
                double p_return = 1 - (p_new + 0.445);
                double rand2 = Math.random();
                if(rand2 < p_return) {
                    //return to previous POI
                    if (m.getLocationList().size() - 2 >= 0)
                        m.setCurrentLocation(m.getLocationList().get(m.getLocationList().size() - 2));
                    else
                        LOG.info("NO PREVIOUS POSITION KNOWN!");
                }

            }

        }
        //recalculate EdgeNodes
        for(EdgeNode n : uppaalRequest.getEdgeNodeList())
        {
            n.setMobileDeviceList(new ArrayList<>());

            n.getMobileDeviceList().addAll(findAllDevicesInDistance(uppaalRequest.getAllAvailableDevices(), n, maxDistance));
        }

        testUtil.logDevicesToExcel(uppaalRequest.getAllAvailableDevices(), erg.getEdgeNodes(), devices, t);
        testUtil.logEdgesToExcel(uppaalRequest.getEdgeNodeList(), edges, t);

        int rows = testUtil.logErgToExcel(erg, sheet, t+1);
        //count errors
        int offloadings = testUtil.simulateOffloading(uppaalRequest);
        int errors = 0;
        errors = testUtil.countOffloadingErrors(uppaalRequest.getEdgeNodeList(), errors);

        testUtil.logErrorsToExcel(rows, errors, sheet, sensitivity, t, offloadings);
    }
}
