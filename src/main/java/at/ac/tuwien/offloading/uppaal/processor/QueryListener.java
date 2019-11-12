package at.ac.tuwien.offloading.uppaal.processor;

import at.ac.tuwien.offloading.rest.UppaalRestController;
import at.ac.tuwien.offloading.uppaal.entity.EdgeNode;
import com.uppaal.engine.QueryFeedback;
import com.uppaal.engine.QueryResult;
import com.uppaal.model.system.concrete.ConcreteTrace;
import com.uppaal.model.system.symbolic.SymbolicTrace;
import com.uppaal.model.system.symbolic.SymbolicTransition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author edermic
 * @since 02.02.2019
 */
public class QueryListener implements QueryFeedback
{
    private static final Logger LOG = LoggerFactory.getLogger(QueryListener.class);

    private EdgeNode.Probability probability;

    private Double eMax;

    @Override
    public void setProgressAvail(boolean b)
    {

    }

    @Override
    public void setProgress(int i, long l, long l1, long l2, long l3, long l4, long l5, long l6, long l7, long l8)
    {

    }

    @Override
    public void setSystemInfo(long l, long l1, long l2)
    {

    }

    @Override
    public void setLength(int i)
    {

    }

    @Override
    public void setCurrent(int i)
    {

    }

    @Override
    public void setTrace(char c, String s, SymbolicTrace symbolicTrace, QueryResult queryResult)
    {

    }

    @Override
    public void setTrace(char c, String s, ConcreteTrace concreteTrace, QueryResult queryResult)
    {

    }

    @Override
    public void setFeedback(String s)
    {
        LOG.info("Feedback: " + s);
        String feedback = s;

        Matcher matcher2 =
                java.util.regex.Pattern.compile("(R\\s\\[\\d*\\.{0,1}\\d+,\\d*\\.{0,1}\\d+\\])").matcher(feedback);

        String probabiltyRaw = "";
        while(matcher2.find()) {
            probabiltyRaw = feedback.substring(matcher2.start(), matcher2.end());
        }

        Matcher start = Pattern.compile("\\[\\d*\\.{0,1}\\d+,").matcher(probabiltyRaw);
        String startRaw = "";
        while (start.find())
        {
            startRaw = probabiltyRaw.substring(start.start(), start.end());
            startRaw = startRaw.substring(1, startRaw.length()-1);
        }
        Matcher end = Pattern.compile(",\\d*\\.{0,1}\\d+\\]").matcher(probabiltyRaw);
        String endRaw = "";
        while (end.find())
        {
            endRaw = probabiltyRaw.substring(end.start(), end.end());
            endRaw = endRaw.substring(1, endRaw.length() - 1);
        }

        if(feedback.indexOf("runs") != -1 && feedback.indexOf("E(max)") == -1)
        {
            String runsRaw = feedback.substring(3, feedback.indexOf("runs"));

            LOG.info("Probabilty: " + probabiltyRaw);
            LOG.info("Start RAW: " + startRaw);
            LOG.info("End RAW: " + endRaw);
            LOG.info("Runs RAW: " + runsRaw);

            Double startRawDouble = 0.0;
            if(!startRaw.isEmpty() && !endRaw.isEmpty() && !runsRaw.isEmpty())
                probability = new EdgeNode.Probability(Double.parseDouble(endRaw), Double.parseDouble(endRaw), Integer.parseInt(runsRaw.trim()));

            LOG.info(probability.toString());
        }
        else if (feedback.indexOf("runs") != -1 && feedback.indexOf("E(max") != -1)
        {
            //String runsRaw = feedback.substring(3, feedback.indexOf("runs"));
            String eMaxRaw = feedback.substring(feedback.indexOf("E(max)")+9, feedback.length()-1);

            probability = new EdgeNode.Probability();
            //probability.setRuns(Integer.parseInt(runsRaw.trim()));
            eMax = Double.parseDouble(eMaxRaw);
        }
        else
            probability = new EdgeNode.Probability();

    }


    @Override
    public void appendText(String s)
    {

    }

    @Override
    public void setResultText(String s)
    {

    }

    public EdgeNode.Probability getProbability()
    {
        return probability;
    }

    public void setProbability(EdgeNode.Probability probability)
    {
        this.probability = probability;
    }

    public Double geteMax()
    {
        return eMax;
    }

    public void seteMax(Double eMax)
    {
        this.eMax = eMax;
    }
}

