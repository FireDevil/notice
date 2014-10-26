package f1.notice.board;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.android.vending.billing.Purchase;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * An activity representing a list of NOT. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link OverviewDetailsActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link OverviewFragment} and the item details
 * (if present) is a {@link OverviewDetailsFragment}.
 * <p>
 * This activity also implements the required
 * {@link OverviewFragment.Callbacks} interface
 * to listen for item selections.
 */
public class Main extends FragmentActivity
        implements OverviewFragment.Callbacks,
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        FeedFragment.OnFeedInteractionListener,
        ExpandableAdapter.Callbacks,
        OverviewDetailsFragment.Callbacks,
        OverviewParagraphFragment.Callbacks{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    final static String ScreenName = "F1NoticeBoard";
    final static String LOG_TAG = "F1NoticeBoard";
    private int minutes = 60;
    int started = 0;

    private IabHelper mHelper;
    private boolean mBillingServiceReady;

    AdView adView;
    MenuItem item;
    boolean adShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.CustomActionBarTheme);
        super.onCreate(savedInstanceState);


            // Do your oncreate stuff because there is no bundle
            requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
            setContentView(R.layout.activity_not_list);

            initialiseBilling();

            getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90CC0000")));
            getActionBar().setTitle(R.string.fnb);//getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00cc0000")));
            getActionBar().setDisplayShowHomeEnabled(true);
            getActionBar().setIcon(new ColorDrawable(android.R.color.transparent));

            //deleteDatabase("fia.db");

            mNavigationDrawerFragment = (NavigationDrawerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
            mTitle = getTitle();

            // Set up the drawer.
            mNavigationDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));


            OverviewFragment frag = new OverviewFragment();

            if (findViewById(R.id.main_detail_container) != null) {

                findViewById(R.id.main_detail_container).setVisibility(View.GONE);
                // The detail container view will be present only in the
                // large-screen layouts (res/values-large and
                // res/values-sw600dp). If this view is present, then the
                // activity should be in two-pane mode.
                mTwoPane = true;

                adView = (AdView) findViewById(R.id.adView);

            }

        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putBoolean(getString(R.string.app_name), mTwoPane);
            frag.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, frag,"Overview").commit();



        }
        handleIntent(getIntent());
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            onOptionsItemSelected(item);
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 1000*60*minutes); //execute in every 50000 ms
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {

        Intent browserIntent;

        switch(position){
            case(1):
                Bundle args = new Bundle();
                args.putBoolean(getString(R.string.app_name), mTwoPane);
                OverviewFragment l = new OverviewFragment();
                l.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.container,l,"Overview").commit();

                if(mTwoPane){
                    FrameLayout fl = (FrameLayout)findViewById(R.id.main_detail_container);
                    fl.setVisibility(View.GONE);
                }
                break;
            case(2):
                FeedFragment c = new FeedFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, c).commit();
                setTheme(R.style.AppTheme);


                if(mTwoPane){
                    FrameLayout fl = (FrameLayout)findViewById(R.id.main_detail_container);
                    fl.setVisibility(View.GONE);
                }
                break;
            case(3):
                Bundle arg = new Bundle();
                arg.putString("section","S");
                ItemFragment sporting = new ItemFragment();
                sporting.setArguments(arg);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, sporting).commit();

                if(mTwoPane){
                    FrameLayout fl = (FrameLayout)findViewById(R.id.main_detail_container);
                    fl.setVisibility(View.GONE);
                }
                break;
            case(4):
                Bundle argu = new Bundle();
                argu.putString("section","T");
                ItemFragment technical = new ItemFragment();
                technical.setArguments(argu);
                getSupportFragmentManager().beginTransaction().replace(R.id.container,technical).commit();

                if(mTwoPane){
                    FrameLayout fl = (FrameLayout)findViewById(R.id.main_detail_container);
                    fl.setVisibility(View.GONE);
                }
                break;
            case (7):
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fia.com/sport/regulations?f[0]=field_regulation_category%3A82"));
                startActivity(browserIntent);
                break;
            case (6):
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.twitter.com"));
                startActivity(browserIntent);
                break;
            case (5):
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.Android-Timing.com"));
                startActivity(browserIntent);
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView

        if(ACP.isAd_free()){
            menu.findItem(R.id.remove_ad).setVisible(false);
        }

        this.item = menu.findItem(R.id.refresh_feed);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        int linlayId = getResources().getIdentifier("android:id/search_plate", null, null);
        ViewGroup v = (ViewGroup) searchView.findViewById(linlayId);
        v.setBackgroundResource(R.drawable.searchviewwhite);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setQueryHint(getString(R.string.searchHint));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        if(started == 0){
            callAsynchronousTask();
            started++;
        }



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent browserIntent;

        switch(item.getItemId()){

            case(R.id.refresh_feed):
                this.item =  item;

                item.setActionView(R.layout.circle);
                downloadTweets();

                if(ACP.isAd_free() && adView!=null){
                    adView.setVisibility(View.GONE);
                }
                break;
            /*case(R.id.fia):
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fia.com/sport/regulations?f[0]=field_regulation_category%3A82"));
                startActivity(browserIntent);
                break;
            case (R.id.twitter):
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.twitter.com"));
                startActivity(browserIntent);
                break;
            case (R.id.website):
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.Android-Timing.com"));
                startActivity(browserIntent);
                break;*/
            case (R.id.email):
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",getString(R.string.emailAdd), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailSubject));
                startActivity(Intent.createChooser(emailIntent, getString(R.string.emailPopUp)));
                break;
            case (R.id.remove_ad):
                if (!mBillingServiceReady) {
                    Toast.makeText(this, "Purchase requires Google Play Store (billing) on your Android.", Toast.LENGTH_LONG).show();
                    break;
                }

                String payload = ""; //generatePayloadForSKU(ACP.getAd_removal()); // This is based off your own implementation.
                mHelper.launchPurchaseFlow(this, ACP.getAd_removal(), 0, mPurchaseFinishedListener, payload);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFeedInteraction(int id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.

            FrameLayout fl = (FrameLayout)findViewById(R.id.main_detail_container);
            fl.setVisibility(View.VISIBLE);

            Bundle args = new Bundle();
            args.putInt("TWEET ID",id);
            args.putBoolean("twopane",mTwoPane);
            OverviewParagraphFragment fragment = new OverviewParagraphFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_detail_container, fragment)
                    .commit();
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, OverviewDetailsActivity.class);
            detailIntent.putExtra("TWEET ID",id);
            detailIntent.putExtra("twopane",mTwoPane);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onChildSelected(int id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            FrameLayout fl = (FrameLayout)findViewById(R.id.main_detail_container);
            fl.setVisibility(View.VISIBLE);

            Bundle args = new Bundle();
            args.putInt("§ ID",id);
            OverviewDetailsFragment fragment = new OverviewDetailsFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_detail_container, fragment)
                    .commit();
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, OverviewDetailsActivity.class);
            detailIntent.putExtra("§ ID",id);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onRecentSelected(String query) {
        Intent i = new Intent(this, SearchResultActivity.class);
        i.putExtra(SearchManager.QUERY,query);
        startActivity(i);
    }

    @Override
    public void onTweetSelected(String chapter, String subchapter, String section) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            FrameLayout fl = (FrameLayout)findViewById(R.id.main_detail_container);
            fl.setVisibility(View.VISIBLE);

            Bundle args = new Bundle();
            args.putString("TWEET chapter",chapter);
            args.putString("TWEET subchapter",subchapter);
            args.putString("TWEET section", section);
            OverviewDetailsFragment fragment = new OverviewDetailsFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_detail_container, fragment)
                    .commit();
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, OverviewDetailsActivity.class);
            detailIntent.putExtra("TWEET chapter",chapter);
            detailIntent.putExtra("TWEET subchapter",subchapter);
            detailIntent.putExtra("TWEET section",section);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onNewIntent(Intent i){
       //super.onNewIntent(i);
        handleIntent(i);

        return;
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH == intent.getAction() || Intent.ACTION_ANSWER == intent.getAction()) {

            String query;
            if(Intent.ACTION_ANSWER == intent.getAction()){
                query = intent.getDataString();
            }else{
                query = intent.getStringExtra(SearchManager.QUERY);
            }

            if(query.contains(" ")){
                query.replace(' ','%');
            }

            Intent searchIntent = new Intent(this,SearchResultActivity.class);
            searchIntent.putExtra(SearchManager.QUERY,query);
            startActivity(searchIntent);
        }

        if (Intent.ACTION_VIEW == intent.getAction()) {
            String where = intent.getDataString();

            /*if (mTwoPane) {
                // In two-pane mode, show the detail view in this activity by
                // adding or replacing the detail fragment using a
                // fragment transaction.
                Bundle args = new Bundle();
                args.putString("WHERE", where);
                OverviewDetailsFragment fragment = new OverviewDetailsFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.not_detail_container, fragment)
                        .commit();

            } else {
                // In single-pane mode, simply start the detail activity
                // for the selected item ID.*/

                Intent detailIntent = new Intent(this, OverviewDetailsActivity.class);
                detailIntent.putExtra("WHERE",where);
                startActivity(detailIntent);
            //}
        }

    }

    public void downloadTweets() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadTwitterTask().execute(ScreenName);
        } else {
            Toast.makeText(this,getString(R.string.noNetwork),Toast.LENGTH_SHORT).show();
        }
    }

    // Uses an AsyncTask to download a Twitter user's timeline
    private class DownloadTwitterTask extends AsyncTask<String, Void, String> {
        final static String CONSUMER_KEY = "Pdwc1nIkxAF1GsM4Ifpd4g";
        final static String CONSUMER_SECRET = "lDxZ2u87austQlTcquuNw908NKYfIarhwunUm4GM5s";
        final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
        final static String TwitterStreamURL = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=";

        @Override
        protected String doInBackground(String... screenNames) {
            String result = null;

            if (screenNames.length > 0) {
                result = getTwitterStream(screenNames[0]);
            }
            return result;
        }

        // onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
        @Override
        protected void onPostExecute(String result) {
            Twitter twits = jsonToTwitter(result);

            ArrayList<String> arr = new ArrayList<String>();
            int i = 1;
            DBHelper db = new DBHelper();
            db.delete("Tweets","");
            Cursor pre = db.select("SELECT * FROM Tweets");
            int tweetCounter = 0;
            int oldTweets = 0;
            ContentValues values;

            // lets write the results to the console as well
            for (Tweet tweet : twits) {

                if(oldTweets > tweetCounter){
                    tweetCounter++;
                    continue;
                }

                int start =-1;
                int end = 0;

                String twText = tweet.getText();

                for( int c = 0; c < twText.length();c++){



                    if(twText.charAt(c) == '§'){
                        start = c+1;
                        continue;
                    }

                    if(twText.charAt(c) == ' ' && start >= 0){
                        end = c;
                        break;
                    }

                }

                String click;
                String chapter;
                String subchapter;
                String section;

                if(start >= 0){
                    click = twText.substring(start,end);
                    chapter = click.substring(0,click.indexOf("."));
                    subchapter = click.substring(click.indexOf(".")+1,click.indexOf("-"));
                    section = click.substring(click.indexOf("-")+1,click.length());
                }else{
                    click = "";
                    chapter = "";
                    subchapter = "";
                    section = "";
                }

                if(subchapter.length()>2 || section.length() > 1 || click.length() > 7){
                    break;
                }


                values = new ContentValues();
                values.put("text", tweet.getText());
                values.put("date_created", tweet.getDateCreated());
                values.put("date_downloaded", new Date().toString());
                values.put("§", chapter);
                values.put("sub§", subchapter);
                values.put("section", section);

                db.insert(getString(R.string.tableTweets),values);

            }

            Cursor c2 = db.select("SELECT * FROM Tweets");

            pre.close();
            c2.close();
            db.close();

            item.collapseActionView();
            item.setActionView(null);

            FeedFragment feed = (FeedFragment)getSupportFragmentManager().findFragmentById(R.id.listed);

            OverviewFragment f = (OverviewFragment)getSupportFragmentManager().findFragmentByTag("Overview");

            if(f != null){
                f.refreshList();
            }

            if(feed.visible()){
                feed.refreshList();
            }

        }

        // converts a string of JSON data into a Twitter object
        private Twitter jsonToTwitter(String result) {
            Twitter twits = null;
            if (result != null && result.length() > 0) {
                try {
                    Gson gson = new Gson();
                    twits = gson.fromJson(result, Twitter.class);
                } catch (IllegalStateException ex) {
                    // just eat the exception
                }
            }
            return twits;
        }

        // convert a JSON authentication object into an Authenticated object
        private TwitterAuthenticated jsonToAuthenticated(String rawAuthorization) {
            TwitterAuthenticated auth = null;
            if (rawAuthorization != null && rawAuthorization.length() > 0) {
                try {
                    Gson gson = new Gson();
                    auth = gson.fromJson(rawAuthorization, TwitterAuthenticated.class);
                } catch (IllegalStateException ex) {
                    // just eat the exception
                }
            }
            return auth;
        }

        private String getResponseBody(HttpRequestBase request) {
            StringBuilder sb = new StringBuilder();
            try {

                DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
                HttpResponse response = httpClient.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                String reason = response.getStatusLine().getReasonPhrase();

                if (statusCode == 200) {

                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();

                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    String line = null;
                    while ((line = bReader.readLine()) != null) {
                        sb.append(line);
                    }
                } else {
                    sb.append(reason);
                }
            } catch (UnsupportedEncodingException ex) {
            } catch (ClientProtocolException ex1) {
            } catch (IOException ex2) {
            }
            return sb.toString();
        }

        private String getTwitterStream(String screenName) {
            String results = null;

            // Step 1: Encode consumer key and secret
            try {
                // URL encode the consumer key and secret
                String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
                String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");

                // Concatenate the encoded consumer key, a colon character, and the
                // encoded consumer secret
                String combined = urlApiKey + ":" + urlApiSecret;

                // Base64 encode the string
                String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

                // Step 2: Obtain a bearer token
                HttpPost httpPost = new HttpPost(TwitterTokenURL);
                httpPost.setHeader("Authorization", "Basic " + base64Encoded);
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
                String rawAuthorization = getResponseBody(httpPost);
                TwitterAuthenticated auth = jsonToAuthenticated(rawAuthorization);

                // Applications should verify that the value associated with the
                // token_type key of the returned object is bearer
                if (auth != null && auth.token_type.equals("bearer")) {

                    // Step 3: Authenticate API requests with bearer token
                    HttpGet httpGet = new HttpGet(TwitterStreamURL + screenName);

                    // construct a normal HTTPS request and include an Authorization
                    // header with the value of Bearer <>
                    httpGet.setHeader("Authorization", "Bearer " + auth.access_token);
                    httpGet.setHeader("Content-Type", "application/json");
                    // update the results with the body of the response
                    results = getResponseBody(httpGet);
                }
            } catch (UnsupportedEncodingException ex) {
            } catch (IllegalStateException ex1) {
            }
            return results;
        }
    }

    private void initialiseBilling() {
        if (mHelper != null) {
            return;
        }

// Create the helper, passing it our context and the public key to verify signatures with
        mHelper = new IabHelper(this,ACP.getPub_key());

// Enable debug logging (for a production application, you should set this to false).
// mHelper.enableDebugLogging(true);

// Start setup. This is asynchronous and the specified listener will be called once setup completes.
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
// Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) {
                    return;
                }

// Something went wrong
                if (!result.isSuccess()) {
                    Log.e("MAIN721", "Problem setting up in-app billing: " + result.getMessage());
                    return;
                }
                mBillingServiceReady = true;

// IAB is fully set up. Now, let's get an inventory of stuff we own.
                mHelper.queryInventoryAsync(iabInventoryListener());
            }
        });
    }

    private IabHelper.QueryInventoryFinishedListener iabInventoryListener() {
        return new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) {
                    return;
                }

                // Something went wrong
                if (!result.isSuccess()) {
                    return;
                }

                // Do your checks here...

                // Do we have the premium upgrade?
                Purchase purchasePro = inventory.getPurchase(ACP.getAd_removal()); // Where G.SKU_PRO is your product ID (eg. permanent.ad_removal)
                ACP.setAd_free((purchasePro != null && ACP.verifyDeveloperPayload(purchasePro)));

                // After checking inventory, re-jig stuff which the user can access now
                // that we've determined what they've purchased
            }

        };
    }

    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) {
                return;
            }

            // Don't complain if cancelling
            if (result.getResponse() == IabHelper.IABHELPER_USER_CANCELLED) {
                return;
            }

            if (!result.isSuccess()) {
                Log.e("MAIN784","Error purchasing: " + result.getMessage());
                if(result.getResponse()==7) {
                    Toast.makeText(getApplicationContext(), getString(R.string.already_own_item), Toast.LENGTH_SHORT).show();
                }
                return;
            }

            if (!ACP.verifyDeveloperPayload(purchase)) {
                Log.e("MAIN 789","Error purchasing. Authenticity verification failed.");
                return;
            }

            // Purchase was success! Update accordingly
            if (purchase.getSku().equals(ACP.getAd_removal())) {
                Toast.makeText(getApplicationContext(), getString(R.string.upgrading), Toast.LENGTH_LONG).show();

                ACP.setAd_free(true);

                if(adView != null && !adShown){
                    if(ACP.isAd_free()) {
                        adView.setVisibility(View.GONE);
                    }else{
                        adView.setAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                super.onAdFailedToLoad(errorCode);
                                adView.setVisibility(View.GONE);
                            }
                        });
                        adView.loadAd(new AdRequest.Builder().build());
                        adShown = true;
                    }
                }

                OverviewFragment f = (OverviewFragment)getSupportFragmentManager().findFragmentByTag("Overview");

                if(f != null){
                    f.refreshList();
                }

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) {
            return;
        }

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode == ConnectionResult.SUCCESS){

        }else{
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }

    }

}
