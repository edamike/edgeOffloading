package at.ac.tuwien.offloading.uppaal.util;

import at.ac.tuwien.offloading.uppaal.entity.EdgeNode;
import at.ac.tuwien.offloading.uppaal.entity.MobileDevice;
import at.ac.tuwien.offloading.uppaal.entity.UppaalRequest;
import at.ac.tuwien.offloading.uppaal.entity.UppaalResponse;
import com.uppaal.engine.CannotEvaluateException;
import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.Problem;
import com.uppaal.model.core2.*;
import com.uppaal.model.system.SystemEdge;
import com.uppaal.model.system.SystemLocation;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.model.system.symbolic.SymbolicState;
import com.uppaal.model.system.symbolic.SymbolicTransition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author edermic
 * @since 07.03.2019
 */
public class UppaalUtil
{
    private static final Logger LOG = LoggerFactory.getLogger(UppaalUtil.class);

    public static SymbolicState logTraceOfUppaalSystem(Engine engine, UppaalSystem system) throws EngineException, CannotEvaluateException
    {
        SymbolicState state = engine.getInitialState(system);
        while (state != null) {
            UppaalUtil.print(system, state);
            // compute the outgoing transitions with successors (including "deadlock"):
            ArrayList<SymbolicTransition> trans = engine.getTransitions(system, state);
            // select a random transition:
            SymbolicTransition tr = trans.get((int)Math.floor(Math.random()*trans.size()));
            if (tr.getSize()==0) { // transition without edges, something special:
                LOG.info(tr.getEdgeDescription());
                break;
            } else { // one or more edges involved:
                LOG.info("(");
                for (SystemEdge e: tr.getEdges()) {
                    LOG.info(e.getProcessName()+": "
                            + e.getEdge().getSource().getPropertyValue("name")
                            + " -> "
                            + e.getEdge().getTarget().getPropertyValue("name")+", ");
                }
                LOG.info(")");
            }
            state = tr.getTarget();
        }
        return state;
    }

    /**
     * Sets a label on a location.
     * @param l the location on which the label is going to be attached
     * @param kind a kind of the label
     * @param value the label value (either boolean or String)
     * @param x the x coordinate of the label
     * @param y the y coordinate of the label
     */
    public static void setLabel(Location l, LKind kind, Object value, int x, int y) {
        l.setProperty(kind.name(), value);
        Property p = l.getProperty(kind.name());
        p.setProperty("x", x);
        p.setProperty("y", y);
    }
    /**
     * Adds a location to a template.
     * @param t the template
     * @param name a name for the new location
     * @param x the x coordinate of the location
     * @param y the y coordinate of the location
     * @return the new location instance
     */
    public static Location addLocation(Template t, String name, int x, int y) {
        Location l = t.createLocation();
        t.insert(l, null);
        l.setProperty("x", x);
        l.setProperty("y", y);
        setLabel(l, LKind.name, name, x, y-28);
        return l;
    }
    /**
     * Sets a label on an edge.
     * @param e the edge
     * @param kind the kind of the label
     * @param value the content of the label
     * @param x the x coordinate of the label
     * @param y the y coordinate of the label
     */
    public static void setLabel(Edge e, EKind kind, String value, int x, int y) {
        e.setProperty(kind.name(), value);
        Property p = e.getProperty(kind.name());
        p.setProperty("x", x);
        p.setProperty("y", y);
    }
    /**
     * Adds an edge to the template
     * @param t the template where the edge belongs
     * @param source the source location
     * @param target the target location
     * @param guard guard expression
     * @param sync synchronization expression
     * @param update update expression
     * @return
     */
    public static Edge addEdge(Template t, Location source, Location target,
                                   String guard, String sync, String update, String select)
    {
        Edge e = t.createEdge();
        t.insert(e, null);
        e.setSource(source);

        e.setTarget(target);
        int x = (source.getX()+target.getX())/2;
        int y = (source.getY()+target.getY())/2;
        if (guard != null)
        {
            setLabel(e, EKind.guard, guard, getRandomLocation(x), getRandomLocation(y));
        }
        if (sync != null) {
            setLabel(e, EKind.synchronisation, sync, getRandomLocation(x), getRandomLocation(y));
        }
        if (update != null) {
            setLabel(e, EKind.assignment, update, getRandomLocation(x), getRandomLocation(y));
        }
        if (select != null) {
            setLabel(e, EKind.select, select, getRandomLocation(x), getRandomLocation(y));
        }
        return e;
    }


    public static int getRandomLocation(int t) {
        if(Math.random() < 0.5)
            return t - (ThreadLocalRandom.current().nextInt(0, 100 + 1));
        else
            return t + (ThreadLocalRandom.current().nextInt(0, 100 + 1));
    }

    public static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }


    public static Engine getUppaalEngine(Document doc) throws IOException, EngineException
    {
        // create a link to a local Uppaal process:
        Engine engine = new Engine();
        engine.setServerPath(getEnginePath());
        engine.connect();
        return engine;
    }

    public static void print(UppaalSystem sys, SymbolicState s) {
        LOG.info("(");
        for (SystemLocation l: s.getLocations()) {
            LOG.info(l.getName()+", ");
        }
        int val[] = s.getVariableValues();
        for (int i=0; i<sys.getNoOfVariables(); i++) {
            LOG.info(sys.getVariableName(i)+"="+val[i]+", ");
        }
        List<String> constraints = new ArrayList<String>();
        s.getPolyhedron().getAllConstraints(constraints);
        for (String constraint : constraints) {
            LOG.info(constraint+", ");
        }
        LOG.info(")");
    }


    /**
     * Locates the path to engine for different platforms.
     */
    public static String getEnginePath(){
        String os = System.getProperty("os.name");
        URL url = ClassLoader.getSystemResource("com/uppaal/engine/Engine.class");
        try {
            url = new URL(url.getPath()); // strip jar scheme
        } catch (MalformedURLException ex) {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
        File file = new File(url.getPath());
        while (file != null && !("model.jar!".equals(file.getName())))
            file = file.getParentFile();
        if (file == null) {
            System.err.println("Could not locate the Uppaal installation path.");
            System.exit(1);
        }
        file = file.getParentFile(); // lib
        file = file.getParentFile(); // installation
        if ("Linux".equals(os)) {
            file = new File(new File(file, "engine/bin-Linux"), "server");
        } else {
            file = new File(new File(file, "engine/bin-Windows"), "server.exe");
        }
        return file.getPath();
    }

    public static UppaalResponse convertProblems(ArrayList<Problem> problems)
    {
        UppaalResponse uppaalResponse = new UppaalResponse();
        List<String> errors = new ArrayList<>();
        boolean fatal = false;
        LOG.info("There are problems with the document:");
        for (Problem p : problems) {
            LOG.info(p.toString());
            errors.add(p.toString());
            if (!"warning".equals(p.getType())) { // ignore warnings
                fatal = true;
            }
        }
        if (fatal) {
            uppaalResponse.setError(true);
        }
        uppaalResponse.setUppaalErrors(errors);
        return uppaalResponse;
    }

    public static String generateRandomInitMethod(UppaalRequest uppaalRequest)
    {
        return
                "\n" +
                //"typedef int[0,(1<<DEVICE_SIZE)-1] uint32_t;\n" +
                //"\n" +
                "void offloadTrace(bool& test[DEVICE_SIZE])\n" +
                "{\n" +
                //"    uint32_t number = fint(random(1<<DEVICE_SIZE));\n" +
                "    for (i: id_t)\n" +
                "       test[i] = fint(random(2));" +
                //"       test[i] = (number >> i) & 1;\n" +
                "}\n\n";
    }

    public static String generateStaticInitMethod(UppaalRequest uppaalRequest)
    {
        return
                "\n" +
                "typedef int[0,(1<<DEVICE_SIZE)-1] uint32_t;\n" +
                "\n" +
                "void offloadTrace(bool& test[DEVICE_SIZE])\n" +
                "{\n" +
                "    for (i: id_t)\n" +
                "       test[i] = 1;\n" +
                "}\n\n";
    }

    public static String generatePeriodicInitMethod(UppaalRequest uppaalRequest)
    {
        return
                "\n" +
                "typedef int[0,(1<<DEVICE_SIZE)-1] uint32_t;\n" +
                "\n" +
                "bool periodic = 1;" +
                "\n" +
                "void offloadTrace(bool& test[DEVICE_SIZE])\n" +
                "{\n" +
                "   if(periodic == 1) \n" +
                "       periodic = 0; \n" +
                "   else \n" +
                "       periodic = 1;\n" +
                "   for (i: id_t)\n" +
                "   {\n " +
                "       if(periodic == 1) \n" +
                "           test[i] = 1;\n" +
                "       else \n" +
                "           test[i] = 0; \n" +
                "   }\n" +
                "}\n\n";
    }

    public static String generateVariableDefintions(UppaalRequest uppaalRequest)
    {
        return "broadcast chan o;\n " +
                "int x = 1;\n " +
                "const int NODE_SIZE = " + uppaalRequest.getEdgeNodeList().size() + ";\n " +
                "const int DEVICE_SIZE = " + uppaalRequest.getDeviceSize() + ";\n" +

                "typedef int[0, DEVICE_SIZE-1] id_t;\n" +
                "bool offload[id_t]; \n" +
                "clock z; \n" +
                "\n";
    }
    public static String generateDeviceMatrixDynamic(UppaalRequest uppaalRequest)
    {
        String declaration = "const bool deviceMatrix[NODE_SIZE][id_t] = { \n";

        for(EdgeNode e : uppaalRequest.getEdgeNodeList())
        {
            declaration += "{";
            for(int i=0; i<uppaalRequest.getDeviceSize();i++)
            {
                if(containsMobileDevice(i, e.getMobileDeviceList()))
                    declaration += "1,";
                else
                    declaration += "0,";
            }
            declaration = declaration.substring(0, declaration.length()-1);
            declaration += "},\n";
        }
        declaration = declaration.substring(0, declaration.length()-2);
        declaration += "\n};";

        return declaration;
    }

    public static boolean containsMobileDevice(
            int id, List<MobileDevice> devices) {

        for (MobileDevice md : devices) {
            if (md.getId() == id) {
                return true;
            }
        }
        return false;
    }
    public static Double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public static boolean getRandomBoolean() {
        return Math.random() < 0.5;
    }

}
