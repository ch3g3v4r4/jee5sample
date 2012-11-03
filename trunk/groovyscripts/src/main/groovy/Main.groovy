
import org.fusesource.jansi.AnsiConsole
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.Color

@Grab(group='commons-lang', module='commons-lang', version='2.6')
@Grab(group='commons-io', module='commons-io', version='2.4')
@Grab(group='com.google.guava', module='guava', version='13.0.1')
public class Main {
	public static void main(String[] args) {
		AnsiConsole.systemInstall();
		System.out.println(Ansi.ansi().eraseScreen().fg(Color.RED).a("Hello").fg(Color.GREEN).a(" World").reset());
	}
}