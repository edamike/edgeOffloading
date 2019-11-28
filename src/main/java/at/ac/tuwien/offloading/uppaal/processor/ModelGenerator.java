package at.ac.tuwien.offloading.uppaal.processor;

import at.ac.tuwien.offloading.uppaal.entity.*;
import at.ac.tuwien.offloading.uppaal.util.LKind;
import at.ac.tuwien.offloading.uppaal.util.UppaalUtil;
import com.uppaal.model.core2.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author edermic
 * @since 07.03.2019
 */
@Component
public class ModelGenerator
{
    private static final Logger LOG = LoggerFactory.getLogger(ModelGenerator.class);

    int sizeOfDevices;
    int limit = 10;

    public Document generateModelForEdgeComputingFromObject(UppaalRequest uppaalRequest)
    {
        Document doc = new Document(new PrototypeDocument());

        String declaration = UppaalUtil.generateVariableDefintions(uppaalRequest);
        declaration += UppaalUtil.generateDeviceMatrixDynamic(uppaalRequest);

        if(uppaalRequest.getStrategy() != null && uppaalRequest.getStrategy().equalsName(Strategy.UNPREDICTABLE.name()))
            declaration += UppaalUtil.generateRandomInitMethod(uppaalRequest);
        else if(uppaalRequest.getStrategy() != null && uppaalRequest.getStrategy().equalsName(Strategy.STATIC.name()))
            declaration += UppaalUtil.generateStaticInitMethod(uppaalRequest);
        else if(uppaalRequest.getStrategy() != null && uppaalRequest.getStrategy().equalsName(Strategy.PERIODIC.name()))
            declaration += UppaalUtil.generatePeriodicInitMethod(uppaalRequest);

        doc.setProperty("declaration", declaration);

        Template generator = getGeneratorTemplate(doc, uppaalRequest.getDeviceSize());
        Template device = getDeviceTemplateDynamic(doc, uppaalRequest);

        // add system declaration:
        doc.setProperty("system", "system Generator, Device;");

        return doc;
    }

    public Template getDeviceTemplateDynamic(Document document, UppaalRequest uppaalRequest)
    {
        Template device = document.createTemplate();
        document.insert(device, null);

        String declaration = "";
        device.setProperty("name", "Device");
        device.setProperty("parameter", "const id_t id");
        device.setProperty("declaration", declaration);

        //generate initial vertice
        Location initial = UppaalUtil.addLocation(device, "initial", 0, 400);
        UppaalUtil.setLabel(initial, LKind.exponentialrate, 1, initial.getX()-20, initial.getY()+20);
        initial.setProperty("init", true);

        Location intermediate = UppaalUtil.addLocation(device, "intermediate", 0, 200);
        UppaalUtil.setLabel(intermediate, LKind.exponentialrate, 1, initial.getX()-20, initial.getY()+20);
        UppaalUtil.setLabel(intermediate, LKind.committed, true, initial.getX()-20, initial.getY()+20);


        String intermediateGuard = "offload[id] && (";
        int x = 0;
        int y = 0;
        int nodeId = 0;
        for(EdgeNode e : uppaalRequest.getEdgeNodeList())
        {
            Location dev = UppaalUtil.addLocation(device, e.getName(), x, y);
            UppaalUtil.setLabel(dev, LKind.exponentialrate, 1, dev.getX()-20, dev.getY()+20);
            x += 30;

            String guard = "deviceMatrix[" + e.getId() + "][id]";
            intermediateGuard += guard + " || ";
            UppaalUtil.addEdge(device, intermediate, dev, "offload[id] && " + guard, null, null, null);
            UppaalUtil.addEdge(device, dev, initial, null, "o?", null, null);

            nodeId++;
        }

        UppaalUtil.addEdge(device, initial, intermediate, intermediateGuard.substring(0, intermediateGuard.length()-3 ) + ")", "o?", null, null);


        return device;
    }

    public Template getGeneratorTemplate(Document doc, int sizeOfDevices)
    {
        Template generator = doc.createTemplate();
        doc.insert(generator, null);

        generator.setProperty("name", "Generator");

        Location gen = UppaalUtil.addLocation(generator, "test", 0, 0);
        UppaalUtil.setLabel(gen, LKind.exponentialrate, 1, gen.getX()-20, gen.getY()+20);

        Location inital = UppaalUtil.addLocation(generator, "initial", 250, 0);
        inital.setProperty("init", true);
        UppaalUtil.setLabel(inital, LKind.exponentialrate, 1, inital.getX() - 20, inital.getY()+20);

        String select = "";

        String methodCall = "offloadTrace(offload)";


        UppaalUtil.addEdge(generator, inital, gen, "x > 0", null, methodCall, select);
        UppaalUtil.addEdge(generator, gen, inital, "x == 0", "o!", "x=1", null);
        UppaalUtil.addEdge(generator, gen, gen, "x > 0", "o!", "x = x-1", null);

        return generator;
    }

}
