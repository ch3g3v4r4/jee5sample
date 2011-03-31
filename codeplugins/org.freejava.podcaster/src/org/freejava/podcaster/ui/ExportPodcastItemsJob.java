package org.freejava.podcaster.ui;

import java.io.File;
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
import org.freejava.podcaster.dao.impl.Database;
import org.freejava.podcaster.domain.PodcastFeed;
import org.freejava.podcaster.domain.PodcastItem;

public class ExportPodcastItemsJob extends Job {

    public ExportPodcastItemsJob() {
        super("Exporting Podcast Feeds to Playlists Job");
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            List<PodcastFeed> feeds = Database.getInstance().getPodcastFeeds();
            String userHome = System.getProperty("user.home");
            File playlistsDir = new File(new File(userHome),
                    "My Documents\\My Music\\My Playlists");
            monitor.beginTask("Exporting Feeds", feeds.size());
            for (int i = 0; i < feeds.size(); i++) {
                PodcastFeed feed = feeds.get(i);
                monitor.subTask("Feed " + feed.getFeedUrl());
                export(feed, playlistsDir);
                monitor.worked(i);
                if (monitor.isCanceled())
                    break;
            }
            return Status.OK_STATUS;
        } catch (Exception e) {
            e.printStackTrace();
            return Status.CANCEL_STATUS;
        }
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

    private void export(PodcastFeed feed, File playlistsDir) {
        try {

            // 1. Generate playlist file (.wpl file)

            String playlistName = feed.getTitle();
            StringBuffer playlistContent = new StringBuffer();
            playlistContent
                    .append("<?wpl version=\"1.0\"?>\n<smil>\n<head>\n<meta name=\"AverageRating\" content=\"0\"/>\n"
                            + "<meta name=\"TotalDuration\" content=\"0\"/>\n"
                            + "<meta name=\"ItemCount\" content=\"0\"/>\n<title>"
                            + StringEscapeUtils.escapeXml(playlistName)
                            + "</title>\n</head>\n<body>\n<seq>\n");
            List<PodcastItem> items = getPodcastItems(feed);
            for (PodcastItem item : items) {
                String type = item.getEnclosureType();
                if (type != null
                        && (type.startsWith("audio") || type.startsWith("video"))) {
                    String path = item.getEnclosureDiskpath();
                    if (path == null) {
                        path = item.getEnclosureUrl();
                    }
                    playlistContent.append("  <media src=\""
                            + StringEscapeUtils.escapeXml(path) + "\"/>\n");
                }
            }
            playlistContent.append("</seq>\n</body>\n</smil>\n");
            File playlistFile = new File(playlistsDir, playlistName + ".wpl");
            FileUtils.writeStringToFile(playlistFile, playlistContent
                    .toString());

            // 2. Add .wpl file to Windows Media Player library
            addPlayListToWMP(playlistFile);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addPlayListToWMP(File playlistFile) throws Exception {

        // Run the below VBS code to add playlist to WMP

        // Create VBS file
        // Set ArgObj = WScript.Arguments
        // Set objPlayer = CreateObject("WMPlayer.OCX" )
        // Set objMediaCollection = objPlayer.MediaCollection
        // Set objNewlist = objMediaCollection.Add (ArgObj(0))
        StringBuffer scriptContent = new StringBuffer();
        scriptContent.append("Set ArgObj = WScript.Arguments\n");
        scriptContent
                .append("Set objPlayer = CreateObject(\"WMPlayer.OCX\" )\n");
        scriptContent
                .append("Set objMediaCollection = objPlayer.MediaCollection\n");
        scriptContent
                .append("Set objNewlist = objMediaCollection.Add (ArgObj(0))\n");
        File scriptFile = File.createTempFile("addplaylist", ".vbs");
        FileUtils.writeStringToFile(scriptFile, scriptContent.toString());

        // Call VBS file
        String[] command = new String[] { "wscript", "//B",
                scriptFile.getAbsolutePath(), playlistFile.getAbsolutePath() };
        Process child = Runtime.getRuntime().exec(command);
        child.waitFor();

        // Remove VBS file
        FileUtils.deleteQuietly(scriptFile);
    }

}
