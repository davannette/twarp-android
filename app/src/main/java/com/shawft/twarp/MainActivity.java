package com.shawft.twarp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "WToBvaDrAupPFFTHA5t5C02u4";
    private static final String TWITTER_SECRET = "LzKTYKL4PJbiUzijDJO6gc9UBI1LeeTOiIiN3as3LNd8eJlFC0";

    private TwitterLoginButton loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                Log.d("IN", "SUCCESS");
                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Log.d("IN", "FAILED");
            }
        });

        // set date picker limits:
        Calendar cal = Calendar.getInstance();
        DatePicker datePicker = (DatePicker)findViewById(R.id.datePicker);
        datePicker.setMaxDate(cal.getTimeInMillis());
        cal.add(Calendar.DAY_OF_MONTH, -10);
        datePicker.setMinDate(cal.getTimeInMillis());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonOnClick(View v) {
        final EditText hashTagText = (EditText)findViewById(R.id.hashTag);
        String hashTag = hashTagText.getText().toString();
        final DatePicker startDatePicker = (DatePicker)findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker)findViewById(R.id.timePicker);
        Calendar cal = new GregorianCalendar(startDatePicker.getYear(), startDatePicker.getMonth(), startDatePicker.getDayOfMonth(),
                timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        Date startDate = cal.getTime();

        Intent intent = new Intent(this, TimelineActivity.class);
        intent.putExtra("hashtag", hashTag);
        intent.putExtra("start", startDate.getTime());
        startActivity(intent);
    }
}
