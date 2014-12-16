package pk.edu.iba.TrafficCongestion;

import java.util.List;

import pk.edu.iba.TrafficCongestion.R;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class TwitterApp extends Activity implements OnClickListener {
	private static final String TAG = "Twitter";

	private Button buttonLogin;
	private Button getTweetButton;
	private TextView tweetText;
	private ScrollView scrollView;

	private static Twitter twitter;
	private static RequestToken requestToken;
	private static SharedPreferences mSharedPreferences;
	private static TwitterStream twitterStream;
	private boolean running = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	/*	
		mSharedPreferences = getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE);
		scrollView = (ScrollView)findViewById(R.id.scrollView);
		tweetText =(TextView)findViewById(R.id.tweetText);
		getTweetButton = (Button)findViewById(R.id.getTweet);
		getTweetButton.setOnClickListener(this);
		buttonLogin = (Button) findViewById(R.id.twitterLogin);
		buttonLogin.setOnClickListener(this);
		
		*//**
		 * Handle OAuth Callback
		 *//*
		Uri uri = getIntent().getData();
		if (uri != null && uri.toString().startsWith(Const.CALLBACK_URL)) {
			String verifier = uri.getQueryParameter(Const.IEXTRA_OAUTH_VERIFIER);
            try { 
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier); 
                Editor e = mSharedPreferences.edit();
                e.putString(Const.PREF_KEY_TOKEN, accessToken.getToken()); 
                e.putString(Const.PREF_KEY_SECRET, accessToken.getTokenSecret()); 
                e.commit();
	        } catch (Exception e) { 
	                Log.e(TAG, e.getMessage()); 
	                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show(); 
			}
        }		
	}
	
	protected void onResume() {
		super.onResume();

		if (isConnected()) {
			String oauthAccessToken = mSharedPreferences.getString(Const.PREF_KEY_TOKEN, "");
			String oAuthAccessTokenSecret = mSharedPreferences.getString(Const.PREF_KEY_SECRET, "");

			ConfigurationBuilder confbuilder = new ConfigurationBuilder();
			Configuration conf = confbuilder
								.setOAuthConsumerKey(Const.CONSUMER_KEY)
								.setOAuthConsumerSecret(Const.CONSUMER_SECRET)
								.setOAuthAccessToken(oauthAccessToken)
								.setOAuthAccessTokenSecret(oAuthAccessTokenSecret)
								.build();
			twitterStream = new TwitterStreamFactory(conf).getInstance();
			
			buttonLogin.setText(R.string.label_disconnect);
			getTweetButton.setEnabled(true);
		} else {
			buttonLogin.setText(R.string.label_connect);
		}*/
		
		
		
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("arDenCLga2UaqpS3bFtlgR4RG")
                .setOAuthConsumerSecret(
                        "Jx9Sv3JI1oJHMmur1HODCxR4zGWcdfzUecSizOi94w4zAWKNNA")
                .setOAuthAccessToken(
                        "2877830571-rFgjTOIWouq5ZBDL9M6WsqIRE5DYWckaRwQhJ5S")
                        .setUseSSL(true)
                .setOAuthAccessTokenSecret(
                        "LNJYlGn1nYFl3MM7HAYoLxqagGv8Aa9znPyRFUTV0UQ27");
        Toast.makeText(this, "I made it !!", Toast.LENGTH_LONG).show();
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        try {
            List<Status> statuses;
            String user;
            user = "KhiTraffic";
            statuses = twitter.getUserTimeline(user);
            Log.i("Status Count", statuses.size() + " Feeds");
            for (int i = 0; i < statuses.size(); i++) {
                Status status = statuses.get(i);
                String mStatus=status.getText();
            //    mStatus= mStatus.substring(mStatus.indexOf(":"), mStatus.length());
                		
                
                Toast.makeText(this, mStatus, Toast.LENGTH_LONG).show();
                Log.i("Tweet Count " + (i + 1), status.getText() + "\n\n");
            }
       
        } catch (TwitterException te) {
            te.printStackTrace();
            Toast.makeText(this, "Error !!", Toast.LENGTH_LONG).show();
        }
        
	}

	/**
	 * check if the account is authorized
	 * @return
	 */
	private boolean isConnected() {
		return mSharedPreferences.getString(Const.PREF_KEY_TOKEN, null) != null;
	}

	private void askOAuth() {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(Const.CONSUMER_KEY);
		configurationBuilder.setOAuthConsumerSecret(Const.CONSUMER_SECRET);
		Configuration configuration = configurationBuilder.build();
		twitter = new TwitterFactory(configuration).getInstance();
		
		try {
			requestToken = twitter.getOAuthRequestToken(Const.CALLBACK_URL);
			Toast.makeText(this, "Please authorize this app!", Toast.LENGTH_LONG).show();
			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove Token, Secret from preferences
	 */
	private void disconnectTwitter() {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.remove(Const.PREF_KEY_TOKEN);
		editor.remove(Const.PREF_KEY_SECRET);

		editor.commit();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.twitterLogin:
			if (isConnected()) {
				disconnectTwitter();
				buttonLogin.setText(R.string.label_connect);
			} else {
				askOAuth();
			}
			break;
		case R.id.getTweet:
			if (running) {
				stopStreamingTimeline();
				running = false;
				getTweetButton.setText("start streaming");
			} else {
				startStreamingTimeline();
				running = true;
				getTweetButton.setText("stop streaming");
			}
			break;
		}
	}
	
	private void stopStreamingTimeline() {
		twitterStream.shutdown();
	}

	public void startStreamingTimeline() {
	    UserStreamListener listener = new UserStreamListener() {

			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				System.out.println("deletionnotice");
			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
				System.out.println("scrubget");
			}

			@Override
			public void onStatus(Status status) {
				final String tweet = "@" + status.getUser().getScreenName() + " : " + status.getText() + "\n"; 
				System.out.println(tweet);
				tweetText.post(new Runnable() {
					@Override
					public void run() {
						tweetText.append(tweet);
						scrollView.fullScroll(View.FOCUS_DOWN);
					}
				});
			}

			@Override
			public void onTrackLimitationNotice(int arg0) {
				System.out.println("trackLimitation");
			}

			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onBlock(User arg0, User arg1) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onDeletionNotice(long arg0, long arg1) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onDirectMessage(DirectMessage arg0) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onFavorite(User arg0, User arg1, Status arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onFollow(User arg0, User arg1) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onFriendList(long[] arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onUnblock(User arg0, User arg1) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onUnfavorite(User arg0, User arg1, Status arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onUserListCreation(User arg0, UserList arg1) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onUserListDeletion(User arg0, UserList arg1) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onUserListMemberAddition(User arg0, User arg1, UserList arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onUserListMemberDeletion(User arg0, User arg1,  UserList arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onUserListSubscription(User arg0, User arg1, UserList arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onUserListUnsubscription(User arg0, User arg1, UserList arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onUserListUpdate(User arg0, UserList arg1) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onUserProfileUpdate(User arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				
			}
	    };
        twitterStream.addListener(listener);
        twitterStream.user();
	}
}
