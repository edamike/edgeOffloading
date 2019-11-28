package at.ac.tuwien.offloading;

import at.ac.tuwien.offloading.livelab.LiveLab;
import at.ac.tuwien.offloading.rest.UppaalRestController;
import at.ac.tuwien.offloading.service.TestDataService;
import at.ac.tuwien.offloading.service.UppaalService;
import at.ac.tuwien.offloading.uppaal.entity.*;
import com.uppaal.model.core2.Edge;
import org.apache.poi.ss.usermodel.*;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OffloadingApplicationTests {

	private static final Logger LOG = LoggerFactory.getLogger(OffloadingApplicationTests.class);

	@Autowired
	private UppaalRestController uppaalRestController;


	/**
	 * 2 Edge Nodes
	 *
	 * 		E0	E1
	 * MD0 	*	*
	 * MD1		*
	 */
	@Test
	public void test1() {

		UppaalRequest request = new UppaalRequest();
		request.setStrategyEnum(Strategy.UNPREDICTABLE);
		request.setComputationIntensity(ComputationIntensity.LOW);
		request.setConsideredTimeUnits(100);
		request.setRequiredRuns(10);

		MobileDevice mobileDevice0 = new MobileDevice(0, "0");
		MobileDevice mobileDevice1 = new MobileDevice(1, "1");

		request.setAllAvailableDevices(Arrays.asList(mobileDevice0, mobileDevice1));

		List<MobileDevice> mobileDevicesEdge0 = new ArrayList<>();
		mobileDevicesEdge0.add(mobileDevice0);
		mobileDevicesEdge0.add(mobileDevice1);

		List<MobileDevice> mobileDevicesEdge1 = new ArrayList<>();
		mobileDevicesEdge1.add(mobileDevice1);


		EdgeNode edgeNode0 = new EdgeNode(0, "edge0", mobileDevicesEdge0);
		EdgeNode edgeNode1 = new EdgeNode(1,"edge1", mobileDevicesEdge1);

		request.setEdgeNodeList(Arrays.asList(edgeNode0, edgeNode1));
		UppaalResponse erg = uppaalRestController.analyzeModel(request);

		LOG.info(erg.toString());


		for(EdgeNode e : erg.getEdgeNodes())
		{
			LOG.info("Edge " + e.getId() +  " : " + e.getMessage());
		}
	}

	/**
	 * 2 Edge Nodes
	 *
	 * 		E0	E1
	 * MD0	*
	 * MD1	*
	 * MD2	*	*
	 * MD3	*
	 * MD4	*
	 */
	@Test
	public void test2() {

		UppaalRequest request = new UppaalRequest();
		request.setStrategyEnum(Strategy.UNPREDICTABLE);
		request.setComputationIntensity(ComputationIntensity.LOW);
		request.setConsideredTimeUnits(100);
		request.setRequiredRuns(10);

		MobileDevice mobileDevice0 = new MobileDevice(0, "0");
		MobileDevice mobileDevice1 = new MobileDevice(1, "1");
		MobileDevice mobileDevice2 = new MobileDevice(2, "2");
		MobileDevice mobileDevice3 = new MobileDevice(3, "3");
		MobileDevice mobileDevice4 = new MobileDevice(4, "4");

		request.setAllAvailableDevices(Arrays.asList(mobileDevice0, mobileDevice1, mobileDevice2, mobileDevice3, mobileDevice4));

		List<MobileDevice> mobileDevicesEdge0 = new ArrayList<>();
		mobileDevicesEdge0.add(mobileDevice0);
		mobileDevicesEdge0.add(mobileDevice1);
		mobileDevicesEdge0.add(mobileDevice2);
		mobileDevicesEdge0.add(mobileDevice3);
		mobileDevicesEdge0.add(mobileDevice4);

		List<MobileDevice> mobileDevicesEdge1 = new ArrayList<>();
		mobileDevicesEdge1.add(mobileDevice2);


		EdgeNode edgeNode0 = new EdgeNode(0, "edge0", mobileDevicesEdge0);
		EdgeNode edgeNode1 = new EdgeNode(1,"edge1", mobileDevicesEdge1);

		request.setEdgeNodeList(Arrays.asList(edgeNode0, edgeNode1));
		UppaalResponse erg = uppaalRestController.analyzeModel(request);

		LOG.info(erg.toString());

		for(EdgeNode e : erg.getEdgeNodes())
		{
			LOG.info("Edge " + e.getId() +  " : " + e.getMessage());
		}
	}

	/**
	 * 5 Edge Nodes
	 *
	 * 		E0	E1	E2	E3	E4
	 * 	MD0	*
	 * 	MD1		*
	 * 	MD2	*		*
	 * 	MD3	*			*
	 * 	MD4	*				*
	 */
	@Test
	public void test3() {

		UppaalRequest request = new UppaalRequest();
		request.setStrategyEnum(Strategy.UNPREDICTABLE);
		request.setComputationIntensity(ComputationIntensity.LOW);
		request.setConsideredTimeUnits(100);
		request.setRequiredRuns(10);

		MobileDevice mobileDevice0 = new MobileDevice(0, "0");
		MobileDevice mobileDevice1 = new MobileDevice(1, "1");
		MobileDevice mobileDevice2 = new MobileDevice(2, "2");
		MobileDevice mobileDevice3 = new MobileDevice(3, "3");
		MobileDevice mobileDevice4 = new MobileDevice(4, "4");

		request.setAllAvailableDevices(Arrays.asList(mobileDevice0, mobileDevice1, mobileDevice2, mobileDevice3, mobileDevice4));

		List<MobileDevice> mobileDevicesEdge0 = new ArrayList<>();
		mobileDevicesEdge0.add(mobileDevice0);
		//mobileDevicesEdge0.add(mobileDevice1);
		mobileDevicesEdge0.add(mobileDevice2);
		mobileDevicesEdge0.add(mobileDevice3);
		mobileDevicesEdge0.add(mobileDevice4);

		List<MobileDevice> mobileDevicesEdge1 = new ArrayList<>();
		mobileDevicesEdge1.add(mobileDevice1);


		List<MobileDevice> mobileDevicesEdge2 = new ArrayList<>();
		mobileDevicesEdge2.add(mobileDevice2);

		List<MobileDevice> mobileDevicesEdge3 = new ArrayList<>();
		mobileDevicesEdge3.add(mobileDevice3);

		List<MobileDevice> mobileDevicesEdge4 = new ArrayList<>();
		mobileDevicesEdge4.add(mobileDevice4);

		EdgeNode edgeNode0 = new EdgeNode(0, "edge0", mobileDevicesEdge0);
		EdgeNode edgeNode1 = new EdgeNode(1,"edge1", mobileDevicesEdge1);
		EdgeNode edgeNode2 = new EdgeNode(2,"edge2", mobileDevicesEdge2);
		EdgeNode edgeNode3 = new EdgeNode(3,"edge3", mobileDevicesEdge3);
		EdgeNode edgeNode4 = new EdgeNode(4,"edge4", mobileDevicesEdge4);

		request.setEdgeNodeList(Arrays.asList(edgeNode0, edgeNode1, edgeNode2, edgeNode3, edgeNode4));
		UppaalResponse erg = uppaalRestController.analyzeModel(request);

		LOG.info(erg.toString());

		for(EdgeNode e : erg.getEdgeNodes())
		{
			LOG.info("Edge " + e.getId() +  " : " + e.getMessage());
		}
	}

	@Test
	public void test4()
	{
		UppaalRequest request = new UppaalRequest();
		request.setStrategyEnum(Strategy.UNPREDICTABLE);
		request.setComputationIntensity(ComputationIntensity.LOW);
		request.setConsideredTimeUnits(100);
		request.setRequiredRuns(10);
		List<MobileDevice> mobileDeviceList = new ArrayList<>();
		for(int i = 0; i < 100 ; i++)
		{
			mobileDeviceList.add(new MobileDevice(i, "mobileDevice" + i));
		}
		request.setAllAvailableDevices(mobileDeviceList);
		List<EdgeNode> edgeNodeList = new ArrayList<>();
		for(int i = 0; i < 2; i++)
		{
			edgeNodeList.add(new EdgeNode(i, "edge" + i, null));
		}
		int r = 0;
		for(int i = 0; i <2; i++)
		{
			Random rand = new Random();
			EdgeNode n = edgeNodeList.get(i);
			List<MobileDevice> mobileDeviceList1 = new ArrayList<>();
			for(int j = 0; j < 50; j++)
			{
				//int r = rand.nextInt(10);

				mobileDeviceList1.add(mobileDeviceList.get(r));
				r++;
				if(r==27)
					break;
			}
			n.setMobileDeviceList(mobileDeviceList1);
		}
		request.setEdgeNodeList(edgeNodeList);
		UppaalResponse erg = uppaalRestController.analyzeModel(request);

		LOG.info(erg.toString());

		for(EdgeNode e : erg.getEdgeNodes())
		{
			LOG.info("Edge " + e.getId() +  " : " + e.getMessage());
		}
	}


	@Test
	//Execution time: 16 sec
	public void test5()
	{
		UppaalRequest request = new UppaalRequest();
		request.setStrategyEnum(Strategy.UNPREDICTABLE);
		request.setComputationIntensity(ComputationIntensity.LOW);
		request.setConsideredTimeUnits(100);
		request.setRequiredRuns(10);
		List<MobileDevice> mobileDeviceList = new ArrayList<>();
		for(int i = 0; i < 1000 ; i++)
		{
			mobileDeviceList.add(new MobileDevice(i, "mobileDevice" + i));
		}
		request.setAllAvailableDevices(mobileDeviceList);
		List<EdgeNode> edgeNodeList = new ArrayList<>();
		for(int i = 0; i < 2; i++)
		{
			edgeNodeList.add(new EdgeNode(i, "edge" + i, null));
		}
		int r = 0;
		for(int i = 0; i <2; i++)
		{
			Random rand = new Random();
			EdgeNode n = edgeNodeList.get(i);
			List<MobileDevice> mobileDeviceList1 = new ArrayList<>();
			for(int j = 0; j < 500; j++)
			{
				//int r = rand.nextInt(10);

				mobileDeviceList1.add(mobileDeviceList.get(r));
				r++;
				if(r==420)
					break;
			}
			n.setMobileDeviceList(mobileDeviceList1);
		}
		request.setEdgeNodeList(edgeNodeList);
		UppaalResponse erg = uppaalRestController.analyzeModel(request);

		LOG.info(erg.toString());

		for(EdgeNode e : erg.getEdgeNodes())
		{
			LOG.info("Edge " + e.getId() +  " : " + e.getMessage());
		}
	}


	@Test
	//Execution time: 46 sec
	public void test6()
	{
		UppaalRequest request = new UppaalRequest();
		request.setStrategyEnum(Strategy.UNPREDICTABLE);
		request.setComputationIntensity(ComputationIntensity.LOW);
		request.setConsideredTimeUnits(10);
		request.setRequiredRuns(40);
		List<MobileDevice> mobileDeviceList = new ArrayList<>();
		for(int i = 0; i < 100 ; i++)
		{
			mobileDeviceList.add(new MobileDevice(i, "mobileDevice" + i));
		}
		request.setAllAvailableDevices(mobileDeviceList);
		List<EdgeNode> edgeNodeList = new ArrayList<>();
		for(int i = 0; i < 50; i++)
		{
			edgeNodeList.add(new EdgeNode(i, "edge" + i, null));
		}
		int r = 0;
		for(int i = 0; i <50; i++)
		{
			Random rand = new Random();
			EdgeNode n = edgeNodeList.get(i);
			List<MobileDevice> mobileDeviceList1 = new ArrayList<>();
			for(int j = 0; j < 2; j++)
			{
				//int r = rand.nextInt(10);

				mobileDeviceList1.add(mobileDeviceList.get(r));
				r++;
			}
			n.setMobileDeviceList(mobileDeviceList1);
		}
		request.setEdgeNodeList(edgeNodeList);
		UppaalResponse erg = uppaalRestController.analyzeModel(request);

		LOG.info(erg.toString());

		for(EdgeNode e : erg.getEdgeNodes())
		{
			LOG.info("Edge " + e.getId() +  " : " + e.getMessage());
		}
	}

}
