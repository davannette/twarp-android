package com.shawft.twarp;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TimelineResult;

/**
 * Created by david on 5/10/2015.
 */
public class SearchTimelineWrapper implements Timeline<Tweet> {

    private SearchTimeline search = null;
    private long MaxID = 0;

    private boolean firstTime = true;


    public SearchTimelineWrapper(String hashtag, long id) {
        search = new SearchTimeline.Builder()
                .query("#" + hashtag)
                .build();
        MaxID = id;
    }

    @Override
    public void next(Long sinceId, Callback<TimelineResult<Tweet>> cb) {
        // get around bug where max_id cannot be included in the query:
        if (firstTime)
            search.previous(MaxID, cb);
        else
            search.next(sinceId, cb);
    }

    /**
     * Loads Tweets with id less than (older than) maxId.
     * @param maxId maximum id of the Tweets to load (exclusive).
     * @param cb callback.
     */
    @Override
    public void previous(Long maxId, Callback<TimelineResult<Tweet>> cb) {
        search.previous(maxId, cb);
    }
}
