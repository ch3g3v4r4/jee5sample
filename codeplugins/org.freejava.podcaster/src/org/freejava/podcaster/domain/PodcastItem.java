package org.freejava.podcaster.domain;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.sleepycat.persist.model.Entity;

@Entity
public class PodcastItem extends PodcastInfo {
    private String podcastFeedId;
    private String link;
    private String guid;
    private String enclosureUrl;
    private String enclosureDiskpath;
    private Long enclosureLength;
    private String enclosureType;
    private String category;

    public PodcastItem() {
    }

    public PodcastItem(String id, String title, String description,
            Date publishDate, String podcastFeedId, String link, String guid,
            String enclosureUrl, String enclosureDiskpath,
            Long enclosureLength, String enclosureType, String category) {

        super(id, title, description, publishDate);

        if (podcastFeedId == null || link == null || guid == null
                || enclosureUrl == null || enclosureLength == null
                || enclosureType == null)
            throw new IllegalArgumentException();

        this.podcastFeedId = podcastFeedId;
        this.link = link;
        this.guid = guid;
        this.enclosureUrl = enclosureUrl;
        this.enclosureDiskpath = enclosureDiskpath;
        this.enclosureLength = enclosureLength;
        this.enclosureType = enclosureType;
        this.category = category;
    }

    public String getPodcastFeedId() {
        return podcastFeedId;
    }

    public String getLink() {
        return link;
    }

    public String getGuid() {
        return guid;
    }

    public String getEnclosureUrl() {
        return enclosureUrl;
    }

    public String getEnclosureDiskpath() {
        return enclosureDiskpath;
    }

    public void setEnclosureDiskpath(String enclosureDiskpath) {
        this.enclosureDiskpath = enclosureDiskpath;
    }

    public Long getEnclosureLength() {
        return enclosureLength;
    }

    public String getEnclosureType() {
        return enclosureType;
    }
    public String getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean result;
        if (this == anObject) {
            result = true;
        } else if (anObject instanceof PodcastItem) {
            PodcastItem it2 = (PodcastItem)anObject;
            result = StringUtils.equals(podcastFeedId, it2.podcastFeedId)
               && StringUtils.equals(link, it2.link)
               && StringUtils.equals(guid, it2.guid)
               && StringUtils.equals(enclosureUrl, it2.enclosureUrl)
               && StringUtils.equals(enclosureDiskpath, it2.enclosureDiskpath)
               && StringUtils.equals(String.valueOf(enclosureLength), String.valueOf(it2.enclosureLength))
               && StringUtils.equals(String.valueOf(enclosureLength), String.valueOf(it2.enclosureLength))
               && StringUtils.equals(enclosureType, it2.enclosureType)
               && StringUtils.equals(category, it2.category);
        } else {
            result = false;
        }
        return result;
    }
}
