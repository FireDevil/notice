package f1.notice.board;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
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
public class ItemFragment extends Fragment {

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ExpandableAdapter mAdapter;

    private ArrayList<ExpandableGroup> groups;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        ExpandableListView el = (ExpandableListView)view.findViewById(R.id.expandableListView);
        groups = new ArrayList<ExpandableGroup>();
        ArrayList<ExpandableItem> items = new ArrayList<ExpandableItem>();
        ArrayList<String> details = new ArrayList<String>();

        String section ="";
        boolean sporting = true;

        if(getArguments().containsKey("section")){
            section = getArguments().getString("section");
            if(section.equals("S")){
                section = "S";
                sporting = true;
            }else{
                section = "T";
                sporting = false;
            }

        }


        DBHelper db = new DBHelper();
        Cursor content = db.select("SELECT * FROM TableOfContent WHERE section='"+section+"'");

        while(content.moveToNext()){
            Cursor sub = db.select("SELECT * FROM Content WHERE paragraph='"+content.getString(2)+"' AND section='"+section+"'");

            items = new ArrayList<ExpandableItem>();
            while (sub.moveToNext()){
                items.add(new ExpandableItem(sub.getInt(0),sub.getString(2)+"."+sub.getString(3),sub.getString(4)));
            }

            String info;

            if(sporting){
                info = getString(R.string.Sporting);
            }else{
                info = getString(R.string.Technical);
            }

            groups.add(new ExpandableGroup(content.getInt(0),content.getString(2),content.getString(3),info,content.getString(4),items));

        }

        mAdapter = new ExpandableAdapter(getActivity(),groups);
        mAdapter.setAct(getActivity());

        if(groups.size()== 0){
            setEmptyText("Sorry, something went wrong! Try a restart or Re-installation");
        }

        el.setAdapter(mAdapter);
        // Set the tAdapter

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

}
