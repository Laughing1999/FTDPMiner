import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a member of Element. It is for eid, embedded in element
 * @see Element
 */
public class Member {

    /** variables of the class
     * @param eid last event id, from 0 on
     * @param st start time of pattern
     * @param et end time of pattern
     * @param si source intervals
     */
    public int eid ;
    public int st;
    public int et;
    public List<TI> si = new ArrayList<TI>();

    // Default constructor
    public Member(){}


    // Constructor.
    public Member( int ei, int s, int e, TI ti){
        eid = ei;
        st = s;
        et = e;
        si.add(ti);
    }

    //method to add new source interval
    public void addTi(TI ti){
        si.add(ti);
    }

    //method to add new source interval
    public void addTi(List<TI> tis){
        for (TI ti:tis){
            si.add(ti);
        }
    }
}
