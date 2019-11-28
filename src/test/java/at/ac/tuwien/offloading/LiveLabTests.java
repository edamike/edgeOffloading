package at.ac.tuwien.offloading;

import at.ac.tuwien.offloading.livelab.LiveLab;
import at.ac.tuwien.offloading.rest.UppaalRestController;
import at.ac.tuwien.offloading.uppaal.entity.*;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LiveLabTests {

    private static final Logger LOG = LoggerFactory.getLogger(LiveLabTests.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UppaalRestController uppaalRestController;

    @Autowired
    private TestUtil testUtil;

    @Test
    public void testLiveLabData() throws IOException {

        String date = "2010-12-08";


        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("model-checking-case");
        Sheet devices = workbook.createSheet("devices");
        Sheet edges = workbook.createSheet("edges");

        Row headerRow = sheet.createRow(0);
        Row headerRowDevices = sheet.createRow(0);
        Row headerRowEdges = sheet.createRow(0);

        Cell cell = headerRow.createCell(0);
        cell.setCellValue("Edge-Node");
        Cell cellDevices = headerRowDevices.createCell(0);
        cellDevices.setCellValue("Device-Id");
        Cell cellEdges = headerRowEdges.createCell(0);
        cellEdges.setCellValue("Edge-Id");

        // 1 - find all towers
        List<String> towers = jdbcTemplate.queryForList("select distinct towerid " +
                "from celltower where SUBSTR( DATEADD('SECOND', \"time\", DATE '1970-01-01'), 0, 10) = '" + date + "' ORDER BY towerid", String.class);

        SortedMap<String, EdgeNode> edgeNodes = new TreeMap<>();
        int i = 0;
        for(String towerId : towers)
        {
            EdgeNode n = new EdgeNode(i, towerId, new ArrayList<>());
            edgeNodes.put(towerId, n);
            i++;
        }

        for (int j = 1; j <= 24; j++) {
            cell = headerRow.createCell(j);
            cell.setCellValue(j-1);

            cell = headerRowDevices.createCell(j);
            cell.setCellValue(j-1);
            headerRowEdges.createCell(j).setCellValue(j-1);

            analyzeHour(date, j-1, edgeNodes, sheet, edges, devices);
        }

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("testLiveLab.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
    }


    private void analyzeHour(String date, int x, SortedMap<String, EdgeNode> edgeNodes, Sheet sheet, Sheet edges, Sheet devices) {

        List<LiveLab> liveLabs = jdbcTemplate.query("select distinct uid, towerid from celltower " +
                        "where SUBSTR( DATEADD('SECOND', \"time\", DATE '1970-01-01'), 0, 13) = '" + date + " " +
                        String.format("%02d", x) + "' order by uid;",
                new RowMapper<LiveLab>() {
                    @Override
                    public LiveLab mapRow(ResultSet resultSet, int i) throws SQLException {
                        return new LiveLab(resultSet.getString("uid"), resultSet.getString("towerid"));
                    }
                });

        HashMap<String, MobileDevice> allMobileDevices = convertLiveLabToUppaal(edgeNodes, liveLabs);

        testUtil.logEdgesToExcel(new ArrayList<>(edgeNodes.values()), edges, x);

        testUtil.logDevicesToExcel(new ArrayList<>(allMobileDevices.values()), new ArrayList<>(edgeNodes.values()), devices, x);

        UppaalRequest uppaalRequest = new UppaalRequest();
        uppaalRequest.setStrategyEnum(Strategy.PERIODIC);
        uppaalRequest.setComputationIntensity(ComputationIntensity.LOW);
        uppaalRequest.setAllAvailableDevices(new ArrayList<>(allMobileDevices.values()));
        List<EdgeNode> finalList = new ArrayList<>(edgeNodes.values());
        finalList.sort(Comparator.comparing(EdgeNode::getId));
        uppaalRequest.setEdgeNodeList(finalList);
        uppaalRequest.setConsideredTimeUnits(allMobileDevices.size()+10);
        uppaalRequest.setRequiredRuns(10);


        UppaalResponse erg = uppaalRestController.analyzeModel(uppaalRequest);
        int rows = 1;
        for(EdgeNode n : erg.getEdgeNodes())
        {
            Row header;
            if(sheet.getRow(rows) == null)
                header = sheet.createRow(rows);
            else
                header = sheet.getRow(rows);
            header.createCell(0).setCellValue(n.getName());
            rows++;
        }
        int maxRows = rows;
        rows--;
        for (EdgeNode n : erg.getEdgeNodes()) {
            for(rows = maxRows-1; rows!=0; rows--) {
                Row r = sheet.getRow(rows);
                if(r.getCell(0).getStringCellValue().equals(n.getName()))
                {
                    sheet.getRow(rows).createCell(x + 1).setCellValue(TestUtil.round(Double.parseDouble(n.getMessage().substring(0,
                            n.getMessage().indexOf('±'))),0));
                }
            }
        }


        //Count offloading errors
        int errors = 0;
        int offloadings = 0;
        for(int j = 0; j <= 5; j++)
        {
            liveLabs = jdbcTemplate.query("select distinct uid, towerid from celltower " +
                            "where SUBSTR( DATEADD('SECOND', \"time\", DATE '1970-01-01'), 0, 15) = '" + date + " " +
                            String.format("%02d", x) + ":" + j + "' order by uid;",
                    new RowMapper<LiveLab>() {
                        @Override
                        public LiveLab mapRow(ResultSet resultSet, int i) throws SQLException {
                            return new LiveLab(resultSet.getString("uid"), resultSet.getString("towerid"));
                        }
                    });
            offloadings += liveLabs.size();
            for(LiveLab l : liveLabs)
            {
                System.out.println("UID: " + l.getUid() + ", TowerId:" + l.getTowerId());
                for(EdgeNode n : edgeNodes.values()) {
                    if(n.getName().equals(l.getTowerId())) {
                        n.incrementCurrentOffloadings(uppaalRequest.getComputationIntensity());
                    }
                }
            }
            errors += testUtil.countOffloadingErrors(new ArrayList<>(edgeNodes.values()), 0);
        }


        testUtil.logErrorsToExcel(maxRows, errors, sheet, null, x, offloadings);

    }

    private HashMap<String, MobileDevice> convertLiveLabToUppaal(SortedMap<String, EdgeNode> edgeNodes, List<LiveLab> liveLabs)
    {
        for(EdgeNode i : edgeNodes.values())
        {
            i.getMobileDeviceList().removeAll(i.getMobileDeviceList());
        }
        HashMap<String, MobileDevice> allMobileDevices = new HashMap<>();
        int mId = 0;
        for(LiveLab l : liveLabs)
        {
            if(allMobileDevices.values().stream().noneMatch(a -> a.getName().equals(l.getUid())))
            {
                allMobileDevices.put(l.getUid(), new MobileDevice(mId, l.getUid()));
                mId++;
            }
            for(EdgeNode n : edgeNodes.values())
            {
                if(n.getName().equals(l.getTowerId()))
                    n.getMobileDeviceList().add(allMobileDevices.get(l.getUid()));

            }
        }
        return allMobileDevices;
    }
}
