package at.ac.tuwien.offloading;

import org.junit.Test;

import java.io.*;
import java.util.HashMap;

public class TesterTest {

    @Test
    public void test() throws IOException {
        InputStream s = new FileInputStream(new File("C:\\Users\\edermic\\Documents\\UNI\\Diplomarbeit\\new\\diplomathesis\\offloading\\src\\main\\resources\\data_old.sql"));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("new.sql"))));
        BufferedReader reader = new BufferedReader(new InputStreamReader(s));
        StringBuilder out = new StringBuilder();
        String line;
        HashMap<String, String> test = new HashMap<>();
        int i = 0;
        while ((line = reader.readLine()) != null) {
            if(line.length() < 35)
            {
                bw.write(line);
                bw.write("\n");
                continue;
            }
            if(ordinalIndexOf(line, "'", 4) == -1 || ordinalIndexOf(line, "'", 5) == -1)
            {
                bw.write(line);
                bw.write("\n");
                continue;
            }
            String edgeId = line.substring(ordinalIndexOf(line, "'", 4), ordinalIndexOf(line, "'", 5));

            if(test.get(edgeId) == null)
            {
                test.put(edgeId, "edge" + i);
                i++;
            }

            line = line.substring(0, ordinalIndexOf(line, "'", 4)+1).concat(test.get(edgeId)).concat("');");
            bw.write(line);

            System.out.println(line);
            bw.write("\n");
        }

        System.out.println("SIZE: " + test.size());
        for(String edge : test.values())
        {
            System.out.println(edge);
        }
        bw.close();
    }

    public static int ordinalIndexOf(String str, String substr, int n) {
        int pos = -1;
        do {
            pos = str.indexOf(substr, pos + 1);
        } while (n-- > 0 && pos != -1);
        return pos;
    }
}
