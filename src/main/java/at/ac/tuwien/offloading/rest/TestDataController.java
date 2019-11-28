package at.ac.tuwien.offloading.rest;

import at.ac.tuwien.offloading.euadatasets.Example;
import at.ac.tuwien.offloading.service.TestDataService;
import at.ac.tuwien.offloading.uppaal.entity.UppaalRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Api(value = "Uppaal Rest Controller")
public class TestDataController {

    @Autowired
    public TestDataService testDataService;

    @ApiOperation(value = "Provides TestData")
    @RequestMapping(value = "/testData/{example}/{numberEdges}/{numberUsers}", method = RequestMethod.GET)
    public UppaalRequest testData(@PathVariable("example") String example,
                                  @PathVariable("numberEdges") int numberEdges,
                                  @PathVariable("numberUsers") int numberUsers) {

        try {
            if(Example.valueOf(example).equals(Example.MELBOURNE_METROPOLIAN))
                return testDataService.readTestDataEua(505.0, numberEdges, numberUsers);
            else if(Example.valueOf(example).equals(Example.SYNTHETIC_EXAMPLE_SMALL))
                return testDataService.getSyntheticTestData();
            else if(Example.valueOf(example).equals(Example.SYNTHETIC_EXAMPLE_MOTIVATING))
                return testDataService.getMotivatingExample();
            else
                return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
