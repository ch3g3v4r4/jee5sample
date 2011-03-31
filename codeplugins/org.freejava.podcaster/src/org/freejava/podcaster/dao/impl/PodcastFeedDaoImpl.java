package org.freejava.podcaster.dao.impl;

import org.freejava.podcaster.dao.PodcastFeedDao;
import org.freejava.podcaster.domain.PodcastFeed;

import com.sleepycat.persist.EntityStore;

public class PodcastFeedDaoImpl extends GenericDaoImpl<PodcastFeed, String>
        implements PodcastFeedDao {

    public PodcastFeedDaoImpl(EntityStore store) throws Exception {
        super(store);
    }

}
