import org.apache.commons.io.FileUtils;
@Grapes([
@Grab(group='commons-io', module='commons-io', version='1.4')
])
class Main {
    static main(args) {
        FileUtils.writeStringToFile(new File("C:\\test.txt"), "Hello", "UTF-8");
    }
}
