package at.ac.tuwien.offloading.rest;

import at.ac.tuwien.offloading.service.TestDataService;
import at.ac.tuwien.offloading.service.UppaalService;
import at.ac.tuwien.offloading.uppaal.entity.UppaalRequest;
import at.ac.tuwien.offloading.uppaal.entity.UppaalResponse;
import at.ac.tuwien.offloading.uppaal.util.TimeWatch;
import com.uppaal.engine.CannotEvaluateException;
import com.uppaal.engine.EngineException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author edermic
 * @since 07.03.2019
 */
@RestController
@Api(value = "Uppaal Rest Controller")
public class UppaalRestController
{
    private static final Logger LOG = LoggerFactory.getLogger(UppaalRestController.class);

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private UppaalService uppaalService;

    @RequestMapping(value = "/resourcePlanningWithRealData", method = RequestMethod.POST)
    public UppaalResponse performResourcePlanningWithRealData(@RequestBody UppaalRequest uppaalRequest) {


        try
        {
            uppaalRequest = testDataService.readTestDataEua(250.0, 10, 100);
            TimeWatch watch = TimeWatch.start();
            if(uppaalRequest.getStrategy() == null)
            {
                UppaalResponse uppaalResponse = new UppaalResponse();
                uppaalResponse.setError(true);
                List<String> errors = new ArrayList<>();
                errors.add("Please select a Offloading Strategy!");
                uppaalResponse.setUppaalErrors(errors);
                return uppaalResponse;
            }
            UppaalResponse response = uppaalService.analyzeWithUppaal(uppaalRequest);
            long passedTimeInMs = watch.time();
            LOG.info("Execution takes " + passedTimeInMs + "ms");
            return response;
        } catch (IOException | EngineException | CannotEvaluateException e)
        {
            e.printStackTrace();
            return new UppaalResponse(Arrays.asList("Error: " + e.getMessage()), true);

        }
    }


    @RequestMapping(value = "/resourcePlanning", method = RequestMethod.POST)
    public UppaalResponse analyzeModel(@RequestBody UppaalRequest uppaalRequest) {
        try
        {
            TimeWatch watch = TimeWatch.start();
            if(uppaalRequest.getStrategy() == null)
            {
                UppaalResponse uppaalResponse = new UppaalResponse();
                uppaalResponse.setError(true);
                List<String> errors = new ArrayList<>();
                errors.add("Please select a Offloading Strategy!");
                uppaalResponse.setUppaalErrors(errors);
                return uppaalResponse;
            }
            UppaalResponse response = uppaalService.analyzeWithUppaal(uppaalRequest);
            long passedTimeInMs = watch.time();
            LOG.info("Execution takes " + passedTimeInMs + "ms");
            return response;
        } catch (IOException | EngineException | CannotEvaluateException e)
        {
            e.printStackTrace();
            return new UppaalResponse(Arrays.asList("Error: " + e.getMessage()), true);

        }
    }
}