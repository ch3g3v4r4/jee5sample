import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.openqa.selenium.WebDriver;


public class CodeFinder {
    public static void main(String[] args) throws Exception {

        int count = 100;

        List<String> usernames = Utils.getHostUsernames();

        for (int i = 0; i < count; i++) {
            Map<String, WebDriver> receiverEmails = Utils.generateReceiverEmails(1);
            Map<String, String> sender2Receiver = Utils.sendCodeViaEmails(usernames, receiverEmails.keySet());
            if (usernames.isEmpty()) {
                // no license left
            } else {
                Map<String, String> receiverCodes = getReceiverCodes(receiverEmails);
                writeCodes(sender2Receiver, receiverCodes);
//
//                writeCodes(receiverCodes.values());
            }
            for (WebDriver driver : receiverEmails.values()) driver.close();
        }
    }

    private static void writeCodes(Map<String, String> sender2Receiver, Map<String, String> receiverCodes)
            throws IOException {
        List<String> result = new ArrayList<String>();
        File file = new File("src/codes.txt");
        try {
            for (String s : (List<String>) FileUtils.readLines(file)) {
                if (!StringUtils.isEmpty(s)) {
                    result.add(s);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, String> entry : receiverCodes.entrySet()) {
            boolean added = false;
            for (String line: result) if (line.indexOf(entry.getValue()) != -1) added = true;
            if (!added) {
                String key = entry.getValue();
                String receiver = entry.getKey();
                String sender = "";
                String date = DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date());
                for (Map.Entry<String, String> entry2 : sender2Receiver.entrySet()) {
                    if (entry2.getValue().equals(entry.getKey())) sender = entry2.getKey();
                }
                String newLine = key + " " + receiver + " " + sender + " " + date;
                result.add(newLine);
            }
        }

        FileUtils.writeLines(file, result);

    }

    private static Map<String, String> getReceiverCodes(Map<String, WebDriver> receiverEmails) {
        Map<String, String> result = new Hashtable<String, String>();
        for (Entry<String, WebDriver> email : receiverEmails.entrySet()) {
            try {
                result.put(email.getKey(), Utils.getKey(email));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
