package f1.notice.board;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

/**
 * A list fragment representing a list of NOT. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link OverviewDetailsFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class OverviewFragment extends Fragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onFeedInteraction(int id);
        public void onChildSelected(int id);
        public void onRecentSelected(String query);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sCallbacks = new Callbacks() {
        @Override
        public void onFeedInteraction(int id) {
        }

        @Override
        public void onChildSelected(int id) {
        }

        @Override
        public void onRecentSelected(String query) {
        }
    };

    private static Callbacks mCallbacks = sCallbacks;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OverviewFragment() {
    }

    boolean twoPane;
    public static boolean visible() {
        return visible;
    }
    static boolean visible = false;
    static boolean adShown = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

    }

    public static void refreshList(){
       DBHelper db = new DBHelper();
       setUpdates(db);
       setTweets(db);
       setRecents(db);
       db.close();

        if(adView != null && !adShown){
            if(ACP.isAd_free()) {
                adView.setVisibility(View.GONE);
                ad_padding.setVisibility(View.GONE);
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
    }

    static View rootView;
    static ViewGroup container;
    static LayoutInflater inflater;
    static Activity act;
    static AdView adView;
    static TableRow ad_padding;
    private KenBurnsView backgroundImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        this.container = container;
        this.inflater = inflater;
        act = getActivity();

        getActivity().getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90CC0000")));

        rootView = inflater.inflate(R.layout.main_list,container,false);
        backgroundImage = (KenBurnsView)rootView.findViewById(R.id.main_background);
        ImageView fade = (ImageView)rootView.findViewById(R.id.fading_edge);

        int[] images = new int[]{R.drawable.main_1,
                R.drawable.main_2,
                R.drawable.main_3,
                R.drawable.main_4,
                R.drawable.main_5,
                R.drawable.main_6,
                R.drawable.main_7,
                R.drawable.main_8,
                R.drawable.main_9,
                R.drawable.main_10,
                R.drawable.main_11,
                R.drawable.main_12,
                R.drawable.main_13,
                R.drawable.main_14,
                R.drawable.main_15,
                R.drawable.main_16,
                R.drawable.main_17,
                R.drawable.main_18,
                R.drawable.main_19,
                R.drawable.main_20,
                R.drawable.main_21,
                R.drawable.main_22,
                R.drawable.main_23,
                R.drawable.main_24,
                R.drawable.main_25,
                R.drawable.main_26,
                R.drawable.main_27,
                R.drawable.main_28,
                R.drawable.main_29,
                R.drawable.main_30,
                R.drawable.main_31,
                R.drawable.main_32,
                R.drawable.main_33};

        int imageRand = (int) (Math.random()*images.length);
        int firstImageInt = images[imageRand];
        int[] resourceIds = new int[images.length];
        int counter = 0;

        for(int i = imageRand; i< images.length;i++){
            if(i == imageRand-1){
                break;
            }

            resourceIds[counter] = images[i];
            counter++;

            if(i == images.length-1){
                i = 0;
            }

            if(counter==5){
                break;
            }
        }

        backgroundImage.setResourceIds(resourceIds);

        twoPane = getArguments().getBoolean(getString(R.string.app_name),false);


        if(!twoPane){
            adView = (AdView)rootView.findViewById(R.id.adView);
            ad_padding = (TableRow)rootView.findViewById(R.id.ad_padding);
        }else{

            ViewGroup.LayoutParams params = backgroundImage.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            backgroundImage.setLayoutParams(params);

        }

        DBHelper db = new DBHelper();

        setUpdates(db);
        setTweets(db);
        setRecents(db);

        db.close();

        visible = true;

        return rootView;
    }

    public static void setUpdates(DBHelper db){
        Cursor changed = db.select("SELECT * FROM Content WHERE changed=1");
        changed.moveToFirst();

        LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.top);
        int children = ll.getChildCount();

        if(children > 2){
            ll.removeViews(2, children-2);
        }

        ImageView image = (ImageView)ll.findViewById(R.id.top_image);
        image.setImageResource(R.drawable.refresh_red);

        TextView top_text = (TextView)ll.findViewById(R.id.top_text);
        top_text.setText(act.getString(R.string.updates));

        if(changed.getCount() == 0){
            return;
        }
        int n = 0;
        int randomPosition;
        ArrayList<Integer> positions = new ArrayList<Integer>();

        while(n < 5){
            randomPosition = (int) (Math.random()*changed.getCount());

            if(positions.contains(randomPosition)){
                continue;
            }

            changed.moveToPosition(randomPosition);
            LinearLayout element = new LinearLayout(act);
            element = (LinearLayout)inflater.inflate(R.layout.main_elements,container,false);

            final int id = changed.getInt(0);

            element.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallbacks.onChildSelected(id);
                }
            });


            TextView name = (TextView)element.findViewById(R.id.main_element_Name);
            name.setText(changed.getString(4));

            TextView info = (TextView)element.findViewById(R.id.main_element_Info);
            info.setText(changed.getString(2)+"."+changed.getString(3));

            TextView site = (TextView)element.findViewById(R.id.main_element_Site);
            site.setText(convertSection(changed.getString(1)));

            if(n == changed.getCount()-1 || n == 4){
                LinearLayout divider = (LinearLayout)element.findViewById(R.id.main_element_divider);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0,0,0,0);
                divider.setLayoutParams(lp);
            }


            ll.addView(element);
            n++;
            positions.add(randomPosition);
        }
        changed.close();
    }

    public static void setTweets(DBHelper db){
        Cursor feed = db.select("SELECT * FROM Tweets");

        LinearLayout lm = (LinearLayout)rootView.findViewById(R.id.middle);
        int children = lm.getChildCount();

        if(children > 2){
            lm.removeViews(2, children-2);
        }

        ImageView image = (ImageView)lm.findViewById(R.id.middle_image);
        image.setImageResource(R.drawable.twitter_red);

        TextView middle_text = (TextView)lm.findViewById(R.id.middle_text);
        middle_text.setText(act.getString(R.string.tableTweets));

        if(feed.getCount() == 0) {
            return;
        }

        int n = 0;


        while(feed.moveToNext()){

            LinearLayout element = new LinearLayout(act);
            element = (LinearLayout)inflater.inflate(R.layout.main_elements,container,false);

            final int id = feed.getInt(0);

            element.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallbacks.onFeedInteraction(id);
                }
            });


            String tweet = feed.getString(1);
            int start =-1;
            int end = 0;

            for( int c = 0; c < tweet.length();c++){



                if(tweet.charAt(c) == '§'){
                    start = c+1;
                    continue;
                }

                if(tweet.charAt(c) == ' ' && start >= 0){
                    end = c;
                    break;
                }

            }

            if(start < 0)start=0;

            SpannableString ss = new SpannableString(tweet);
            ss.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            TextView name = (TextView)element.findViewById(R.id.main_element_Name);
            name.setText(ss);

            TextView info = (TextView)element.findViewById(R.id.main_element_Info);
            info.setText(feed.getString(2).substring(0,16));

            TextView site = (TextView)element.findViewById(R.id.main_element_Site);
            site.setText("");

            if(n == feed.getCount()-1 || n == 2){
                LinearLayout divider = (LinearLayout)element.findViewById(R.id.main_element_divider);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0,0,0,0);
                divider.setLayoutParams(lp);
            }


            lm.addView(element);
            n++;

            if(n==3)break;
        }
        feed.close();
    }

    public static void setRecents(DBHelper db){
        Cursor recent = db.select("SELECT * FROM RecentSearch ORDER BY count DESC");

        LinearLayout lm = (LinearLayout)rootView.findViewById(R.id.bottom);

        int children = lm.getChildCount();

        if(children > 2){
            lm.removeViews(2, children-2);
        }

        ImageView image = (ImageView)lm.findViewById(R.id.bottom_image);
        image.setImageResource(R.drawable.search_red);

        TextView middle_text = (TextView)lm.findViewById(R.id.bottom_text);
        middle_text.setText(act.getString(R.string.search));

        int n = 0;
        recent.moveToFirst();

        while(n<5){

            LinearLayout element = new LinearLayout(act);
            element = (LinearLayout)inflater.inflate(R.layout.main_elements,container,false);
            TextView name = (TextView)element.findViewById(R.id.main_element_Name);
            TextView info = (TextView)element.findViewById(R.id.main_element_Info);
            TextView site = (TextView)element.findViewById(R.id.main_element_Site);

            String count;
            String search ="";
            boolean zero = false;

            if(recent.getCount() == 0){
                name.setText(act.getString(R.string.noResultsYet));
                count = act.getString(R.string.wellStructured);
                site.setVisibility(View.GONE);
                zero = true;

            }else{
                final String query = recent.getString(1);

                element.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallbacks.onRecentSelected(query);
                    }
                });

                name.setText(query);

                if(recent.getInt(2) > 1){
                    count = act.getString(R.string.searched)+recent.getInt(2)+act.getString(R.string.time_s);
                }else{
                    count = act.getString(R.string.searchedOnce);
                }
            }


            info.setText(count);
            site.setText("");

            if(n == recent.getCount()-1 || n == 4 || recent.getCount() == 0){
                LinearLayout divider = (LinearLayout)element.findViewById(R.id.main_element_divider);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0,0,0,0);
                divider.setLayoutParams(lp);
            }


            lm.addView(element);
            n++;

            if(zero)break;

            if(recent.isLast()){
                break;
            }else{
                recent.moveToNext();
            }

        }
        recent.close();
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }



        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(android.R.color.holo_red_dark));
        getActivity().setTheme(R.style.AppTheme);
        visible=false;
        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sCallbacks;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
            inflater.inflate(R.menu.feed, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    public static String convertSection(String section){
        if(section.equals("S")){
            return act.getString(R.string.Sporting);
        }else{
            return act.getString(R.string.Technical);
        }
    }

    @Override
    public void onPause() {
        if(adView!= null){
            adView.pause();
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adView != null){
            adView.resume();
        }

    }

    @Override
    public void onDestroy() {
        if(adView!=null){
            adView.destroy();
        }
        super.onDestroy();
    }



}
