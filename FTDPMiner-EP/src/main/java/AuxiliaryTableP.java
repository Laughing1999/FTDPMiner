import java.util.*;

// the structure is auxiliary table, which is shown in fig.4
public class AuxiliaryTableP {

    Map<Integer,List<Instance>> mapSidToInst = new HashMap<Integer, List<Instance>>();
    Set<Integer> sids = new HashSet<Integer>();

    public AuxiliaryTableP(){}
//    public AuxiliaryTableP(Pattern p){
//        pattern = p;
//    }

//    //return this pattern
//    public Pattern getPattern(){
//        return pattern;
//    }

    //add an instance of pattern t in seq sid, we need information of an instance
    public void add(int sid, int lp, List<Pair> equs, List<Pair> eqnus, int thistime){
        Instance ins = new Instance();
        ins.lp = lp;
        ins.EQUs = equs;
        ins.EQNUs = eqnus;
        ins.lastTime = thistime;
        if (sids.contains(sid)){
            List<Instance> insts = mapSidToInst.get(sid);
            insts.add(ins);
        }else {
            List<Instance> insts = new ArrayList<Instance>();
            insts.add(ins);
            mapSidToInst.put(sid,insts);
            sids.add(sid);
        }
        if (eqnus.size()!=0){
            ins.fp = eqnus.get(0).position;
        }
    }

//    //for the 1-event pattern, update their MDU value
//    public void upMUD(int i, int ru) {
//        mapSidToMdu.put(i,ru);
//    }

//    //get MDU value
//    public int getMUD() {
//        int mdu = 0;
//        for (Integer sid:mapSidToMdu.keySet()){
//            mdu += mapSidToMdu.get(sid);
//        }
//        return mdu;
//    }

//    //get utility value
//    public int getU() {
//        int u = 0;
//        for (Integer sid:mapSidToUtil.keySet()){
//            u += mapSidToUtil.get(sid);
//        }
//        return u;
//    }

    //get frequency value
    public int getS() {
        return sids.size();
    }


}
