import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//this structure is used to represent seq version1(like example after definition3)
public class SeqVersion1 {
    //the size is the size of time set, N represent e-,P represent e+
    List<List<Uei>> seqVersionN = new ArrayList<List<Uei>>();
    List<List<Uei>> seqVersionP = new ArrayList<List<Uei>>();
    //utility of each set
    List<Integer> particialSU = new ArrayList<Integer>();
    //the key is (e+,position), value is corresponding position of e_
    Map<Pair,Integer> mapPairToEn = new HashMap<Pair, Integer>();

//    //define a class pair to represent " e+ in a position "
//    class Pair{
//        int event;
//        int position;
//    }

    public SeqVersion1(){}

    public void mapAdd(int e,int p1,int p2){
        Pair pa = new Pair();
        pa.event = e;
        pa.position = p1;
        mapPairToEn.put(pa,p2);
    }

    // given event e+ in position p1, return position of corresponding e-
    public int mapGet(int e, int p1){
        Pair pa = new Pair();
        pa.event = e;
        pa.position = p1;
        return  mapPairToEn.get(pa);
    }

    // given event e+ with e- in position fy, return next position of e+
    public int epGet(int e,int fy){
        for (int i=fy;i<seqVersionP.size();i++){
            if (seqVersionP.get(i).contains(e)){
                return i;
            }
        }
        return 0;
    }

    public int aepGet(int event, int j, List<Integer> events, int ep){
        List<Uei> uSetP = seqVersionP.get(j);
        if (uSetP.size()==1){
            return 0;
        }else {
            for (int s=ep;s<events.size();s++){
                int ae = events.get(s);
                if (uSetP.contains(ae)){
                    return s;
                }
            }
        }
        return 0;
    }
}
