package f1.notice.board;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * An activity representing a single NOT detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link Main}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link OverviewDetailsFragment}.
 */
public class OverviewDetailsActivity extends FragmentActivity implements
            OverviewDetailsFragment.Callbacks,
            OverviewParagraphFragment.Callbacks{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_detail);

        // Show the Up button in the action bar.
        getActionBar().hide();
        getActionBar().setBackgroundDrawable(getResources().getDrawable(android.R.color.holo_red_dark));

        if (savedInstanceState == null) {

            Bundle args = getIntent().getExtras();
            Bundle arguments = new Bundle();
            arguments.putBoolean("twopane",args.getBoolean("twopane"));

            Fragment f = new OverviewDetailsFragment();;

            if(args.containsKey(("TWEET ID"))){
                arguments.putInt("TWEET ID",args.getInt("TWEET ID"));
                f = new OverviewParagraphFragment();
            }

            if(args.containsKey("TWEET chapter")){
                arguments.putString("TWEET chapter",args.getString("TWEET chapter"));

            }

            if(args.containsKey("TWEET subchapter")){
                arguments.putString("TWEET subchapter",args.getString("TWEET subchapter"));
            }

            if(args.containsKey("TWEET section")){
                arguments.putString("TWEET section",args.getString("TWEET section"));
            }

            if(args.containsKey("WHERE")){
                arguments.putString("WHERE",args.getString("WHERE"));
                DBHelper db = new DBHelper();
                Cursor c = db.select("SELECT * FROM "+args.getString("WHERE"));
                c.moveToFirst();
                if(c.getColumnName(3).equals("name")){
                    f = new OverviewParagraphFragment();
                }
            }

            if(args.containsKey("§ ID")){
                arguments.putInt("§ ID",args.getInt("§ ID"));
            }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //

            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            f.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.not_detail_container, f)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, Main.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTweetSelected(String chapter, String sub, String section) {
        Bundle args = new Bundle();
        args.putString("TWEET chapter",chapter);
        args.putString("TWEET subchapter",sub);
        args.putString("TWEET section",section);
        OverviewDetailsFragment fragment = new OverviewDetailsFragment();
        fragment.setArguments(args);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.not_detail_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

}
