package at.ac.tuwien.offloading;

import at.ac.tuwien.offloading.uppaal.entity.*;
import at.ac.tuwien.offloading.uppaal.util.UppaalUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
public class TestUtil {


    private static final Logger LOG = LoggerFactory.getLogger(TestUtil.class);

    public void logDevicesToExcel(List<MobileDevice> allMobileDevices, List<EdgeNode> edgeNodes, Sheet devices, int x)
    {
        int count = 1;
        for(MobileDevice m : allMobileDevices)
        {
            LOG.info("Device-Name: " + m.getName());
            Row row = devices.getRow(count);
            if(row == null)
                row = devices.createRow(count);
            if(row.getCell(0) == null)
                row.createCell(0).setCellValue(m.getName());

            for(int i = 0; i <= allMobileDevices.size(); i++)
            {
                if(devices.getRow(i) != null && devices.getRow(i).getCell(0).getStringCellValue().equals(m.getName()))
                {
                    row.createCell(x+1).setCellValue(findAllConnectedEdges(m, edgeNodes));
                }
            }

            count++;
        }
    }

    public void logEdgesToExcel(List<EdgeNode> edgeNodes, Sheet edges, int x)
    {
        int count = 1;
        for(EdgeNode n : edgeNodes)
        {

            LOG.info("Server-Name: " + n.getName() + ", Connected-Devices: " + n.getMobileDeviceList().size());
            n.getMobileDeviceList().stream().forEach(a -> LOG.info(a.getName()));
            Row row = edges.getRow(count);
            if(row == null)
                row = edges.createRow(count);
            row.createCell(0).setCellValue(n.getName());
            String s = "";
            for(MobileDevice m : n.getMobileDeviceList())
            {
                s += m.getName() + ", ";
            }
            if(s.length() > 1)
                s = s.substring(0, s.length()-2);
            row.createCell(x+1).setCellValue(n.getMobileDeviceList().size() + " " + s);
            count++;
        }
    }

    public void logErrorsToExcel(int maxRows, int errors, Sheet sheet, Sheet sensitivity, int x, int offloadings)
    {
        System.out.println("ALL ERRORS: " + errors);
        if(sheet.getRow(maxRows) == null) {
            sheet.createRow(maxRows).createCell(0).setCellValue("Errors");
        }
        sheet.getRow(maxRows).createCell(x+1).setCellValue(errors);
        maxRows++;
        if(sheet.getRow(maxRows) == null) {
            sheet.createRow(maxRows).createCell(0).setCellValue("Offloadings");
        }
        sheet.getRow(maxRows).createCell(x+1).setCellValue(offloadings);

        if(sensitivity != null)
        {
            int lastRow = sensitivity.getLastRowNum();
            if(x==1) {
                lastRow = lastRow + 1;
                sensitivity.createRow(lastRow).createCell(0).setCellValue("Errors");
                sensitivity.getRow(lastRow).createCell(x+1).setCellValue(errors);
            }
            else
                sensitivity.getRow(lastRow-1).createCell(x+1).setCellValue(errors);
            if(x==1) {
                lastRow = lastRow + 1;
                sensitivity.createRow(lastRow).createCell(0).setCellValue("Offloadings");
                sensitivity.getRow(lastRow).createCell(x+1).setCellValue(offloadings);
            }
            else
                sensitivity.getRow(lastRow).createCell(x+1).setCellValue(offloadings);
        }

    }

    public void logStatistics(UppaalRequest uppaalRequest, Sheet sensitivity, Sheet statistics)
    {
        //Sum of all Offloadings
        Row off = sensitivity.getRow(sensitivity.getLastRowNum());
        Row errors = sensitivity.getRow(sensitivity.getLastRowNum()-1);
        int offloadingSum = 0;
        for(int i = 2; i < off.getLastCellNum(); i++)
        {
            offloadingSum += off.getCell(i).getNumericCellValue();
        }
        int errorsSum = 0;
        for(int i = 2; i < errors.getLastCellNum(); i++)
        {
            errorsSum += errors.getCell(i).getNumericCellValue();
        }
        if(statistics.getLastRowNum() == 0)
        {
            Row header = statistics.createRow(0);
            header.createCell(0).setCellValue("ConsideredTimeUnits");
            header.createCell(1).setCellValue("Offloading-SUM");
            header.createCell(2).setCellValue("Errors-SUM");
            header.createCell(3).setCellValue("Errors in percent");
        }
        Row stat = statistics.createRow(statistics.getLastRowNum()+1);
        stat.createCell(0).setCellValue(uppaalRequest.getConsideredTimeUnits());
        stat.createCell(1).setCellValue(offloadingSum);
        stat.createCell(2).setCellValue(errorsSum);
        stat.createCell(3).setCellValue(((float)100.0/offloadingSum) * errorsSum);
    }

    public int logErgToExcel(UppaalResponse erg, Sheet sheet, int index) {
        int rows=1;
        for(EdgeNode n : erg.getEdgeNodes())
        {
            if(sheet.getRow(rows) == null)
                sheet.createRow(rows).createCell(0).setCellValue(n.getName());

            sheet.getRow(rows).createCell(index).setCellValue(Double.parseDouble(n.getMessage().substring(0,
                    n.getMessage().indexOf('±'))));

            rows++;
        }
        return rows;
    }

    private String findAllConnectedEdges(MobileDevice mobileDevice, List<EdgeNode> edgeNodes) {
        String s = "";
        for(EdgeNode n : edgeNodes) {
            for(MobileDevice m : n.getMobileDeviceList())
            {
                if(m.getId() == mobileDevice.getId())
                    s += n.getName() + ", ";
            }
        }
        if(s.length() < 2) {
            System.out.println("FEHLER!");
            return s;
        }
        return s.substring(0, s.length()-2);
    }

    public int countOffloadingErrors(List<EdgeNode> edgeNodes, int errors)
    {
        //count offloading errors
        for(EdgeNode n : edgeNodes)
        {
            Double d = Double.parseDouble(n.getMessage().substring(0, n.getMessage().indexOf("±")-1));
            if(d < n.getCurrentOffloadings())
                errors++;
        }

        System.out.println("ERRORS: " + errors);
        edgeNodes.forEach(EdgeNode::resetCurrentOffloadings);
        return errors;
    }

    public int worstCaseAnalysis(UppaalRequest uppaalRequest)
    {
        List<EdgeNode> list = uppaalRequest.getEdgeNodeList();
        int worstCaseResources = 0;
        for(EdgeNode n : list)
        {
            worstCaseResources += (n.getMobileDeviceList().size() * uppaalRequest.getComputationIntensity().getValue());
        }
        return worstCaseResources;
    }

    public double bestCaseAnalysis(UppaalRequest uppaalRequest)
    {
        int offloadings = 0;

        simulateOffloading(uppaalRequest);

        for(EdgeNode n : uppaalRequest.getEdgeNodeList())
        {
            offloadings += n.getCurrentOffloadings();
        }
        uppaalRequest.getEdgeNodeList().forEach(EdgeNode::resetCurrentOffloadings);
        return offloadings;
    }

    public List<EdgeNode> findAllEdgeNodesForMobileDevice(MobileDevice m, List<EdgeNode> list) {
        List<EdgeNode> listNew = new ArrayList<>();
        for(EdgeNode n : list)
        {
            if(n.getMobileDeviceList().contains(m))
                listNew.add(n);
        }
        return listNew;
    }

    public int simulateOffloading(UppaalRequest uppaalRequest) {
        int offloadings = 0;
        List<MobileDevice> offloadMap = new ArrayList<>();
        HashMap<Integer, Boolean> previousOffloadedMap = new HashMap<>();

        for(EdgeNode n : uppaalRequest.getEdgeNodeList())
        {
            for(MobileDevice m : n.getMobileDeviceList())
            {
                if(!offloadMap.contains(m))
                {
                    if(         //STATIC:
                            uppaalRequest.getStrategy().equals(Strategy.STATIC) ||
                                    //Unpredictable:
                                    (!UppaalUtil.getRandomBoolean() && uppaalRequest.getStrategy().equals(Strategy.UNPREDICTABLE)) ||
                                    //PERIODIC:
                                    ((previousOffloadedMap.get(m.getId()) == null || !previousOffloadedMap.get(m.getId()))  && uppaalRequest.getStrategy().equals(Strategy.PERIODIC)) ) {
                        offloadMap.add(m);
                        List<EdgeNode> list = findAllEdgeNodesForMobileDevice(m, uppaalRequest.getEdgeNodeList());
                        Random r = new Random();
                        int low = 0;
                        int high = list.size();
                        int result = r.nextInt(high-low) + low;
                        list.get(result).incrementCurrentOffloadings(uppaalRequest.getComputationIntensity());
                        offloadings++;
                    }
                    else
                        offloadMap.add(m);

                    if(uppaalRequest.getStrategy().equals(Strategy.PERIODIC))
                    {
                        if(previousOffloadedMap.get(m.getId()) == null)
                            previousOffloadedMap.put(m.getId(), true);
                        else
                            previousOffloadedMap.put(m.getId(), !previousOffloadedMap.get(m.getId()));

                    }
                }
            }

        }
        return offloadings;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
