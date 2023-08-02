import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * this class represents STIRP
 * @see TIRP
 * @author LFY
 */
public class STIRP {

    int Ssize = 0; // event size == size of setEvents  ???
    List<Integer> setEvents = new ArrayList<Integer>();   // set of events
    Map<NewRelations,TIRP> mapRelasToTirp = new HashMap<NewRelations, TIRP>();  // corresponding TIRP with different vectors of relations of the TIRP

    // default constructor
    public STIRP(){}

    // used for 1-STIRP
    public STIRP(int event){
        setEvents.add(event);
        Ssize++;
        TIRP tirp = new TIRP();
        mapRelasToTirp.put(null,tirp);
    }

    public STIRP(List<Integer> setEs){
        for (Integer e:setEs){
            setEvents.add(e);
            Ssize++;
        }
    }


    /**
     * method to return frequent tirps in a stirp
     * @param minSup given threshold
     * @param size database size for calculate vs
     * @return if null, then no frequent tirp
     */
    public STIRP isFreq(double minSup,int size){
        if (mapRelasToTirp.keySet().size()==0){
            return null;
        }
        STIRP stirp = new STIRP();
        boolean hasFreq = false;
        for (NewRelations relas:mapRelasToTirp.keySet()){
            TIRP tirp = mapRelasToTirp.get(relas);
            if (tirp.getVs(size) >= minSup){
                stirp.mapRelasToTirp.put(relas,tirp);
                hasFreq = true;
            }
        }
        if (!hasFreq){
            return null;
        }
        stirp.setEvents = setEvents;
        stirp.Ssize = Ssize;
        return stirp;
    }


    /**
     * for 1-STIRP, method to add element
     * @param element
     * @param size size of sequence, where the element lies in
     */
    public void addElem(Element element,int size){
        TIRP tirp = mapRelasToTirp.get(null);
        tirp.listElems.add(element);
        tirp.upStatistics(element,size);
    }
}
