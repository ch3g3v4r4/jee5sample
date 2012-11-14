import java.text.DecimalFormat
import java.text.NumberFormat
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.fusesource.jansi.AnsiConsole
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.Color

@Grab(group='commons-lang', module='commons-lang', version='2.6')
@Grab(group='commons-io', module='commons-io', version='2.4')
@Grab(group='com.google.guava', module='guava', version='13.0.1')
@Grab(group='commons-codec', module='commons-codec', version='1.5')
public class RssMirror {
	public static void main(String[] args) {

		String sitedir = args[0]
		String siteurl = args[1]

		AnsiConsole.systemInstall()

		Scanner scanIn = new Scanner(System.in)

		List<Map> feeds = [
			[id: 'voa15', name: 'VOA 15 Minute Features', url : 'http://feeds.voanews.com/ps/getRSS?client=Standard&PID=_veJ_N_q3IUpwj2Z5GBO2DYqWDEodojd&startIndex=1&endIndex=500'],
			[id: 'voa5', name: 'VOA 5 Minute Features', url : 'http://feeds.voanews.com/ps/getRSS?client=Standard&PID=gGE0Pwaj1KvIdiToyB__6Y29HOjcqwwK&startIndex=1&endIndex=500'],
			[id: 'voa4', name: 'VOA 4 Minute Features', url : 'http://feeds.voanews.com/ps/getRSS?client=Standard&PID=azHBJZWSH45M06gISfCSXmfXMnqmmj4w&startIndex=1&endIndex=500'],
			[id: 'voa30', name: 'VOA Special English Radio: 30 Minutes of News and Features', url : 'http://feeds.voanews.com/ps/getRSS?client=Standard&PID=Eo9sbV8GfTU93Z9cGw1BMHjGyMOlyi0O&startIndex=1&endIndex=500'],
			[id: 'voaagri', name: 'VOA Agriculture Report', url : 'http://feed.voanews.com/f/KI6AEB/fLMnr707yaAG'],
			[id: 'voastor', name: 'VOA American Stories', url : 'http://feed.voanews.com/f/KI6AEB/YKeYXDE3HsNJ'],
			[id: 'voamosai', name: 'VOA American Mosaic', url : 'http://feed.voanews.com/f/KI6AEB/KnoH7wtw9XoZ'],
			[id: 'voaeco', name: 'VOA Economics Report', url : 'http://feeds.voanews.com/ps/getRSS?client=Standard&PID=tU_oyIBP12kxQsAQtkVwQsFq0yj_2xTy&startIndex=1&endIndex=500'],
			[id: 'voaedu', name: 'VOA Education Report', url : 'http://feed.voanews.com/f/KI6AEB/TSvAoRYSNJMc'],
			[id: 'voaexplo', name: 'VOA Explorations', url : 'http://feed.voanews.com/f/KI6AEB/JZ1UKEotS_bA'],
			[id: 'voahealth', name: 'VOA Health Report', url : 'http://feed.voanews.com/f/KI6AEB/ByeMvAAau5cg'],
			[id: 'voanews', name: 'VOA In the News', url : 'http://feed.voanews.com/f/KI6AEB/QLUOXWrBRqKs'],
			[id: 'voappl', name: 'VOA People in America', url : 'http://feed.voanews.com/f/KI6AEB/ncSi_ToL3yi'],
			[id: 'voasci', name: 'VOA Science in the News', url : 'http://feed.voanews.com/f/KI6AEB/iWYQw_v8MLli'],
			[id: 'voaamer', name: 'VOA This is America', url : 'http://feed.voanews.com/f/KI6AEB/mB8BDgQW13LS'],
			[id: 'voatech', name: 'VOA Technology Report', url : 'http://feed.voanews.com/f/KI6AEB/kACdAH98MMFg'],
			[id: 'voanation', name: 'VOA The Making of a Nation', url : 'http://feed.voanews.com/f/KI6AEB/6rN5mTvs_mv6'],
			[id: 'voawords', name: 'VOA Words and Their Stories', url : 'http://feed.voanews.com/f/KI6AEB/TAx6FSvAJOmT']
		]

		while (true) {

			System.out.println(Ansi.ansi().fg(Color.GREEN).a("MENU:").reset())
			System.out.println()

			for (int i = 0; i < feeds.size(); i++) {
				String number = String.valueOf(i)
				if (number.length() < 2) number += ' '
				System.out.println(Ansi.ansi().fg(Color.WHITE).a(number).fg(Color.GREEN).a(" - Mirror RSS : " + feeds[i].name).reset())
			}

			System.out.println(Ansi.ansi().fg(Color.WHITE).a("q").fg(Color.GREEN).a(" - Exit").reset())
			System.out.println()
			System.out.print(Ansi.ansi().fg(Color.GREEN).a("SELECT: ").reset())

			String choice = scanIn.nextLine()
			if ("q".equals(choice)) break
			Map feed = feeds[Integer.parseInt(choice)]

			downloadFeed(feed.id, feed.url, sitedir, siteurl)
		}
	}

	private static void downloadFeed(String id, String url, String sitedir, String siteurl) {
		def dir = new File(sitedir, id)
		def feed = new File(dir, 'feed.xml')

		AntBuilder ant = new AntBuilder()
		ant.mkdir(dir : dir)
		ant.get(src: url, dest: feed)

		def rss = new XmlParser().parse(feed)
		int i=0
		rss.channel.item.enclosure.each {
			if (i == 0) {i++
			def mp3Url = it.@url
			def filename = Hex.encodeHexString(DigestUtils.md5(mp3Url))
			def newMp3Url = new URL(new URL(siteurl), id + "/" + filename)
			ant.get(src: mp3Url, dest: new File(dir, filename))
			it.@url = newMp3Url
			}
		}


		def writer = new StringWriter()
		new XmlNodePrinter(new PrintWriter(writer)).print(rss)
		feed.text = writer.toString()

	}
}