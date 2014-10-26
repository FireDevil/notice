package f1.notice.board;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

;

public class SearchResultActivity extends FragmentActivity implements
        ResultFragment.OnFragmentInteractionListener{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    private CharSequence mTitle;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        String query = getIntent().getStringExtra(SearchManager.QUERY);

        DBHelper db = new DBHelper();
        Cursor recent = db.select("SELECT * FROM RecentSearch WHERE query LIKE '"+query+"'");
        recent.moveToFirst();

        ContentValues values = new ContentValues();
        if(recent.getCount() > 0){
            values.put("count", recent.getInt(2)+1);
            db.update(getString(R.string.tableRecent),values," _id="+recent.getInt(0));
        }else{
            values.put("query",query);
            values.put("count", 1);
            db.insert(getString(R.string.tableRecent), values);
        }

        recent.close();
        db.close();

        if(query.contains(" ")){
            query.replace(' ','%');
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(android.R.color.holo_red_dark));
        getActionBar().setIcon(getResources().getDrawable(android.R.color.transparent));
        getActionBar().setTitle(getString(R.string.resultsFound)+query+"'");

        Bundle args = new Bundle();
        args.putString(SearchManager.QUERY,query);

        ResultFragment fragment = new ResultFragment();
        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.search_container, fragment)
                .commit();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(int id, String table) {

        if(table.equals(getString(R.string.tableContent))){
            Intent detailIntent = new Intent(this, OverviewDetailsActivity.class);
            detailIntent.putExtra("§ ID",id);
            startActivity(detailIntent);
        }

        if(table.equals(getString(R.string.tableTableOfContent))){
            Intent detailIntent = new Intent(this, OverviewDetailsActivity.class);
            detailIntent.putExtra("WHERE",table+" WHERE _id="+id );
            startActivity(detailIntent);
        }

        if(table.equals(getString(R.string.tableTweets))){
            Intent detailIntent = new Intent(this, OverviewDetailsActivity.class);
            detailIntent.putExtra("TWEET ID",id);
            startActivity(detailIntent);
        }
    }

}
