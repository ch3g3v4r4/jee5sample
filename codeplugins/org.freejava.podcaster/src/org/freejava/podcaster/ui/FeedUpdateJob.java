package org.freejava.podcaster.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.freejava.podcaster.Activator;
import org.freejava.podcaster.dao.impl.Database;
import org.freejava.podcaster.domain.PodcastFeed;
import org.freejava.podcaster.domain.PodcastItem;

import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class FeedUpdateJob extends Job {

    private Database database;

    public FeedUpdateJob(Database database) {
        super("Updating Feeds");
        this.database = database;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            List<PodcastFeed> feeds;
            synchronized (database) {
                feeds = new ArrayList<PodcastFeed>(database.getPodcastFeeds());
            }
            monitor.beginTask("Updating Feeds", feeds.size());
            for (int i = 0; i < feeds.size(); i++) {
                PodcastFeed feed = feeds.get(i);
                String url = feed.getFeedUrl();
                if (url.startsWith("http")) {
                    monitor.subTask("Feed " + url);
                    synchronize(feed, monitor);
                }
                monitor.worked(i);
                if (monitor.isCanceled())
                    break;
            }
            return Status.OK_STATUS;
        } catch (Exception e) {
            Activator.logError("Problem when updating feeds", e);
            return Status.CANCEL_STATUS;
        }
    }

    private void synchronize(PodcastFeed feed, IProgressMonitor monitor) {
        try {

            List<PodcastItem> remoteItemList;
            XmlReader reader = null;
            try {
                SyndFeedInput input = new SyndFeedInput();
                reader = new XmlReader(new URL(feed.getFeedUrl()));
                SyndFeed syndFeed = input.build(reader);
                remoteItemList = getFeedItems(syndFeed, feed.getId());
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
            updateLocalItems(feed, remoteItemList);

        } catch (Exception e) {
            Activator.logError("Problem when synchronizing feed: "
                    + feed.getFeedUrl(), e);
        }
    }

    private List<PodcastItem> getFeedItems(SyndFeed feed, String feedId)
            throws Exception {
        List<PodcastItem> result = new ArrayList<PodcastItem>();

        // FeedInformation feedInfo = (FeedInformation)
        // feed.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd");
        for (Object element : feed.getEntries()) {
            SyndEntry entry = (SyndEntry) element;
            // EntryInformation entryInfo = (EntryInformation)
            // entry.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd");
            if (entry.getEnclosures().size() > 0) {
                SyndEnclosure enc = (SyndEnclosure) entry.getEnclosures()
                        .get(0);
                String id = UUID.randomUUID().toString();
                String title = entry.getTitle();
                String description = entry.getDescription() != null ? entry
                        .getDescription().getValue() : null;
                Date publishDate = entry.getPublishedDate();
                String podcastFeedId = feedId;
                String link = entry.getLink();
                String guid = entry.getUri();
                String enclosureUrl = enc.getUrl();
                String enclosureDiskpath = null;
                Long enclosureLength = enc.getLength();
                String enclosureType = enc.getType();
                PodcastItem item = new PodcastItem(id, title, description,
                        publishDate, podcastFeedId, link, guid, enclosureUrl,
                        enclosureDiskpath, enclosureLength, enclosureType, null);
                result.add(item);
            }
        }
        return result;
    }


    private void updateLocalItems(PodcastFeed feed, List<PodcastItem> remoteItems) throws Exception {
        synchronized (database) {
            List<String> localItemGUIDs = new ArrayList<String>();
            for (PodcastItem item : database.getPodcastItems()) {
                if (item.getPodcastFeedId().equals(feed.getId())) {
                    localItemGUIDs.add(item.getGuid());
                }
            }
            for (PodcastItem item : remoteItems) {
                if (!localItemGUIDs.contains(item.getGuid())) {
                    database.getPodcastItems().add(item);
                }
            }
        }
    }


}
