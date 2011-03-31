package org.freejava.podcaster.domain;

import java.util.Date;

import com.sleepycat.persist.model.Entity;

@Entity
public class PodcastFeed extends PodcastInfo {
    private String feedUrl;
    private String diskPath;

    public PodcastFeed() {
    }

    public PodcastFeed(String id, String title, String description,
            Date publicDate, String feedUrl, String diskPath) {

        super(id, title, description, publicDate);
        this.feedUrl = feedUrl;
        this.diskPath = diskPath;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public String getDiskPath() {
        return diskPath;
    }

}
