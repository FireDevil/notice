package f1.notice.board;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Antec on 28.01.14.
 */
public class OverviewParagraphFragment extends Fragment {

    private Callbacks mCallbacks = sCallbacks;

    public interface Callbacks{
        public void onTweetSelected(String chapter, String sub, String section);
    }

    private static Callbacks sCallbacks = new Callbacks() {
        @Override
        public void onTweetSelected(String chapter, String sub, String section) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OverviewParagraphFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    KenBurnsView kenburn;
    ImageView image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.detail_layout, container, false);
        kenburn = (KenBurnsView) rootView.findViewById(R.id.kenburn);
        image = (ImageView)rootView.findViewById(R.id.imageView);

        Bundle args = getArguments();

        int[] images = new int[]{R.drawable.ferrari,
                R.drawable.india,
                R.drawable.sauber,
                R.drawable.mclaren,
                R.drawable.williams,
                R.drawable.lotus,
                R.drawable.joint,
                R.drawable.mercedes,
                R.drawable.toro,
                R.drawable.williams_2};

        int imageRand = (int) (Math.random()*images.length);
        int imageInt = images[imageRand];

        String where="";

        if(args.containsKey("WHERE")){
            if(!args.getString("WHERE").equals("")){

                // suggestions
                DBHelper db = new DBHelper();
                Cursor c = db.select("SELECT * FROM "+getArguments().getString("WHERE"));
                c.moveToFirst();

                kenburn.setResourceIds(imageInt,imageInt);
                image.setVisibility(View.GONE);

                if(c.getColumnName(3).equals("name")){

                    TextView t = (TextView)rootView.findViewById(R.id.detail_num);
                    t.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    t.setText(c.getString(2));

                    t = (TextView)rootView.findViewById(R.id.detail_heading);
                    t.setText(c.getString(3));

                    String section;

                    Cursor sub = db.select("SELECT * FROM Content WHERE paragraph='"+c.getString(2)+"' AND section='"+c.getString(1)+"'");

                    if(c.getString(1).equals("S")){
                        section = getString(R.string.Sporting);
                    }else{
                        section = getString(R.string.Technical);
                    }

                    t = (TextView)rootView.findViewById(R.id.detail_section);
                    t.setText(section);

                    t = (TextView)rootView.findViewById(R.id.detail_main_text);

                    LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.detail_layout);
                    int index = ll.indexOfChild(t);

                    while (sub.moveToNext()){

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        final String para = sub.getString(2);
                        final String paraSub = sub.getString(3);
                        final String paraSec = sub.getString(1);

                        SpannableString ss = new SpannableString(para+"."+paraSub);

                        ss.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(View textView) {
                                mCallbacks.onTweetSelected(para,paraSub,paraSec);
                            }
                        };
                        ss.setSpan(clickableSpan, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        TextView tn = new TextView(getActivity());
                        tn.setLinkTextColor(getActivity().getResources().getColor(android.R.color.holo_red_dark));
                        tn.setTextSize(18);
                        tn.setText(ss);
                        tn.setMovementMethod(LinkMovementMethod.getInstance());
                        tn.setLayoutParams(lp);
                        tn.setPadding(8,12,8,0);
                        tn.setTypeface(null,1);

                        TextView tv = new TextView(getActivity());
                        tv.setTextSize(18);
                        tv.setText(sub.getString(4));
                        tv.setLayoutParams(lp);
                        tv.setPadding(8, 5, 8, 30);

                        ll.addView(tn,index);
                        index++;
                        ll.addView(tv,index);
                        index++;
                    }


                    t = (TextView)rootView.findViewById(R.id.detail_info1);
                    t.setText(getActivity().getString(R.string.findOriginals) + c.getString(4));
                }

                if(c.getColumnName(4).equals("name")){

                    Cursor top = db.select("SELECT * FROM TableOfContent WHERE paragraph='"+c.getString(2)+"'");
                    top.moveToFirst();

                    ContentValues values = new ContentValues();
                    values.put("clicked",c.getInt(7)+1);

                    db.update(getString(R.string.tableContent),values," _id="+c.getInt(0));

                    String section;

                    if(c.getString(1).equals("S")){
                        section = getString(R.string.Sporting);
                    }else{
                        section = getString(R.string.Technical);
                    }

                    TextView tv = (TextView)rootView.findViewById(R.id.detail_num);
                    tv.setText(c.getString(2)+"."+c.getString(3));
                    tv.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

                    tv = (TextView)rootView.findViewById(R.id.detail_heading);
                    tv.setText(c.getString(4));

                    tv = (TextView)rootView.findViewById(R.id.detail_subheading);
                    tv.setText(top.getString(2)+"  "+top.getString(3));

                    tv = (TextView)rootView.findViewById(R.id.detail_section);
                    tv.setText(section);

                    boolean changed = c.getInt(6) > 0;

                    tv = (TextView)rootView.findViewById(R.id.detail_info1);
                    tv.setText(getActivity().getString(R.string.recentlyChanged)+changed);

                    tv = (TextView)rootView.findViewById(R.id.detail_info2);
                    tv.setText(getActivity().getString(R.string.didSelect)+(c.getInt(7)+1)+getActivity().getString(R.string.time_s));

                    top.close();

                }
                c.close();
                db.close();
            }
        }

        if(args.containsKey(("TWEET ID"))){
            //Tweets

            DBHelper db = new DBHelper();
            Cursor c = db.select("SELECT * FROM Tweets WHERE _id="+args.getInt("TWEET ID"));
            final int tweet = args.getInt("TWEET ID");
            c.moveToFirst();

            image.setImageResource(R.drawable.twitter_red);
            kenburn.setVisibility(View.GONE);

            TextView t = (TextView)rootView.findViewById(R.id.detail_heading);
            t.setText(c.getString(2).substring(0, 19));

            t = (TextView)rootView.findViewById(R.id.detail_section);
            t.setText(c.getString(1));

            t = (TextView)rootView.findViewById(R.id.detail_info1);
            t.setText(getActivity().getString(R.string.downloaded)+c.getString(3));

            t = (TextView)rootView.findViewById(R.id.copyright);
            t.setText("");

            SpannableString ss = new SpannableString(c.getString(4)+"."+c.getString(5)+"-"+c.getString(6));

            if(ss.length()>3){
                ss.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        DBHelper db = new DBHelper();
                        Cursor cur = db.select("SELECT * FROM Tweets WHERE _id="+tweet);
                        cur.moveToFirst();
                        mCallbacks.onTweetSelected(cur.getString(4),cur.getString(5),cur.getString(6));
                    }
                };
                ss.setSpan(clickableSpan, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                TextView text = (TextView)rootView.findViewById(R.id.detail_num);
                text.setLinkTextColor(getActivity().getResources().getColor(android.R.color.holo_red_dark));
                text.setText(ss);
                text.setMovementMethod(LinkMovementMethod.getInstance());
            }


            c.close();
            db.close();

        }

        if(args.containsKey("TWEET chapter")){

            kenburn.setResourceIds(imageInt,imageInt);
            image.setVisibility(View.GONE);

            String chapter = args.getString("TWEET chapter");
            String subchapter = args.getString("TWEET subchapter");
            String section = args.getString("TWEET section");

            if(!chapter.equals("") || !subchapter.equals("") || !section.equals("")){

                DBHelper db = new DBHelper();
                Cursor c = db.select("SELECT * FROM Content WHERE paragraph=" + chapter + " AND sub=" + subchapter+" AND section='"+section+"'");
                c.moveToFirst();
                Cursor top = db.select("SELECT * FROM TableOfContent WHERE paragraph='"+c.getString(2)+"'");
                top.moveToFirst();

                ContentValues values = new ContentValues();
                values.put("clicked",c.getInt(7)+1);

                db.update(getActivity().getString(R.string.tableContent),values," _id="+c.getInt(0));

                if(c.getString(1).equals("S")){
                    section = getString(R.string.Sporting);
                }else{
                    section = getString(R.string.Technical);
                }

                TextView tv = (TextView)rootView.findViewById(R.id.detail_num);
                tv.setText(c.getString(2)+"."+c.getString(3));
                tv.setTextColor(getResources().getColor(android.R.color.holo_red_dark));


                tv = (TextView)rootView.findViewById(R.id.detail_heading);
                tv.setText(c.getString(4));

                tv = (TextView)rootView.findViewById(R.id.detail_subheading);
                tv.setText(top.getString(2)+".  "+top.getString(3));

                tv = (TextView)rootView.findViewById(R.id.detail_section);
                tv.setText(section);

                boolean changed;
                if(c.getInt(6) > 0){
                    changed = true;
                }else{
                    changed = false;
                }

                tv = (TextView)rootView.findViewById(R.id.detail_info1);
                tv.setText(getActivity().getString(R.string.recentlyChanged)+changed);

                tv = (TextView)rootView.findViewById(R.id.detail_info2);
                tv.setText(R.string.didSelect+(c.getInt(7)+1)+R.string.time_s);

                c.close();
                top.close();
                db.close();

            }

            if(!chapter.equals("") && !section.equals("") && subchapter.equals("")){

                DBHelper db = new DBHelper();
                Cursor c = db.select("SELECT * FROM TableOfContent WHERE paragraph='"+chapter+"'");
                c.moveToFirst();

                if(c.getColumnName(3).equals("name")){

                    TextView t = (TextView)rootView.findViewById(R.id.detail_num);
                    t.setText(c.getString(2));
                    t.setTextColor(getResources().getColor(android.R.color.holo_red_dark));


                    t = (TextView)rootView.findViewById(R.id.detail_heading);
                    t.setText(c.getString(3));

                    Cursor sub = db.select("SELECT * FROM Content WHERE paragraph='"+c.getString(2)+"' AND section='"+c.getString(1)+"'");

                    if(c.getString(1).equals("S")){
                        section = getString(R.string.Sporting);
                    }else{
                        section = getString(R.string.Technical);
                    }

                    t = (TextView)rootView.findViewById(R.id.detail_section);
                    t.setText(section);

                    t = (TextView)rootView.findViewById(R.id.detail_main_text);

                    LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.detail_layout);
                    int index = ll.indexOfChild(t);

                    while (sub.moveToNext()){

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        final String para = sub.getString(2);
                        final String paraSub = sub.getString(3);
                        final String paraSec = sub.getString(1);

                        SpannableString ss = new SpannableString(para+"."+paraSub);

                        ss.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(View textView) {
                                mCallbacks.onTweetSelected(para,paraSub,paraSec);
                            }
                        };
                        ss.setSpan(clickableSpan, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        TextView tn = new TextView(getActivity());
                        tn.setLinkTextColor(getActivity().getResources().getColor(android.R.color.holo_red_dark));
                        tn.setTextSize(18);
                        tn.setText(ss);
                        tn.setMovementMethod(LinkMovementMethod.getInstance());
                        tn.setLayoutParams(lp);
                        tn.setPadding(8,12,8,0);
                        tn.setTypeface(null,1);


                        TextView tv = new TextView(getActivity());
                        tv.setTextSize(18);
                        tv.setText(sub.getString(4));
                        tv.setLayoutParams(lp);
                        tv.setPadding(8, 5, 8, 30);

                        ll.addView(tn,index);
                        index++;
                        ll.addView(tv,index);
                        index++;
                    }


                    t = (TextView)rootView.findViewById(R.id.detail_info1);
                    t.setText(R.string.findOriginals+c.getString(4));
                }

                c.close();
                db.close();
            }

        }

        if(args.containsKey("§ ID")){

            kenburn.setResourceIds(imageInt,imageInt);
            image.setVisibility(View.GONE);

            int id = args.getInt("§ ID");

            DBHelper db = new DBHelper();
            Cursor c = db.select("SELECT * FROM Content WHERE _id="+id);
            c.moveToFirst();
            Cursor top = db.select("SELECT * FROM TableOfContent WHERE paragraph='"+c.getString(2)+"'");
            top.moveToFirst();

            ContentValues values = new ContentValues();
            values.put("clicked",c.getInt(7)+1);

            db.update(getActivity().getString(R.string.tableContent),values," _id="+c.getInt(0));

            String section;

            if(c.getString(1).equals("S")){
                section = getString(R.string.Sporting);
            }else{
                section = getString(R.string.Technical);
            }

            TextView tv = (TextView)rootView.findViewById(R.id.detail_num);
            tv.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            tv.setText(c.getString(2)+"."+c.getString(3));

            tv = (TextView)rootView.findViewById(R.id.detail_heading);
            tv.setText(c.getString(4));

            tv = (TextView)rootView.findViewById(R.id.detail_subheading);
            tv.setText(top.getString(2)+".  "+top.getString(3));

            tv = (TextView)rootView.findViewById(R.id.detail_section);
            tv.setText(section);

            tv = (TextView)rootView.findViewById(R.id.detail_main_text);
            tv.setText(c.getString(5));

            boolean changed;
            if(c.getInt(6) > 0){
                changed = true;
            }else{
                changed = false;
            }

            tv = (TextView)rootView.findViewById(R.id.detail_info1);
            tv.setText(getActivity().getString(R.string.recentlyChanged) +changed);

            tv = (TextView)rootView.findViewById(R.id.detail_info2);
            tv.setText(R.string.didSelect+(c.getInt(7)+1)+R.string.time_s);

        }

        return rootView;
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

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sCallbacks;
    }
}
