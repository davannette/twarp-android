package com.shawft.twarp;

/**
 * Created by David on 25/07/2015.
 */
import java.util.Date;

import android.app.ListActivity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import android.os.Handler;

public class TimelineActivity extends ListActivity {

    private String hashTag = "";
    private long lastRefresh = 0;
    private long realTime = 0;

    TwitterInterface twitter = null;

    TimerController timer = null;

    static boolean first_time = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);

        Bundle bundle = getIntent().getExtras();
        hashTag = bundle.getString("hashtag");
        Date start = new Date(bundle.getLong("start"));

        // start timer and twitter feed:
        timer = new TimerController(start);
        twitter = new TwitterInterface(hashTag, start, this, (ListView)findViewById(android.R.id.list));

        mTimeHandler.post(mUpdateTimeTask);
    }

    public void refreshOnClick(View v) {
        twitter.refreshFeed(timer.getTime());
    }

    public void updateData(View v) {
        twitter.notifyUpdate();
    }

    public void jumpTimer(View v) {
        switch (v.getId()) {
            case R.id.jump_back:
                timer.skip(TimerController.Skip.BIG, TimerController.Direction.BACK);
                break;

            case R.id.skip_back:
                timer.skip(TimerController.Skip.SMALL, TimerController.Direction.BACK);
                break;

            case R.id.skip:
                timer.skip(TimerController.Skip.SMALL, TimerController.Direction.FORWARD);
                break;

            case R.id.jump:
                timer.skip(TimerController.Skip.BIG, TimerController.Direction.FORWARD);
                break;
        }
    }

    public void clickPlayPause(View v) {
        if (timer.togglePause()) {
            ((Button) v).setText("Play");
            // stop updating clock
            mTimeHandler.removeCallbacks(mUpdateTimeTask);
        } else {
            ((Button) v).setText("Pause");
            // restart clock updater:
            mTimeHandler.post(mUpdateTimeTask);
        }

    }

    private Handler mTimeHandler = new Handler();

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            TextView clock = (TextView)findViewById(R.id.clock);
            clock.setText(formatForClock(timer.getTime()));
            mTimeHandler.postDelayed(this, 200);
        }
    };

    private String formatForClock(final Date date) {
        String strDate = "";
        int day = Integer.parseInt(new SimpleDateFormat("d").format(date));
        if (day >= 11 && day <= 13) {
            strDate = new SimpleDateFormat("EE d'th'").format(date);
            // return new SimpleDateFormat("EE d'th'\nh:mm:ss a").format(date);
        }
        switch (day % 10) {
            case 1:
                strDate = new SimpleDateFormat("EE d'st'").format(date);
                // return new SimpleDateFormat("EE d'st'\nh:mm:ss a").format(date);
                break;
            case 2:
                strDate = new SimpleDateFormat("EE d'nd'").format(date);
                // return new SimpleDateFormat("EE d'nd'\nh:mm:ss a").format(date);
            case 3:
                strDate = new SimpleDateFormat("EE d'rd'").format(date);
                // return new SimpleDateFormat("EE d'rd'\nh:mm:ss a").format(date);
            default:
                strDate = new SimpleDateFormat("EE d'th'").format(date);
                // return new SimpleDateFormat("EE d'th'\nh:mm:ss a").format(date);
        }
        String strTime = new SimpleDateFormat("h:mm:ss a").format(date);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            return strDate + " " + strTime;
        else
            return strDate + "\n" + strTime;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //setContentView(R.layout.timeline);
    }

    protected void onDestroy() {
        // stop updating the clock
        mTimeHandler.removeCallbacks(mUpdateTimeTask);
        super.onDestroy();
    }
}