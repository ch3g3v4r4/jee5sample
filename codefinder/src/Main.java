import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;


public class Main {

/*
user_id String  24725
city    String  Valencia
party_id        String  197913
count   String  1133
avgCityLong     String  -119.05084826346
avgCityLat      String  34.8557066752869
cityLong        String  -120
cityLat String  35

->

http://houseparty.com/party_map/mapDrillCluster/35/-120/2/159
->

<a href="/PartyMap/mapDrillParty/175468" onclick="mapDrillParty('175468','35','-120','2','159'); return false;">Jennipha<br /><span class="black">Pasadena, CA</span></a>

"/party_map/mapDrillCluster/35/-120/2/159/page:38"

*/
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        
        List<int[]> latlongs = parseCitiesLatLongs();
        System.out.println(latlongs.size());
        
        List<String> usernames = getUsernames(latlongs);
        File file = new File("src\\usernames.txt"); 
        FileUtils.writeLines(file, usernames);
        //System.out.println(usernames);
    }

    private static List<String> getUsernames(List<int[]> latlongs) {
        List<String> result = new ArrayList<String>();
        int i = 0;
        for (int[] latlong : latlongs) {
            List<String> aresult = getUsernames(latlong);
            result.addAll(aresult);
            i++;
            System.out.println("progress:" + i + "/" + latlongs.size());
        }
        return result;
    }

    private static List<String> getUsernames(int[] latlong) {
        List<String> result = new ArrayList<String>();
        try {
            // get page 0: http://houseparty.com/party_map/mapDrillCluster/<lat>/<long>/2/159
            URL url = new URL("http://houseparty.com/party_map/mapDrillCluster/" + latlong[0] + "/" + latlong[1] + "/2/159");
            String content = readUrl(url);
            
            // find max page number: "/party_map/mapDrillCluster/35/-120/2/159/page:38"
            int maxNum = 1; // first page
            Pattern pattern = Pattern.compile("/page:([0-9]+)");
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                String num = matcher.group(1);
                maxNum = Math.max(maxNum, NumberUtils.toInt(num));
            }
            
            // prepare page content list
            List<String> pages = new ArrayList<String>();
            pages.add(content); // page: 1
            for (int i = 2; i <= maxNum; i++) {
                url = new URL("http://houseparty.com/party_map/mapDrillCluster/" + latlong[0] + "/" + latlong[1] + "/2/159/page:" + i);
                content = readUrl(url);
                pages.add(content);
            }
            
            // process all pages to extract usernames
            // <a href="/PartyMap/mapDrillParty/175468" onclick="mapDrillParty('175468','35','-120','2','159'); return false;">Jennipha<br /><span class="black">Pasadena, CA</span></a>
            for (String page : pages) {
                pattern = Pattern.compile("<a href=\"/PartyMap/mapDrillParty/[^>]+>([^<]+)<");
                matcher = pattern.matcher(page);
                while (matcher.find()) {
                    String username = matcher.group(1);
                    result.add(username);
                }
            }
            
        } catch (Exception e) {
            // TODO: handle exception
        }
        return result;
    }

    private static String readUrl(URL url) throws IOException {
        InputStream is = url.openStream();
        String content = "";
        try {
            content = IOUtils.toString(is);
        } catch (Exception e) {
            IOUtils.closeQuietly(is);
        }
        return content;
    }

    private static List<int[]> parseCitiesLatLongs() {
        List<int[]> result = new ArrayList<int[]>();
        
        // read cities.txt to lines
        List<String> lines = null;
        InputStream is = Main.class.getResourceAsStream("/cities.txt");
        try {
            lines = IOUtils.readLines(is);
        } catch (Exception e) {
            IOUtils.closeQuietly(is);
        }
        
        // parse lines
        int[] latlong = new int[2];
        for (String line : lines) {
            String[] tokens = StringUtils.split(line);
            if (StringUtils.equals("cityLat", tokens[0])) {
                if (latlong[0] != 0) throw new IllegalArgumentException("Invalid cities.txt");
                latlong[0] = NumberUtils.toInt(tokens[tokens.length -1]);
            }
            if (StringUtils.equals("cityLong", tokens[0])) {
                if (latlong[1] != 0) throw new IllegalArgumentException("Invalid cities.txt");
                latlong[1] = NumberUtils.toInt(tokens[tokens.length -1]);
            }
            if (latlong[0] != 0 && latlong[1] != 0) {
                result.add(latlong);
                latlong = new int[2];
            }
        }
        
        return result;
    }

}
