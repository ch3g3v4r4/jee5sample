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
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;


public class Utils {
    public static String readUrl(URL url) throws IOException {
        InputStream is = url.openStream();
        String content = "";
        try {
            content = IOUtils.toString(is);
        } catch (Exception e) {
            IOUtils.closeQuietly(is);
        }
        return content;
    }

    public static boolean sendKeyTo(String hostEmail, String receiverEmail)
            throws UnsupportedEncodingException, MalformedURLException {
        boolean result;
        //
        // fname John
        // lname Smith
        // email to@gmail.com
        // country USA
        // host_email xxx
        // submit.x 40
        // submit.y 15
        //
        // Error sorry

        String data = URLEncoder.encode("fname", "UTF-8") + "=" + URLEncoder.encode("John", "UTF-8");
        data += "&" + URLEncoder.encode("lname", "UTF-8") + "=" + URLEncoder.encode("Smith", "UTF-8");
        data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(receiverEmail, "UTF-8");
        data += "&" + URLEncoder.encode("country", "UTF-8") + "=" + URLEncoder.encode("USA", "UTF-8");
        data += "&" + URLEncoder.encode("host_email", "UTF-8") + "=" + URLEncoder.encode(hostEmail, "UTF-8");
        data += "&" + URLEncoder.encode("submit.x", "UTF-8") + "=" + URLEncoder.encode("40", "UTF-8");
        data += "&" + URLEncoder.encode("submit.y", "UTF-8") + "=" + URLEncoder.encode("15", "UTF-8");

        System.out.println("- Checking " + hostEmail);
        String response = null;
        OutputStreamWriter wr = null;
        URL url = new URL("http://usa.kas" + "persky.com/sha" + "keitup/thank" + "you.php");
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

        if (response != null && response.indexOf("Error") == -1 && response.indexOf("sorry") == -1) {
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    public static Map<String, WebDriver> generateReceiverEmails(int count) throws InterruptedException {
        Map<String, WebDriver> result = new Hashtable<String, WebDriver>();
        for (int i = 0; i < count; i++) {
            WebDriver driver = new FirefoxDriver();
            driver.get("http://10minutemail.com/");
            String address = null;
            for (int j = 0; j < 2; j++) {
                if (driver.getPageSource().indexOf("is your temporary e-mail address") != -1) {
                    Pattern pattern = Pattern.compile(">([^>]+@uggsrock.com) is your temporary e-mail address");
                    Matcher matcher = pattern.matcher(driver.getPageSource());
                    if (matcher.find()) {
                        address = matcher.group(1);
                        break;
                    }
                }
                if (address == null) Thread.currentThread().sleep(5 * 1000);
            }
            if (address == null) throw new IllegalArgumentException();
            result.put(address, driver);
        }
        return result;
    }

    public static String getKey1(String username) throws MalformedURLException, IOException,
            InterruptedException {
        System.out.println("mailbox:" + username);
        String code = null;
        Exception e = null;
        for (int loop = 1; loop <= 2; loop++) {
            try {
                HtmlUnitDriver driver = new HtmlUnitDriver();
                driver.setJavascriptEnabled(true);
                driver.get("http://www.mytrashmail.com/");
                WebElement element = driver.findElement(By.name("ctl00$ContentPlaceHolder2$txtAccount"));
                element.sendKeys(username);
                element = driver.findElement(By.name("ctl00$ContentPlaceHolder2$cmdGetMail"));
                element.click();
                element = driver.findElement(By.partialLinkText("Kas" + "per" + "sky " + "Lic" + "ense"));
                element.click();
                Thread.currentThread().sleep(3 * 1000);
                String content = driver.getPageSource();
                Pattern pattern = Pattern.compile("[0-9A-Z]{5}\\-[0-9A-Z]{5}\\-[0-9A-Z]{5}\\-[0-9A-Z]{5}");
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    code = matcher.group(0);
                }

            } catch (Exception e1) {
                e = e1;
            }
            if (loop == 1 && code == null) {
                Thread.currentThread().sleep(10 * 1000);
            } else break;
        }

        if (code == null) e.printStackTrace();

        return code;
    }

    public static String getKey(Map.Entry<String, WebDriver> email) throws MalformedURLException, IOException,
            InterruptedException {
        System.out.println("mailbox:" + email.getKey());
        WebDriver driver = email.getValue();
        String code = null;
        Exception e = null;
        for (int loop = 1; loop <= 7; loop++) {
            try {
                WebElement element = driver.findElement(By.partialLinkText("Kas" + "per" + "sky " + "Lic" + "ense"));
                element.click();
                String content = driver.getPageSource();
                Pattern pattern = Pattern.compile("[0-9A-Z]{5}\\-[0-9A-Z]{5}\\-[0-9A-Z]{5}\\-[0-9A-Z]{5}");
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    code = matcher.group(0);
                }

            } catch (Exception e1) {
                e = e1;
            }
            if (loop < 7 && code == null) {
                Thread.currentThread().sleep(5 * 1000);
            } else
                break;
        }

        if (code == null)
            e.printStackTrace();

        return code;
    }
    public static List<String> getHostUsernames() {
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

    public static Map<String, String> sendCodeViaEmails(List<String> usernames, Collection<String> preceiverEmails)
            throws Exception {
        Map<String, String> senderReceiver = new Hashtable<String, String>();

        List<String> receiverEmails = new ArrayList<String>(preceiverEmails);

        while (!usernames.isEmpty() && !receiverEmails.isEmpty()) {
            String currentId = usernames.get(0);
            if (!currentId.contains("@") && EmailValidator.getInstance().isValid(currentId + "@yahoo.com")) {
                usernames.remove(0);
                usernames.add(0, currentId + "@yahoo.com");
                usernames.add(0, currentId + "@gmail.com");
                usernames.add(0, currentId + "@hotmail.com");
                usernames.add(0, currentId + "@aol.com");
                continue;
            }

            if (!EmailValidator.getInstance().isValid(currentId)) {
                usernames.remove(0);
                continue;
            }

            String email = currentId;
            if (Utils.sendKeyTo(email, receiverEmails.get(0))) {
                writeEmail(email);
                senderReceiver.put(email, receiverEmails.get(0));
                receiverEmails.remove(0);
            } else {
                usernames.remove(0);
            }
        }
        return senderReceiver;
    }

    private static void writeEmail(String email) throws IOException {
        System.out.println(email);
        List<String> result = new ArrayList<String>();
        File file = new File("src/emails.txt");
        try {
            result.addAll(FileUtils.readLines(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!result.contains(email)) {
            result.add(email);
            FileUtils.writeLines(file, result);
        }
    }

}
