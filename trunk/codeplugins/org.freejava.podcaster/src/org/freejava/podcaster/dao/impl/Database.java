package org.freejava.podcaster.dao.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.freejava.podcaster.Activator;
import org.freejava.podcaster.dao.PodcastFeedDao;
import org.freejava.podcaster.dao.PodcastItemDao;
import org.freejava.podcaster.domain.PodcastFeed;
import org.freejava.podcaster.domain.PodcastItem;
import org.freejava.podcaster.ui.VOAFeedBuilder;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

public class Database {

    private static final Database INSTANCE = new Database();

    private Environment env;
    private EntityStore podcastStore;
    private List<PodcastFeed> feeds;
    private List<PodcastItem> items;

    public static Database getInstance() {
        return INSTANCE;
    }

    private Environment getEnvironment() throws Exception {
        if (env == null) {
            File db = new File("/tmp");
            if (!db.exists()) {
                db.mkdir();
            }
            EnvironmentConfig conf = new EnvironmentConfig();
            conf.setAllowCreate(true);
            env = new Environment(db, conf);
        }
        return env;
    }

    public void initialize() throws Exception {
        EntityStore store = getPodcastStore();
        File downloadsDir = new File("/downloads");

        PodcastFeedDao pcf = new PodcastFeedDaoImpl(store);
        String efTitle = "Effortless English Podcast";
        File efFeedDir = new File(downloadsDir, efTitle);
        if (pcf.findById("1") == null) {
            //efFeedDir.mkdirs();
            PodcastFeed efEntity = new PodcastFeed("1", efTitle,
                    "Automatic English For The People", new Date(),
                    "http://www.effortlessenglish.libsyn.com/rss", efFeedDir
                            .getAbsolutePath());
            pcf.add(efEntity);
        }

        if (pcf.findById("10") == null) {
            String title = "British Council - Elementary";
            File feedDir = new File(downloadsDir, title);
            //feedDir.mkdirs();
            PodcastFeed voaEntity = new PodcastFeed("10", title,
                    "British Council Elementary", new Date(), "http://www.learnenglish.org.uk/rss/ele.xml",
                    feedDir.getAbsolutePath());
            pcf.add(voaEntity);
        }
        if (pcf.findById("11") == null) {
            String title = "British Council - Themes";
            File feedDir = new File(downloadsDir, title);
            //feedDir.mkdirs();
            PodcastFeed voaEntity = new PodcastFeed("11", title,
                    "British Council Themes", new Date(), "http://www.learnenglish.org.uk/rss/themes.xml",
                    feedDir.getAbsolutePath());
            pcf.add(voaEntity);
        }
        if (pcf.findById("12") == null) {
            String title = "British Council - Stories Poems";
            File feedDir = new File(downloadsDir, title);
            //feedDir.mkdirs();
            PodcastFeed voaEntity = new PodcastFeed("12", title,
                    "British Council Stories Poems", new Date(), "http://www.learnenglish.org.uk/rss/storiespoems.xml",
                    feedDir.getAbsolutePath());
            pcf.add(voaEntity);
        }
        if (pcf.findById("13") == null) {
            String title = "British Council - Professional";
            File feedDir = new File(downloadsDir, title);
            //feedDir.mkdirs();
            PodcastFeed voaEntity = new PodcastFeed("13", title,
                    "British Council Professional", new Date(), "http://www.learnenglish.org.uk/rss/prof.xml",
                    feedDir.getAbsolutePath());
            pcf.add(voaEntity);
        }

        // ALL VOA categories
        if (pcf.findById("20") == null) {
            String voaTitle = "VOA Special English";
            File voaFeedDir = new File(downloadsDir, voaTitle);
            //voaFeedDir.mkdirs();
            PodcastFeed voaEntity = new PodcastFeed("20", voaTitle,
                    "VOA Special English", new Date(), "fjf:voaspecialenglish",
                    voaFeedDir.getAbsolutePath());
            pcf.add(voaEntity);
        }
        //Separate category
        Map<String, String> voaLinks = new TreeMap<String, String>();
        voaLinks.put("Agriculture Report", VOAFeedBuilder.AGRICULTURE_REPORT);
        voaLinks.put("American Mosaic", VOAFeedBuilder.AMERICAN_MOSAIC);
        voaLinks.put("American Stories", VOAFeedBuilder.AMERICAN_STORIES);
        voaLinks.put("Development Report", VOAFeedBuilder.DEVELOPMENT_REPORT);
        voaLinks.put("Economics Report", VOAFeedBuilder.ECONOMICS_REPORT);
        voaLinks.put("Education Report", VOAFeedBuilder.EDUCATION_REPORT);
        voaLinks.put("Explorations", VOAFeedBuilder.EXPLORATIONS);
        voaLinks.put("Health Report", VOAFeedBuilder.HEALTH_REPORT);
        voaLinks.put("In the News", VOAFeedBuilder.IN_THE_NEWS);
        voaLinks.put("People In America", VOAFeedBuilder.PEOPLE_IN_AMERICA);
        voaLinks.put("Science In the News", VOAFeedBuilder.SCIENCE_IN_THE_NEWS);
        voaLinks.put("The Making of a Nation", VOAFeedBuilder.THE_MAKING_OF_A_NATION);
        voaLinks.put("This Is America", VOAFeedBuilder.THIS_IS_AMERICA);
        voaLinks.put("Words and Their Stories", VOAFeedBuilder.WORDS_AND_THEIR_STORIES);
        voaLinks.put("WORDMASTER", VOAFeedBuilder.WORDMASTER);
        int feedId = 21;
        for (String category: voaLinks.keySet()) {
            String keyword = voaLinks.get(category);
            String feedUrl = "fjf:voaspecialenglish:" + keyword;
            String feedIdStr = String.valueOf(feedId);
            if (pcf.findById(feedIdStr) == null) {
                String voaTitle = "VOA Special English - " + category;
                File voaFeedDir = new File(downloadsDir, voaTitle);
                //voaFeedDir.mkdirs();
                PodcastFeed voaEntity = new PodcastFeed(feedIdStr, voaTitle,
                        category, new Date(), feedUrl,
                        voaFeedDir.getAbsolutePath());
                pcf.add(voaEntity);
            }
            feedId++;
        }

        // load all items to memory
        getPodcastFeeds();
        getPodcastItems();
    }

    private EntityStore getPodcastStore() throws Exception {
        if (podcastStore == null) {
            Environment env = getEnvironment();
            StoreConfig conf = new StoreConfig();
            conf.setAllowCreate(true);
            podcastStore = new EntityStore(env, "PodcastStore", conf);
        }
        return podcastStore;
    }

    public List<PodcastFeed> getPodcastFeeds() throws Exception {
        if (feeds == null) {
            EntityStore store = getPodcastStore();
            PodcastFeedDao pcf = new PodcastFeedDaoImpl(store);
            feeds = pcf.findAll();
        }
        return feeds;
    }

    public List<PodcastItem> getPodcastItems() throws Exception {
        if (items == null) {
            EntityStore store = getPodcastStore();
            PodcastItemDao pci = new PodcastItemDaoImpl(store);
            items = pci.findAll();
        }
        return items;
    }

    public void shutdown() {
        try {
            if (items != null) {
                EntityStore store = getPodcastStore();
                PodcastItemDao pci = new PodcastItemDaoImpl(store);
                pci.removeAll();
                pci.addAll(items);
                items = null;
            }
            if (feeds != null) {
                EntityStore store = getPodcastStore();
                PodcastFeedDao pcf = new PodcastFeedDaoImpl(store);
                pcf.removeAll();
                pcf.addAll(feeds);
                feeds = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Activator.logError("Cannot synchronize entities to database", e);
        }

        if (podcastStore != null) {
            try {
                podcastStore.close();
                podcastStore = null;
            } catch (Exception e) {
                e.printStackTrace();
                Activator.logError("Cannot close database store", e);
            }
        }

        if (env != null) {
            try {
                env.cleanLog();
                env.close();
                env = null;
            } catch (Exception e) {
                e.printStackTrace();
                Activator.logError("Cannot shutdown database", e);
            }
        }

    }

}
