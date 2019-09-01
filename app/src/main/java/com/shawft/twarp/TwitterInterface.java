package com.shawft.twarp;

// Twitter API imports:
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;

import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import twitter4j.conf.ConfigurationBuilder;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

// Date/Time processing imports
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TwitterInterface {

//	private static String CONSUMER_KEY = "fE13v8jaEsyQMq9U7zKd8mUb6";
//	private static String CONSUMER_SECRET = "0VDEy8TmrKhIEqkCqi0JqBrDG10zXvZUlYu3kkDcwFYTO8E3us";
//	private static String ACCESS_TOKEN = "190086925-Irxh5ofRRdnilseGhT8zTXeuBWrDsoeW4O8pJSRp";
//	private static String ACCESS_TOKEN_SECRET = "2DxPjnYAyFcVWhTKq0yz86igHMcBu5HgM8qWArIK8HrTq";

	private static String CONSUMER_KEY = "F3LcIYdThWCYVnqyQl0AqdYQp";
	private static String CONSUMER_SECRET = "9gv8FfFQQQWZNmJ3a9QvgPZ6nbrLHuyha8c2109YiLlbWMP62o";
	private static String ACCESS_TOKEN = "190086925-8ttuKdwZnZ55ih80oNJUbGsKtCeEgqxm3IEOGNk7";
	private static String ACCESS_TOKEN_SECRET = "AQNiCR4y9RIu5zQ9a536TwuKt9rKCYWE6Y4qtfWjei6S8";

	private Twitter twitter = null;

	// query properties:
	private String HashTag = "";
	public String getHashTag() { return HashTag; }
	private Date StartDateTime;
	private Context UIcontext;
	private ListView TwitterViewUI;

	// timeline properties:
	private long startID = 0;
	private long tweetsPerMillisec = 0;

	// twitter fabric timeline vars:
	private SearchTimelineWrapper searchTimeline = null;
	private TweetTimelineListAdapter tweetAdapter = null;
	private long bottomID = 0;

	// Constructor:
	public TwitterInterface(String hashtag, Date datetime, Context context, ListView view) {
		// set properties:
		HashTag = hashtag;
		StartDateTime = datetime;
		TwitterViewUI = view;
		UIcontext = context;

		// initialise api authorisation:
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey(CONSUMER_KEY)
				.setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);

		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();

		// find nearest id:
		new TwitterTask().execute(StartDateTime);
	}

	private long findStartID(Date start) {

		// Twitter searches work with UTC time, setup a UTC formatter:
		SimpleDateFormat UTCformatter = new SimpleDateFormat("yyyy-MM-dd");
		UTCformatter.setTimeZone(TimeZone.getTimeZone("UTC"));

		// set lower boundary
		String lowerBound = UTCformatter.format(start);

		// set upper boundary
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("UTC"));
		c.setTime(start);
		c.add(Calendar.DATE, 1);
		String upperBound = UTCformatter.format(c.getTime());
		
		// calculate UTC offset in minutes from midnight:
		long offset = (c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE)) * 60 * 1000;

	    long baseId = 0;
	    long ceilId = 0;
	    long millisecs = 0;

		try {
			// setup boundary queries:
		    Query queryLower = new Query("a").until(lowerBound).count(1);
		    Query queryUpper = new Query("a").until(upperBound).count(1);

		    // get boundary tweets:
		    QueryResult result1 = twitter.search(queryLower);
			QueryResult result2 = twitter.search(queryUpper);
			if (result2.getCount() == 0 || result2.getTweets() == null)
				return 0;
			baseId = result1.getMaxId();
			ceilId = result2.getMaxId();
			
			// calculate time between boundaries:
			millisecs = result2.getTweets().get(0).getCreatedAt().getTime()
					- result1.getTweets().get(0).getCreatedAt().getTime();

		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}

		// calculate id count per minute:
		long tweetCount = ceilId - baseId;
		tweetsPerMillisec = tweetCount / millisecs;
				
		// generate starting point ID estimate based on calculations:
		long guessID = baseId + tweetsPerMillisec * offset;
	
		// find first tweet with the hashtag before the generated ID:
		Status status = null;
		try {
			Query q = new Query("a").maxId(guessID).count(1);
			status = twitter.search(q).getTweets().get(0);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		if (status != null) {
			// output for debugging:
			//System.out.println(status.getCreatedAt() + ": @" + status.getUser().getScreenName() + ":" + status.getText());
			startID = status.getId();
			return startID;
		}
		else
			return 0;
	}

	public void refreshFeed(Date datetime) {
		long timediff = datetime.getTime() - StartDateTime.getTime();
		long id = startID + timediff * tweetsPerMillisec;
		updateTwitter(id);
	}

	private void updateTwitter(long id) {
		if (id == 0)
			return;

		searchTimeline = new SearchTimelineWrapper(HashTag,id);
		tweetAdapter = new TweetTimelineListAdapter(UIcontext, searchTimeline);
		TwitterViewUI.setAdapter(tweetAdapter);
	}

	public void notifyUpdate() {
		synchronized (searchTimeline) {
			searchTimeline.notify();
		}
	}

	private class TwitterTask extends AsyncTask<Date, Integer, Long> {
		protected Long doInBackground(Date...params) {
			long maxid = findStartID(params[0]);
			return maxid;
		}

		protected void onPostExecute(Long result) {
			updateTwitter(result);
		}
	}

}
