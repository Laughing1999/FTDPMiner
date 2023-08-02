import java.util.*;

/**
 * this class represents sequence used in vertTIRP (with eid)
 * @author LFY
 */
public class Sequence {

    //int sid; // sequence id
    List<TI> tis = new ArrayList<TI>(); // tis is sorted by start time, so the index ti in tis is its eid
    Set<Integer> events = new HashSet<>(); // set of different events
    int size = 0;  // count of events, if C A B C, then size is 4

    // default constructor
    public Sequence(){}

    /**
     * constructor with sid and first event
     * @param st start time of the first event
     * @param et end time of the first event
     * @param sym symbol of the first event
     */
    public Sequence(int st, int et, int sym){
        TI ti = new TI(st,et,sym);
        tis.add(ti);
        size++;
        events.add(sym);
    }

    /**
     * method to add event into an existing sequence
     * @param st
     * @param et
     * @param sym
     */
    public void addTi(int st, int et, int sym){
        // check if satisfying start time order
        int oldSt;
        if (tis.size()>=1){
            oldSt = tis.get(size-1).st;
        }else {
            oldSt = 0;
        }
        int newSt = st;
        if (newSt < oldSt){
            System.out.println("Error! Event order in sequence is not sorted by start time");
            return;
        }
        // update
        TI ti = new TI(st,et,sym);
        tis.add(ti);
        size++;
        events.add(sym);
    }

    /**
     * method to add event into an existing sequence
     * @param ti event waited to be extended
     */
    public void addTi(TI ti){
        // check if satisfying start time order
        int oldSt;
        if (tis.size()>=1){
            oldSt = tis.get(size-1).st;
        }else {
            oldSt = 0;
        }
        int newSt = ti.st;
        if (newSt < oldSt){
            System.out.println("Error! Event order in sequence is not sorted by start time");
            return;
        }
        tis.add(ti);
        size++;
        events.add(ti.sym);
    }

    /**
     * for 1-STIRP, scan ti in sequence one by one to return map, which is used to construct TIRP
     * @return res: events to members
     */
    public Map<Integer,List<Member>> scanSeq(){
        Map<Integer,List<Member>> res = new HashMap<>();
        // scan ti one by one
        for (int i=0;i<size;i++){
             TI ti = tis.get(i);
             int sym = ti.sym;
             Member member = new Member(i, ti.st, ti.et, ti);
             // if res has stored info about sym
             if (res.keySet().contains(sym)){
                 res.get(sym).add(member);
             }
             // if res has not stored info about sym
             else {
                 List<Member> mems = new ArrayList<>();
                 res.put(sym,mems);
                 mems.add(member);
             }
         }
        return res;
    }
}
