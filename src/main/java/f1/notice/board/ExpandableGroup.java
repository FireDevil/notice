package f1.notice.board;

import java.util.ArrayList;

/**
 * Created by Antec on 14.01.14.
 */
public class ExpandableGroup {

    String mNumber;
    String mName;
    String mSite;
    int id;

    String mInfo;
    ArrayList<ExpandableItem> mList;

    public ExpandableGroup(){
    }

    public ExpandableGroup(int id, String number, String name,String info, String site, ArrayList<ExpandableItem> list){
        setId(id);
        setNumber(number);
        setName(name);
        setInfo(info);
        setSite(site);
        setList(list);

    }

    @Override
    public String toString(){

        StringBuilder builder = new StringBuilder();
        builder.append(mNumber);
        builder.append("\n");
        builder.append(mName);
        builder.append("\n");
        builder.append(mInfo);
        builder.append("\n");
        builder.append(mSite);

        for(ExpandableItem i : mList){
            builder.append("\n");
            builder.append(i.getName());
        }

        return "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getInfo() {
        return mInfo;
    }

    public void setInfo(String mInfo) {
        this.mInfo = mInfo;
    }


    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getSite() {
        return mSite;
    }

    public void setSite(String mSite) {
        this.mSite = mSite;
    }

    public ArrayList<ExpandableItem> getChildren() {
        return mList;
    }

    public void setList(ArrayList<ExpandableItem> mList) {
        this.mList = mList;
    }

}
