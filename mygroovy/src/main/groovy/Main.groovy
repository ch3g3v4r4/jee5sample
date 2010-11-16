import java.io.File;

import org.apache.commons.io.FileUtils;
@Grapes([
@Grab(group='commons-io', module='commons-io', version='1.4')
])
class Main {
    static main(args) {

        File temp = File.createTempFile("stdin", ".tmp");
        FileUtils.writeStringToFile(temp, "hello", "UTF-8");

        def ant = new AntBuilder()
        ant.sshexec(
            host:"192.168.17.129",
            trust:"true",
            username:"tha",
            password:"tha",
            command:"find / -name javac -type f -print 2> /dev/null | grep 'bin/javac'",
            failonerror: "false");

        ant.sshexec(
            host:"192.168.17.129",
            trust:"true",
            username:"tha",
            password:"tha",
            command:"/usr/lib/jvm/jdk1.6.0_22/bin/java -cp /mnt/hgfs/mygroovy/target/classes Run",
            input:temp.getAbsolutePath());
    }
}
