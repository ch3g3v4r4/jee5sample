package org.freejava.podcaster.ui;

import org.freejava.podcaster.domain.PodcastItem;

public interface PlayingIconIndicator {
	void showPlayingIcon(PodcastItem feedItem);
	void hidePlayingIcon(PodcastItem currentItem);
}
