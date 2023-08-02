import org.omg.CORBA.INTERNAL;

import java.util.*;

public class Database {

    // list of sequences, index is sid
    List<Sequence> Sequences = new ArrayList<>();
    // map event to its support
    Map<Integer, Integer> mapEventToSup = new HashMap<>();
    // database size
    int size = 0;
    // total sequence length
    int tsl = 0;

    public Database(){}

    /**
     * method to add sequence and updata event's support and database size
     * @param sequence added sequence
     */
    public void addSeq(Sequence sequence){
        Sequences.add(sequence);
        for (int event: sequence.events){
            int oldSup = 0;
            if (mapEventToSup.keySet().contains(event)){
                oldSup = mapEventToSup.get(event);
            }
            mapEventToSup.put(event,oldSup+1);
            tsl++;
        }
        size++;
    }

}
