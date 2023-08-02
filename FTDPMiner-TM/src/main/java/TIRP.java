import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a temporal pattern, but do not contain relations info.,
 * which is stored in STIRP by map structure
 */
public class TIRP {

    /** variables of the class
     * @param listElems list of elements
     * @param mapSidToMaxdur sequence id to max duration of pattern, in order to calculate sum of duration
     * @param vs vertical support
     * @param shs sum horizontal support
     */
    List<Element> listElems = new ArrayList<>();
    Map<Integer,Integer> mapSidToMaxdur = new HashMap<>();
    private int vs = 0;
    private double shs = 0;
    private int sd = 0;

    // Default constructor
    public TIRP(){}

//    /**
//     * while scanning database to fulfill TIRP, we need to add new element
//     * @param size size of the sequence,where the element lie, used to update shs
//     * @param element element waited to be extended into TIRP
//     */
//    public void addElemDB(int size, Element element){
//        int sid = element.sid;
//
//        // if there has no sequence with this sid before
//        if (mapSidToElems.get(sid)==null){
//            // update mapSidToElems
//            List<Element> a = new ArrayList<Element>();
//            a.add(element);
//            mapSidToElems.put(sid,a);
//            // update statistical values
//            vs++;
//            shs = shs + 1.0/size;
//            mapSidToMaxdur.put(sid,element.et-element.st);
//        }else {
//            // update mapSidToElems
//            List<Element> a = mapSidToElems.get(sid);
//            a.add(element);
//            // update statistical values
//            shs = shs + 1.0/size;
//            if ((element.et-element.st)>mapSidToMaxdur.get(sid)){
//                mapSidToMaxdur.put(sid,element.et-element.st);
//            }
//        }
//    }

    /**
     * method to return vs
     * @param dbs database size
     * @return vertical support
     */
    public double getVs(int dbs){
//        if (mapSidToMaxdur.size()>0){
//            vs = listElems.size();
//        }
//        return ((double) vs/dbs);
        return listElems.size();
    }

    /**
     * method to return mhs
     * @return mean horizontal support
     */
    public double getMhs(){
        if (mapSidToMaxdur.size()>0){
            vs = listElems.size();
        }
        return (shs/vs);
    }

    /**
     * method to return md
     * @return mean duration
     */
    public double getmd() {
        if (mapSidToMaxdur.size()>0){
            vs = listElems.size();
            int sd = 0;
            for (Integer sid:mapSidToMaxdur.keySet()){
                sd = sd+mapSidToMaxdur.get(sid);
            }
        }
        return ((double) sd/vs);
    }

    /**
     * for 1-STIRP, updata 3 statistics according added element and size of sequence, where the element lies in
     * @param element
     * @param size
     */
    public void upStatistics(Element element, int size) {
        List<Object> increStatis = element.getStatistic(size);
        vs = vs + (int) increStatis.get(0);
        shs = shs + (double) increStatis.get(1);
        sd = sd + (int) increStatis.get(2);
    }

    /**
     * for construct, add new member in sid into TIRP and update Statistics
     * @param sid
     * @param memberZ
     * @param size size of sequence sid
     */
    public void addMember(int sid, Member memberZ, int size) {
        // add memberZ
        // there has existed element in TIRP
        if (listElems.size()-1>=0){
            int lsid = listElems.get(listElems.size()-1).sid;// last sid
            // add into last element
            if (lsid==sid){
                listElems.get(listElems.size()-1).listMems.add(memberZ);
            }
            // add new last element
            else if (lsid<sid){
                List<Member> members = new ArrayList<>();
                members.add(memberZ);
                Element element = new Element(sid,members);
                listElems.add(element);
            }
            // test error
            else {
                System.out.println("test1-error, sid not sorted!");
            }
        }
        // there hasn't existed element in TIRP
        else {
            List<Member> members = new ArrayList<>();
            members.add(memberZ);
            Element element = new Element(sid,members);
            listElems.add(element);
        }

        // upStatistics
        if (mapSidToMaxdur.keySet().contains(sid)){
            if (memberZ.et-memberZ.st>mapSidToMaxdur.get(sid)){
                mapSidToMaxdur.put(sid, memberZ.et-memberZ.st);
            }
        }
        else {
            mapSidToMaxdur.put(sid, memberZ.et-memberZ.st);
        }
        shs = shs + 1.0/size;
    }
}
