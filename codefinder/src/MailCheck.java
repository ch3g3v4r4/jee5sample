import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;


public class MailCheck {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
        List<String> usernames = getUsernames();
        List<String> receiverEmails = getReceiverEmails();
        sendEmails(usernames, receiverEmails);
    }

    private static List<String> getReceiverEmails() {
        List<String> result = new ArrayList<String>();

        // read usernames.txt to lines
        List<String> lines = null;
        InputStream is = Main.class.getResourceAsStream("/receiverEmails.txt");
        try {
            lines = IOUtils.readLines(is);
        } catch (Exception e) {
            IOUtils.closeQuietly(is);
        }
        for (String line : lines) {
        	String[] emails = StringUtils.split(line);
        	if (emails != null) {
        		for (String email : emails) {
        			if (EmailValidator.getInstance().isValid(email)) result.add(email);
        		}
        	}
        }

        return result;
	}

	private static void sendEmails(List<String> usernames, List<String> receiverEmails) throws Exception {

        for (String username : usernames) {

            if (receiverEmails.isEmpty()) return;

            if (!EmailValidator.getInstance().isValid(username + "@yahoo.com")) continue;

            String testemails[] = new String[] {
                    username + "@yahoo.com",
                    username + "@gmail.com",
                    username + "@hotmail.com",
                    username + "@aol.com"};

            for (String email : testemails) {
                if (isAvailable(email, receiverEmails.get(0))) {
                    writeEmail(email);

                    receiverEmails.remove(0);
                    if (receiverEmails.isEmpty()) return;

                    while (isAvailable(email, receiverEmails.get(0))) {
                        receiverEmails.remove(0);
                        if (receiverEmails.isEmpty()) return;
                    }
                }
            }
        }
    }

    private static void writeEmail(String email) throws IOException {
        List<String> result = new ArrayList<String>();
        File file = new File("src/emails.txt");
        try {
            result.addAll(FileUtils.readLines(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.add(email);
        FileUtils.writeLines(file, result);
        System.out.println(email);
    }

    private static boolean isAvailable(String email, String receiverEmail) throws UnsupportedEncodingException, MalformedURLException {
        boolean result;
//
//        fname John
//        lname Smith
//        email to@gmail.com
//        country USA
//        host_email xxx
//        submit.x 40
//        submit.y 15
//
//        Error sorry


        String data = URLEncoder.encode("fname", "UTF-8") + "=" + URLEncoder.encode("John", "UTF-8");
        data += "&" + URLEncoder.encode("lname", "UTF-8") + "=" + URLEncoder.encode("Smith", "UTF-8");
        data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(receiverEmail, "UTF-8");
        data += "&" + URLEncoder.encode("country", "UTF-8") + "=" + URLEncoder.encode("USA", "UTF-8");
        data += "&" + URLEncoder.encode("host_email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
        data += "&" + URLEncoder.encode("submit.x", "UTF-8") + "=" + URLEncoder.encode("40", "UTF-8");
        data += "&" + URLEncoder.encode("submit.y", "UTF-8") + "=" + URLEncoder.encode("15", "UTF-8");

        System.out.println("- Checking " + email);
        String response = null;
        OutputStreamWriter wr = null;
        URL url = new URL("http://usa.kas" 
			+ "persky.com/sha" 
			+ "keitup/thank" 
			+ "you.php");
        try {
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            response = IOUtils.toString(conn.getInputStream());
        } catch (Exception e) {
            IOUtils.closeQuietly(wr);
        }

        System.out.println(response);

        if (response != null && response.indexOf("Error") == -1 && response.indexOf("sorry") == -1) {
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    private static List<String> getUsernames() {
        List<String> result = new ArrayList<String>();

        // read usernames.txt to lines
        List<String> lines = null;
        InputStream is = Main.class.getResourceAsStream("/usernames.txt");
        try {
            lines = IOUtils.readLines(is);
        } catch (Exception e) {
            IOUtils.closeQuietly(is);
        }
        for (String line : lines) {
            result.add(StringUtils.trim(line));
        }
        Collections.shuffle(result);
        return result;
    }

}
