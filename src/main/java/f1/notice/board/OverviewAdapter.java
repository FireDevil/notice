package f1.notice.board;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by Antec on 28.02.14.
 */
public class OverviewAdapter extends ArrayAdapter<String> {

    Cursor tweets;
    Cursor recents;
    Cursor changes;
    int res;



    public OverviewAdapter(Context context,Cursor twitter, Cursor search, Cursor change, int resource) {
        super(context, R.layout.result_item);

        tweets = twitter;
        recents = search;
        changes = change;
        res = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        View v = convertView;

        return v;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}