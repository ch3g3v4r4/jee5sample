package org.freejava.podcaster.ui;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.freejava.podcaster.Activator;
import org.freejava.podcaster.dao.impl.Database;
import org.freejava.podcaster.domain.PodcastFeed;
import org.freejava.podcaster.domain.PodcastItem;

public class VOAUpdateJob extends Job {
    public static void main(String[] args) {
        NullProgressMonitor mon = new NullProgressMonitor();
        Database db = new Database();
        VOAUpdateJob job = new VOAUpdateJob(db);
        job.run(mon);
    }
    
    public static final String VOA_SPECIAL_ENGLISH = "fjf:voaspecialenglish";

    private Database database;

    public VOAUpdateJob(Database database) {
        super("Updating VOA");
        this.database = database;
    }

    private Map<PodcastFeed, Map<String, PodcastItem>> getVoaFeeds()
            throws Exception {
        Map<PodcastFeed, Map<String, PodcastItem>> voaFeeds = new Hashtable<PodcastFeed, Map<String, PodcastItem>>();
        // Get a copy of database items for reference
        // and initialize article mappings
        for (PodcastFeed feed : database.getPodcastFeeds()) {
            if (feed.getFeedUrl().startsWith(VOA_SPECIAL_ENGLISH)) {
                voaFeeds.put(feed, new Hashtable<String, PodcastItem>());
                for (PodcastItem item : database.getPodcastItems()) {
                    if (item.getPodcastFeedId().equals(feed.getId())) {
                        voaFeeds.get(feed).put(item.getLink(), item);
                    }
                }
            }
        }
        return voaFeeds;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            monitor.beginTask("Updating VOA", IProgressMonitor.UNKNOWN);

            // Get a copy of database items for reference
            Map<String, PodcastItem> copyItems = getVoaItems();

            VOAFeedBuilder builder = new VOAFeedBuilder();

            // Add new VOA articles
            List<PodcastItem> currentArticles = builder.getCurrentEntries(
                    "dummy", monitor, copyItems);
            updateItemsToDatabase(currentArticles);
            if (monitor.isCanceled())
                return Status.OK_STATUS;

            // Get a list of archive week links
            List<String> weekLinks = builder.getArchiveWeekLinks();

            // Get archive articles for week links
            for (String weekLink : weekLinks) {
                List<PodcastItem> articles = builder.getArticles(weekLink,
                        "dummy", monitor, copyItems);
                updateItemsToDatabase(articles);
                if (monitor.isCanceled())
                    return Status.OK_STATUS;
            }

            // remove invalid "current" articles in database
            Set<String> currentLinks = new HashSet<String>();
            for (PodcastItem currentItem : currentArticles) {
                currentLinks.add(currentItem.getLink());
            }
            synchronized (database) {
                Map<PodcastFeed, Map<String, PodcastItem>> voaFeeds = getVoaFeeds();
                for (String url : copyItems.keySet()) {
                    // if this url not exist in currentArticles's links ->
                    // invalid
                    if (!url.contains("/archive/")
                            && !currentLinks.contains(url)
                            && notExistDocument(url)) {
                        for (PodcastFeed feed : voaFeeds.keySet()) {
                            Map<String, PodcastItem> feedMembers = voaFeeds
                                    .get(feed);
                            if (feedMembers.containsKey(url)) {
                                PodcastItem item = feedMembers.get(url);
                                feedMembers.remove(url);
                                database.getPodcastItems().remove(item);
                            }
                        }
                    }
                }
            }

            return Status.OK_STATUS;
        } catch (Exception e) {
            Activator.logError("Problem when updating VOA", e);
            return Status.CANCEL_STATUS;
        }
    }

    private Map<String, PodcastItem> getVoaItems() throws Exception {
        Map<String, PodcastItem> copyItems = new Hashtable<String, PodcastItem>();
        synchronized (database) {
            for (PodcastFeed feed : database.getPodcastFeeds()) {
                if (feed.getFeedUrl().startsWith(VOA_SPECIAL_ENGLISH)) {
                    for (PodcastItem item : database.getPodcastItems()) {
                        if (item.getPodcastFeedId().equals(feed.getId())) {
                            copyItems.put(item.getLink(), item);
                        }
                    }
                }
            }
        }
        return copyItems;
    }

    private boolean notExistDocument(String url) {
        boolean exist = false;
        try {
            URLConnection urlConnection = new URL(url).openConnection();
            if (urlConnection instanceof HttpURLConnection) {
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                httpURLConnection.setRequestMethod("HEAD");
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    exist = true;
                }
            }
        } catch (Exception e) {
            Activator.logWarning("Article is moved to archive", e);
        }
        return exist;
    }

    private void updateItemsToDatabase(List<PodcastItem> items)
            throws Exception {

        synchronized (database) {
            Map<PodcastFeed, Map<String, PodcastItem>> voaFeeds = getVoaFeeds();
            for (PodcastItem currentItem : items) {
                String itemCategory = currentItem.getCategory();
                for (PodcastFeed feed : voaFeeds.keySet()) {
                    String feedUrl = feed.getFeedUrl();
                    String feedCategory;
                    if (feedUrl.length() == VOA_SPECIAL_ENGLISH.length()) {
                        feedCategory = null;
                    } else {
                        feedCategory = feedUrl.substring(VOA_SPECIAL_ENGLISH
                                .length() + 1);
                    }
                    if (itemCategory == null || feedCategory != null
                            && !feedCategory.contains(itemCategory)) {
                        continue;
                    }
                    Map<String, PodcastItem> feedMembers = voaFeeds.get(feed);
                    if (!feedMembers.containsKey(currentItem.getLink())) {
                        PodcastItem newItem = makeCopy(currentItem, feed
                                .getId());
                        feedMembers.put(newItem.getLink(), newItem);
                        database.getPodcastItems().add(newItem);
                    }
                }
            }
        }
    }

    private PodcastItem makeCopy(PodcastItem prototype, String feedId) {
        PodcastItem entry = new PodcastItem(UUID.randomUUID().toString(),
                prototype.getTitle(), prototype.getDescription(), prototype
                        .getPublishDate(), feedId, prototype.getLink(),
                prototype.getGuid(), prototype.getEnclosureUrl(), prototype
                        .getEnclosureDiskpath(),
                prototype.getEnclosureLength(), prototype.getEnclosureType(),
                prototype.getCategory());
        return entry;
    }

}
