package f1.notice.board;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Antec on 19.01.14.
 */
public class TweetAdapter extends ArrayAdapter<String> {


    private Callbacks mCallbacks = sCallbacks;

    public interface Callbacks{
    }

    private static Callbacks sCallbacks = new Callbacks() {
    };

    Cursor tweets;
    int res;

    public TweetAdapter(Context context, Cursor cursor, int ressource) {
        super(context, R.layout.start_feed_item);

        if(cursor.getCount() == 0){
            MatrixCursor matrix = new MatrixCursor(new String[]{"_id","text","date_created","date_downloaded","§","sub§","section"});
            matrix.addRow(new String[]{"-1","no Tweets available","to get some","refresh","","",""});

            tweets = new MergeCursor(new Cursor[]{cursor, matrix});
        }else{
            tweets = cursor;
        }

        res = ressource;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        View v = convertView;

        if(v == null){
            LayoutInflater vi = (LayoutInflater)ACP.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(res,null);
        }

        tweets.moveToPosition(position);

        String one;
        String two ="";
        final String tweet =tweets.getString(1);

        ImageView icon = (ImageView)v.findViewById(R.id.feed_icon);
        icon.setImageResource(R.drawable.twitter_red);

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
        TextView text = (TextView)v.findViewById(R.id.feed_text);
        text.setText(ss);

        TextView info = (TextView)v.findViewById(R.id.feed_info);
        if(tweets.getString(3).length()>11){
            one = tweets.getString(3).substring(0,10);
            two = tweets.getString(3).substring(11);
        }else{
            one = tweets.getString(3);
        }
        info.setText(one+"\n"+two);

        TextView date = (TextView)v.findViewById(R.id.feed_date);
        if(tweets.getString(2).length()>11){
            one = tweets.getString(2).substring(0,10)+ " "+tweets.getString(2).substring(26,30);
            two = tweets.getString(2).substring(11,19);
        }else{
            one = tweets.getString(2);
        }
        date.setText(one+"\n"+two);


        return v;
    }

    @Override
    public int getCount() {
        return tweets.getCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
