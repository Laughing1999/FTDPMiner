import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an Element of a vertical representation of patterns in vertTIRP as used by the VertTIRP algorithm.
 * Generaly, elememt contain a info with the same sid but different eids
 */
public class Element{

    /** variables of the class
     * @param sid sequence id
     * @param listMems members in sequence id sid
     */
    public int sid;
    public List<Member> listMems = new ArrayList<>();

    // Default constructor
    public Element(){}

    public Element(int sid,List<Member> mems){
        this.sid = sid;
        listMems = mems;
    }

    /**
     * given sequence size, return 3 statatics
     * @param size sequence size
     * @return 3 statatics, vs, shs, d
     */
    public List<Object> getStatistic(int size){
        List<Object> res = new ArrayList<>();
        int addVs = 1;
        res.add(addVs);
        double addShs = (double) listMems.size() /size;
        res.add(addShs);
        int addD = getMaxDur(listMems);
        res.add(addD);
        return res;
    }

    /**
     * for 1-STIRP
     * @param listMems
     * @return maximum duration in element
     */
    private int getMaxDur(List<Member> listMems) {
        int res = 0;
        for (int i=0;i<listMems.size();i++){
            int dura;
            Member member = listMems.get(i);
            dura = member.et-member.st;
            if (dura>res){
                res = dura;
            }
        }
        return res;
    }

}
