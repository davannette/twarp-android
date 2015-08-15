package com.shawft.helloworld;

/**
 * Created by David on 25/07/2015.
 */
import android.app.ListActivity;
import android.os.Bundle;

import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

public class TimelineActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);

        final SearchTimeline searchTimeline = new SearchTimeline.Builder()
                .query("#qanda until:2015-07-20")
                .build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter(this, searchTimeline);
        setListAdapter(adapter);
    }
}