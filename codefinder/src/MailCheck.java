import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;

public class MailCheck {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
        List<String> usernames = Utils.getHostUsernames();
        List<String> receiverEmails = getReceiverEmails();
        Utils.sendCodeViaEmails(usernames, receiverEmails);
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
                    if (EmailValidator.getInstance().isValid(email))
                        result.add(email);
                }
            }
        }

        return result;
    }


}
