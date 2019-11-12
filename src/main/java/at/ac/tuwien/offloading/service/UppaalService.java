package at.ac.tuwien.offloading.service;

import at.ac.tuwien.offloading.uppaal.entity.*;
import at.ac.tuwien.offloading.uppaal.processor.ModelGenerator;
import at.ac.tuwien.offloading.uppaal.processor.QueryListener;
import at.ac.tuwien.offloading.uppaal.util.UppaalUtil;
import com.uppaal.engine.*;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.QueryData;
import com.uppaal.model.system.UppaalSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author edermic
 * @since 07.03.2019
 */
@Component
public class UppaalService
{
    private static final Logger LOG = LoggerFactory.getLogger(UppaalService.class);

    public static final String options = "order 0\n"
            + "reduction 1\n"
            + "representation 0\n"
            + "trace 0\n"
            + "extrapolation 0\n"
            + "hashsize 27\n"
            + "reuse 1\n"
            + "smcparametric 1\n"
            + "modest 0\n"
            + "statistical 0.01 0.01 0.05 0.05 0.05 0.9 1.1 0.0 0.0 1280.0 0.01";

    @Autowired
    private ModelGenerator modelGenerator;

    public Document generateUppaalModel(UppaalRequest uppaalRequest)
    {
        Document document = modelGenerator.generateModelForEdgeComputingFromObject(uppaalRequest);
        return document;
    }

    public UppaalResponse analyzeWithUppaal(UppaalRequest uppaalRequest) throws IOException, EngineException, CannotEvaluateException
    {
        Document document = this.generateUppaalModel(uppaalRequest);
        File file = new File("generatedModel.xml");
        document.save(file);

        Engine engine = UppaalUtil.getUppaalEngine(document);
        ArrayList<Problem> problems = new ArrayList<Problem>();
        UppaalSystem system = engine.getSystem(document, problems);


        if (!problems.isEmpty()) {
            return UppaalUtil.convertProblems(problems);
        }

        // compute the initial state:
        //SymbolicState state = UppaalUtil.logTraceOfUppaalSystem(engine, system);

        //das sind die meisten Ressourcen die benötigt werden.
        //String uppaalQuery = "E[<=10; 1000](max: sum(i : id_t) Device(i).edge1)";

        QueryListener queryListener = new QueryListener();
        //engine.getInitialState(system);

        List<UppaalQueryObject> queries = new ArrayList<>();

        //get Queries
        for(EdgeNode edgeNode : uppaalRequest.getEdgeNodeList())
        {
            LOG.info("Start query for edge node " + edgeNode.getName());
            int requiredRuns = uppaalRequest.getRequiredRuns();
            int consideredTimeUnits = uppaalRequest.getConsideredTimeUnits();
            queries.add(new UppaalQueryObject("E[ <= " + consideredTimeUnits + "; " + requiredRuns + "](max: sum(i : id_t) Device(i)." + edgeNode.getName() + ")", edgeNode));
        }

        if(queries == null || queries.isEmpty())
        {
            List l = new ArrayList<String>();
            l.add(new String("Error! There is no query given!"));

            return new  UppaalResponse(l, true);
        }

        UppaalResponse uppaalResponse = new UppaalResponse();
        for(UppaalQueryObject query : queries)
        {
            Query query1 = new Query(query.getQuery(), "");

            LOG.info("Querying Uppaal: " + query1.getFormula());
            QueryResult queryResult = engine.query(system, options, query1, queryListener);

            QueryData data = queryResult.getData();

            //final char result = queryVerificationResult.result;
            //LOG.info("Result of query " + query.getQuery() + " : " + result);
            //LOG.info("The probability is " + queryListener.getProbability().toString());

            Double d = Double.parseDouble(queryResult.getMessage().substring(0, queryResult.getMessage().indexOf("±")));
            Double conf = Double.parseDouble(queryResult.getMessage().substring(queryResult.getMessage().indexOf("±") + 1, queryResult.getMessage().length()));

            d = UppaalUtil.round(d * uppaalRequest.getComputationIntensity().getValue(), 2);
            conf = UppaalUtil.round( conf * uppaalRequest.getComputationIntensity().getValue(), 2);

            query.getEdgeNode().setMessage(d + " ± " + conf);
            query.getEdgeNode().setQuery(query1.getFormula());
            //uppaalResponse.setProbability(queryListener.getProbability());
            //uppaalResponse.seteMAX(queryListener.geteMax());
            uppaalResponse.getEdgeNodes().add(query.getEdgeNode());
        }

        engine.disconnect(); // terminate the engine process

        return uppaalResponse;

    }
}
