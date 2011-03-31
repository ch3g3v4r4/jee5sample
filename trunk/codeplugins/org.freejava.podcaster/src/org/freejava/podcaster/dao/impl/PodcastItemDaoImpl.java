package org.freejava.podcaster.dao.impl;

import org.freejava.podcaster.dao.PodcastItemDao;
import org.freejava.podcaster.domain.PodcastItem;

import com.sleepycat.persist.EntityStore;

public class PodcastItemDaoImpl extends GenericDaoImpl<PodcastItem, String>
        implements PodcastItemDao {

    public PodcastItemDaoImpl(EntityStore store) throws Exception {
        super(store);
    }

}
