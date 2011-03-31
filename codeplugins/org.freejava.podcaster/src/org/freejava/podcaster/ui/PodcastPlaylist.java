package org.freejava.podcaster.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.freejava.podcaster.Activator;
import org.freejava.podcaster.dao.impl.Database;
import org.freejava.podcaster.domain.PodcastFeed;
import org.freejava.podcaster.domain.PodcastItem;

public class PodcastPlaylist implements BasicPlayerListener {

    private List<PodcastItem> feedItems = new ArrayList<PodcastItem>();
    private int index;
    private BasicPlayer player = new BasicPlayer();
    private PlayingIconIndicator indicator;
    public PodcastPlaylist(PlayingIconIndicator indicator) {
        this.indicator = indicator;
    }

    public void play(PodcastFeed feed, PodcastItem item) {
        try {
            this.feedItems = getPodcastItems(feed);
            if (item != null) {
            	this.index = feedItems.indexOf(item);
            } else {
            	index = 0;
            }
            openClip();
        } catch (Exception e) {
            Activator.logError("Cannot play clip", e);
        }
    }
    public void stop() {
        try {
            player.removeBasicPlayerListener(this);
            player.stop();
        } catch (Exception e) {
            Activator.logError("Cannot stop playing clip", e);
        }
    }
    public void pause() {
        try {
            player.pause();
        } catch (Exception e) {
            Activator.logError("Cannot pause playing clip", e);
        }
    }
    public void resume() {
        try {
            player.resume();
        } catch (Exception e) {
            Activator.logError("Cannot resume playing clip", e);
        }
    }
    private void openClip() throws Exception {
        if (index >= 0 && index < feedItems.size()) {
            PodcastItem feedItem = feedItems.get(index);
            playSound(feedItem.getEnclosureUrl());
        }
    }

    private void playSound(final String url) throws Exception {
        Runnable job = new Runnable() {
            @Override
            public void run() {
                try {
                    doPlaySound(url);
                } catch (Exception e) {
                    Activator.logError("Cannot play sound", e);
                }
            }
        };
        Thread t = new Thread(job);
        t.start();
    }

    private synchronized void doPlaySound(String url) throws Exception {
        player.removeBasicPlayerListener(this);
        player.stop();
        player.addBasicPlayerListener(this);

        // Open file, or URL or Stream (shoutcast) to play.
        player.open(new URL(url));
        // control.open(new URL("http://yourshoutcastserver.com:8000"));

        // Start playback in a thread.
        player.play();

        // Set Volume (0 to 1.0).
        // setGain should be called after control.play().
        player.setGain(0.85);

        // Set Pan (-1.0 to 1.0).
        // setPan should be called after control.play().
        player.setPan(0.0);

    }

    public static URL getScriptUrl(PodcastItem feedItem) throws IOException {
        if (true) {
            return new URL(feedItem.getLink());
        }
        StringBuilder webpage = new StringBuilder();
        webpage.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n        <html>\n        <head>\n        <title>"
                + StringEscapeUtils.escapeXml(feedItem.getTitle())
                +"</title>\n<base href='" + StringEscapeUtils.escapeXml(feedItem.getLink()) + "' />\n"
                +"</head>\n        <body>\n");
        webpage.append(feedItem.getDescription());
        webpage.append("\n</body>\n        </html>");

        File temp = File.createTempFile("temp", ".html");
        temp.deleteOnExit();
        FileUtils.writeStringToFile(temp, webpage.toString(), "UTF-8");
        return temp.toURI().toURL();
    }

    private void showScript(URL url, String title) {
        try {
            int style = IWorkbenchBrowserSupport.AS_EDITOR
                    | IWorkbenchBrowserSupport.NAVIGATION_BAR;
            IWorkbenchBrowserSupport wbbs = PlatformUI.getWorkbench()
                    .getBrowserSupport();
            IWebBrowser browser = wbbs.createBrowser(style, "podcastplayer",
                    title, null);
            browser.openURL(url);
        } catch (Exception e) {
            Activator.logError("Cannot open script", e);
        }
    }


    private List<PodcastItem> getPodcastItems(PodcastFeed feed) throws Exception {
        List<PodcastItem> items = Database.getInstance().getPodcastItems();
        List<PodcastItem> feedItems = new ArrayList<PodcastItem>();
        for (PodcastItem item : items) {
            if (item.getPodcastFeedId().equals(feed.getId())) {
                feedItems.add(item);
            }
        }
        Collections.sort(feedItems, new Comparator<PodcastItem>() {
            public int compare(PodcastItem item1, PodcastItem item2) {
                Date date1 = item1.getPublishDate();
                Date date2 = item2.getPublishDate();
                Date current = new Date();
                if (date1 == null) date1 = current;
                if (date2 == null) date2 = current;
                return date1.compareTo(date2);
            }
        });
        return feedItems;
    }

    @SuppressWarnings("unchecked")
	@Override
    public void opened(Object stream, Map properties) {
        // TODO
    }

    @SuppressWarnings("unchecked")
	@Override
    public void progress(int bytesread, long microseconds, byte[] pcmdata,
            Map properties) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setController(BasicController controller) {
        // TODO Auto-generated method stub

    }

    @Override
    public void stateUpdated(BasicPlayerEvent event) {
        switch (event.getCode()) {
        case BasicPlayerEvent.UNKNOWN:System.out.println("UNKNOWN");break;
        case BasicPlayerEvent.OPENING:System.out.println("OPENING");break;
        case BasicPlayerEvent.OPENED:System.out.println("OPENED");break;
        case BasicPlayerEvent.PLAYING:System.out.println("PLAYING");break;
        case BasicPlayerEvent.STOPPED:System.out.println("STOPPED");break;
        case BasicPlayerEvent.PAUSED:System.out.println("PAUSED");break;
        case BasicPlayerEvent.RESUMED:System.out.println("RESUMED");break;
        case BasicPlayerEvent.SEEKING:System.out.println("SEEKING");break;
        case BasicPlayerEvent.SEEKED:System.out.println("SEEKED");break;
        case BasicPlayerEvent.EOM:System.out.println("EOM");break;
        case BasicPlayerEvent.PAN:System.out.println("PAN");break;
        case BasicPlayerEvent.GAIN:System.out.println("GAIN");break;
        }
        try {
            if (event.getCode() == BasicPlayerEvent.OPENED) {
                if (index >= 0 && index < feedItems.size()) {
                    final PodcastItem feedItem = feedItems.get(index);
                    final URL url = getScriptUrl(feedItem);
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            showScript(url, feedItem.getTitle());
                            indicator.showPlayingIcon(feedItem);
                        }

                    });
                }
            }
            if (event.getCode() == BasicPlayerEvent.STOPPED) {
                indicator.hidePlayingIcon(getCurrentItem());
                next();
            }
        } catch (Exception e) {
            Activator.logError("Cannot handle player event", e);
        }

    }


    public boolean isPaused() {
        return (this.player.getStatus() == BasicPlayer.PAUSED);

    }

    public PodcastItem getCurrentItem() {
        if (index >= 0 && index < feedItems.size()) {
            return feedItems.get(index);
        }
        return null;
    }

    public void next() {
        try {
            if (index < this.feedItems.size() -1) index++;
            else index = 0;
            openClip();
        } catch (Exception e) {
            Activator.logError("Cannot open next clip", e);
        }

    }

    public void previous() {
        // TODO Auto-generated method stub
    }
}
