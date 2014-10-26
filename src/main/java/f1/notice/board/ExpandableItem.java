package f1.notice.board;

import java.util.ArrayList;

/**
 * Created by Antec on 08.01.14.
 */
public class ExpandableItem {

    int id;
    private String Name;
    private String Number;
    ArrayList<String> details = new ArrayList<String>();

    public ExpandableItem(){
    }

    public ExpandableItem(int id,String num, String name){
        setId(id);
        setNumber(num);
        setName(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumber(String num) {
        Number = num;
    }

    public String getNumber(){
        return Number;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public ArrayList<String> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<String> details) {
        this.details = details;
    }

}
