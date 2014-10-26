package f1.notice.board;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link OverviewFragment.Callbacks}
 * interface.
 */
public class FeedFragment extends Fragment implements
        AbsListView.OnItemClickListener{

    private OnFeedInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    public static AbsListView mListView;
    public static Cursor cursor;
    public static TweetAdapter adapter;

    public static boolean visible() {
        return visible;
    }

    public static boolean visible = false;

    public FeedFragment() {
    }

    public static void refreshList(){
        Log.e("REFRESH", "OK");

        DBHelper db = new DBHelper();
        cursor = db.select("SELECT * FROM Tweets");

        adapter = new TweetAdapter(ACP.getContext(),cursor, R.layout.complete_feed_item);

        mListView.setAdapter(adapter);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content_grid, container, false);

        ArrayList<String> tweets = new ArrayList<String>();
        DBHelper db = new DBHelper();
        Cursor c = db.select("SELECT * FROM Tweets");
        cursor = c;

        TweetAdapter adapter = new TweetAdapter(getActivity(),c, R.layout.complete_feed_item);

        mListView = (AbsListView)rootView.findViewById(R.id.list);

        mListView.setOnItemClickListener(this);

        mListView.setAdapter(adapter);

        visible =true;

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFeedInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        visible =false;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            cursor.moveToPosition(position);
            mListener.onFeedInteraction(cursor.getInt(0));
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }



    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */

    public interface OnFeedInteractionListener {
        public void onFeedInteraction(int id);
    }
}
