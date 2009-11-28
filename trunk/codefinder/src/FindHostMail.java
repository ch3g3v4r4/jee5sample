import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


public class FindHostMail {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        List<String> usernames = Utils.getHostUsernames();
        Collections.sort(usernames, new Comparator() {
            public int compare(Object o1, Object o2) {
                return o2.toString().length() - o1.toString().length();
            }
        });
        for (String username : usernames) {
            if (found(username+"@yahoo.com")) {
                System.out.println("SUCCESS: Found " + (username+"@yahoo.com"));
            }
            if (found(username+"@gmail.com")) {
                System.out.println("SUCCESS: Found " + username+"@gmail.com");
            }
        }
    }

    private static boolean found(String string) throws Exception {

        try {
        URL url = new URL("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q="
                + URLEncoder.encode("+\"" + string + "\"", "UTF-8"));
        URLConnection connection = url.openConnection();
        connection.addRequestProperty("Referer", "http://www.hotmail.com");

        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while((line = reader.readLine()) != null) {
         builder.append(line);
        }
        JSONObject json = new JSONObject(builder.toString());
        json = (JSONObject) json.get("responseData");
        JSONArray array = (JSONArray) json.get("results");

        return array != null && array.length() > 0;
        } catch (Exception e) {
            System.out.println("ERROR");
            return false;
        }
    }

}
