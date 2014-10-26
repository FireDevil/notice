package f1.notice.board;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Antec on 18.01.14.
 */
public class SearchProvider extends ContentProvider {

    private Context ctx;


    @Override
    public boolean onCreate() {

        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        ctx = ACP.getContext();

        MatrixCursor mc = new MatrixCursor(new String[]{BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_TEXT_2,
                SearchManager.SUGGEST_COLUMN_ICON_1,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA,
                SearchManager.SUGGEST_COLUMN_INTENT_ACTION});

        if(selectionArgs[0].length() < 3){
            mc.addRow(new Object[]{"0",ctx.getString(R.string.littleMore),ctx.getString(R.string.getSuggestions),R.drawable.ic_action_search,"hallo","NOTHING"});
            return mc;
        }

        DBHelper db = new DBHelper();
        Cursor c1 = db.select("SELECT * FROM Content  WHERE text LIKE '%"+selectionArgs[0]+"%' ORDER BY clicked DESC");
        Cursor c2 = db.select("SELECT * FROM TableOfContent WHERE name LIKE '%"+selectionArgs[0]+"%' LIMIT 5");
        Cursor recent = db.select("SELECT * FROM RecentSearch  WHERE query LIKE '%"+selectionArgs[0]+"%' ORDER BY count DESC");

        int n = 0;
        String section ="";

        while(recent.moveToNext() && n < 6){

            mc.addRow(new Object[]{""+n,recent.getString(1),ctx.getString(R.string.searchedBefore)+recent.getString(2)+ctx.getString(R.string.time_s),R.drawable.ic_action_search,recent.getString(1),"android.intent.action.ANSWER"});
            n++;
        }

        n = 0;

        while(c2.moveToNext() && n < 6){
                if(c2.getString(1).equals("S")){
                    section = ctx.getString(R.string.Sporting);
                }else{
                    section = ctx.getString(R.string.Technical);
                }


            mc.addRow(new Object[]{""+n,c2.getString(3),c2.getString(2)+" - "+section,R.drawable.ic_drawer," TableOfContent WHERE _id ="+c2.getInt(0),"android.intent.action.VIEW"});
            n++;
        }

        while(c1.moveToNext() && n < 10){
                if(c1.getString(1).equals("S")){
                    section = ctx.getString(R.string.Sporting);
                }else{
                    section = ctx.getString(R.string.Technical);
                }
            mc.addRow(new Object[]{""+n,c1.getString(4),c1.getString(2)+"."+c1.getString(3)+" - "+section,R.drawable.ic_drawer," Content WHERE _id ="+c1.getInt(0),"android.intent.action.VIEW"});
            n++;
        }



        if(mc.getCount() == 0){
            //mc.addRow(new Object[]{""+n,"no results","try a more common wording",R.drawable.ic_action_search,"hallo","NOTHING"});
        }

        recent.close();
        c1.close();
        c2.close();
        db.close();

        return mc;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
