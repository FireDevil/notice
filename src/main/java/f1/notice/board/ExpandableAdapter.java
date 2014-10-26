package f1.notice.board;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Antec on 14.01.14.
 */
public class ExpandableAdapter extends BaseExpandableListAdapter {

    private Callbacks mCallbacks = sCallbacks;
    private Context context;
    private ArrayList<ExpandableGroup> groups;
    private ArrayList<ExpandableItem> items;
    Activity act;

    int preColor = 0;

    public interface Callbacks{
        public void onChildSelected(int id);
    }

    private static Callbacks sCallbacks = new Callbacks() {
        @Override
        public void onChildSelected(int id){
        }
    };

    public ExpandableAdapter(Context context, ArrayList<ExpandableGroup> groups) {
        this.context = context;
        this.groups = groups;
    }

    public ExpandableItem getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getChildren().get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View view,
                             ViewGroup parent) {
       ExpandableItem item = getChild(groupPosition, childPosition);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.expandable_item, null);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onChildSelected(groups.get(groupPosition).getChildren().get(childPosition).getId());
            }
        });

        TextView num = (TextView)view.findViewById(R.id.expandableItemNumber);
        num.setText(item.getNumber());
        TextView name = (TextView)view.findViewById(R.id.expandableItemText);
        name.setText(""+item.getName()+"");


        return view;
    }

    public int getChildrenCount(int groupPos) {
        return groups.get(groupPos).getChildren().size();

    }

    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    public int getGroupCount() {
        return groups.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(final int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {
        ExpandableGroup g = (ExpandableGroup)groups.get(groupPosition);


        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.expandable_group,null);
        }

        TextView num = (TextView)view.findViewById(R.id.expandableGroupNum);
        num.setText(g.getNumber());
        TextView name = (TextView)view.findViewById(R.id.expandableGroupName);
        name.setText(g.getName());
        TextView info = (TextView)view.findViewById(R.id.expandableGroupInfo);
        info.setText(g.getInfo());
        TextView site = (TextView)view.findViewById(R.id.expandableGroupSite);
        site.setText(g.getSite());


        return view;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    public void setAct(Activity act) {
        this.act = act;
        mCallbacks = (Callbacks) act;
    }
}
