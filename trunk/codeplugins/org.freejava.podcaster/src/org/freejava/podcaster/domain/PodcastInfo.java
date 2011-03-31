package org.freejava.podcaster.domain;

import java.util.Date;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PrimaryKey;

@Persistent
public abstract class PodcastInfo {
    @PrimaryKey
    private String id;
    private String title;
    private String description;
    private Date publishDate;

    public PodcastInfo() {
    }

    public PodcastInfo(String id, String title, String description,
            Date publishDate) {
        super();
        if (id == null || title == null)
    		throw new IllegalArgumentException();

        this.id = id;
        this.title = title;
        this.description = description;
        this.publishDate = publishDate;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getPublishDate() {
        return publishDate;
    }

}
