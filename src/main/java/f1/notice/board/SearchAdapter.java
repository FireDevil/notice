package f1.notice.board;

import android.content.Context;
import android.database.MergeCursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Antec on 30.01.14.
 */
public class SearchAdapter extends ArrayAdapter<String> {

    MergeCursor c;
    int res;


    public SearchAdapter(Context context,MergeCursor cursor, int resource) {
        super(context, R.layout.result_item);

        c = cursor;
        res = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        View v = convertView;
        String section;
        if(v == null){
            LayoutInflater vi = (LayoutInflater)ACP.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(res,null);
        }

        c.moveToPosition(position);


        if(c.getString(c.getColumnIndex("section")).equals("S")){
            section = "Sporting Regulations";
        }else{
            section = "Technical Regulations";
        }

        TextView num = (TextView)v.findViewById(R.id.resultNum);
        TextView name = (TextView)v.findViewById(R.id.resultName);
        TextView info = (TextView)v.findViewById(R.id.resultInfo);
        TextView site = (TextView)v.findViewById(R.id.resultSite);

        if(c.getColumnIndex("name") == 3){
            num.setText(c.getString(c.getColumnIndex("section")));
            name.setText(c.getString(3));
            info.setText(c.getString(2)+". - "+section);
            site.setVisibility(View.GONE);
        }
        if(c.getColumnIndex("name")== 4){
            num.setText(c.getString(c.getColumnIndex("section")));
            name.setText(c.getString(4));
            info.setText(c.getString(2)+"."+c.getString(3)+" - "+section);
            site.setVisibility(View.GONE);
        }

        if(c.getColumnIndex("text")==1){
            num.setText("TW");
            name.setText(c.getString(1));
            info.setText("Tweet");
            site.setText(c.getString(2).substring(0,10));
        }

        return v;
    }

    @Override
    public int getCount() {
        return c.getCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
