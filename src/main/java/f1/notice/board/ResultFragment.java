package f1.notice.board;

import android.app.Activity;
import android.app.SearchManager;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 */
public class ResultFragment extends Fragment
        implements AbsListView.OnItemClickListener {

    private OnFragmentInteractionListener mListener;
    private AbsListView mListView;
    private SearchAdapter adapter;
    private MergeCursor mc;
    private int[] cursors;

    public ResultFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        mListView = (AbsListView)view.findViewById(R.id.result_list);

        String query ="";

        if(getArguments().containsKey(SearchManager.QUERY)){
            query = getArguments().getString(SearchManager.QUERY);
        }

        query = query.replace(' ','%');

        DBHelper db = new DBHelper();

        Cursor table = db.select("SELECT * FROM TableOfContent WHERE name LIKE '%"+query+"%'");
        Cursor contents = db.select("SELECT * FROM Content WHERE section='S' AND text LIKE '%"+query+"%'");
        Cursor contentt = db.select("SELECT * FROM Content WHERE section='T' AND text LIKE '%"+query+"%'");
        Cursor tweets = db.select("SELECT * FROM Tweets WHERE text LIKE '%"+query+"%'");

        cursors = new int[]{table.getCount(),contents.getCount(),contentt.getCount(),tweets.getCount()};

        mc = new MergeCursor(new Cursor[]{table,contents,contentt,tweets});


        if(mc.getCount() > 0){
            adapter = new SearchAdapter(getActivity(),mc,R.layout.result_item);
            mListView.setOnItemClickListener(this);
            mListView.setAdapter(adapter);
        }else{
            TextView empty = (TextView)view.findViewById(android.R.id.empty);
            empty.setText(getActivity().getString(R.string.noResults));

            db.delete(getString(R.string.tableRecent)," query='"+query+"'");
        }

        return view;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        String table="";

        if(position <cursors[0] ){
            table =getString(R.string.tableTableOfContent);
        }else if(position < cursors[0]+cursors[1]+cursors[2]){
            table = getString(R.string.tableContent);
        }else{
            table = getString(R.string.tableTweets);
        }

        mc.moveToPosition(position);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(mc.getInt(0),table);
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
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(int id, String table);
    }

}
