package org.freejava.podcaster.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.freejava.podcaster.Activator;
import org.freejava.podcaster.dao.impl.Database;
import org.freejava.podcaster.domain.PodcastFeed;
import org.freejava.podcaster.domain.PodcastItem;

public class PlayFeedJob extends Job {

    private PodcastItem feedItem;
    private PodcastFeed feed;

    public PlayFeedJob(PodcastItem feedItem, PodcastFeed feed) {
        super("Play Feed Job");
        this.feedItem = feedItem;
        this.feed = feed;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            monitor.beginTask("Play Podcast Feed", 1);
            // String userHome = System.getProperty("user.home");
            // File playlistsDir = new File(new File(userHome),
            // "My Documents\\My Music\\My Playlists");
            // File playlist = exportPlaylist(feed, playlistsDir);

            if (feedItem == null) {
                List<PodcastItem> items = getPodcastItems(feed);
                if (!items.isEmpty()) {
                    feedItem = items.get(0);
                }
            }
            if (feedItem != null) {
                File file = export(feedItem);
                play(file.toURI().toURL());
            }
            return Status.OK_STATUS;
        } catch (Exception e) {
            Activator.logError("Problem when play feed", e);
            e.printStackTrace();
            return Status.CANCEL_STATUS;
        }
    }

    private File export(PodcastItem feedItem) throws IOException {
        StringBuffer webpage = new StringBuffer();
//        webpage.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n        <html>\n        <head>\n        <link rel=\"stylesheet\" href=\"http://www.w3.org/StyleSheets/Core/Modernist\" type=\"text/css\">\n        <title>"
        webpage.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n        <html>\n        <head>\n        <title>"
                + StringEscapeUtils.escapeXml(feedItem.getTitle())
                +"</title>\n<base href='" + StringEscapeUtils.escapeXml(feedItem.getLink()) + "' />\n"
                +"</head>\n        <body>\n");
        webpage.append(feedItem.getDescription());
        webpage.append("\n</body>\n        </html>");

        File temp = File.createTempFile("temp", ".html");
        temp.deleteOnExit();
        FileUtils.writeStringToFile(temp, webpage.toString());
        return temp;
    }

    private void play(URL url) throws Exception {

        // http://musicplugin.sourceforge.net/

        int style = IWorkbenchBrowserSupport.AS_EDITOR
                | IWorkbenchBrowserSupport.NAVIGATION_BAR;
        IWorkbenchBrowserSupport wbbs = PlatformUI.getWorkbench()
                .getBrowserSupport();
        IWebBrowser browser = wbbs.createBrowser(style, "podcastplayer",
                "Podcast Player", "Podcast Player");
        browser.openURL(url);
    }

    private List<PodcastItem> getPodcastItems(PodcastFeed feed)
            throws Exception {
        List<PodcastItem> items = Database.getInstance().getPodcastItems();
        List<PodcastItem> feedItems = new ArrayList<PodcastItem>();
        for (PodcastItem item : items) {
            if (item.getPodcastFeedId().equals(feed.getId())) {
                feedItems.add(item);
            }
        }
        Collections.sort(feedItems, new Comparator<PodcastItem>() {
            public int compare(PodcastItem o1, PodcastItem o2) {
                return o1.getPublishDate().compareTo(o2.getPublishDate());
            }
        });
        return feedItems;
    }

}
