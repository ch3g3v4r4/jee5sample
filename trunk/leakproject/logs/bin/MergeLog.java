import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;


public class MergeLog {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        File directory = new File(args[0]);

        String[] logFiles = directory.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return "p6spylog4j.log".equals(name) || name.startsWith("p6spylog4j.log.");
            }
        });
        Arrays.sort(logFiles, new Comparator<String>() {
            public int compare(String o1, String o2) {
                int result;
                String str1 = o1.substring("p6spylog4j.log".length());
                String str2 = o2.substring("p6spylog4j.log".length());
                if (str1.equals("")) {
                    result = 1;
                } else if (str2.equals("")) {
                    result = -1;
                } else {
                    int i1 = Integer.parseInt(str1.substring(1));
                    int i2 = Integer.parseInt(str2.substring(1));
                    result = - (i1 - i2);
                }
                return result;
            }
        });

        File tempFile = new File(directory, "temp");
        if (tempFile.exists()) tempFile.delete();
        tempFile.createNewFile();

        for (String file : logFiles) {

            File inFile = new File(directory, file);
            FileWriter outWriter1 = new FileWriter(tempFile, true);
            BufferedWriter outWriter2 =
                new BufferedWriter(outWriter1);
            BufferedReader in = new BufferedReader(new FileReader(inFile));
            String str;
            while ((str = in.readLine()) != null) {
                outWriter2.write(str);
                outWriter2.newLine();
            }
            in.close();
            inFile.delete();
            outWriter2.close();
            outWriter1.close();

        }

        File dxServerLog = new File(directory, "p6spylog4j.log");
        if (dxServerLog.exists()) dxServerLog.delete();
        tempFile.renameTo(dxServerLog);

    }

}
