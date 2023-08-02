import java.util.ArrayList;
import java.util.List;

/**
 * the class is used to store patterns that we are going to mine
 */
public class Pattern {

    // events with order
    List<Integer> events = new ArrayList<>();
    // vector of relations
    List<Integer> relas = new ArrayList<>();
    // list of parameters of relations
    List<List<Integer>> relasParas = new ArrayList<>();   // improve1
    // count of events
    int size;

    // default constructor
    public Pattern(){}

    public Pattern(List<Integer> es,List<Integer> rs){
        events = es;
        relas = rs;
        size = es.size();
    }

//    public void addEvent(int event,){
//        events.add(size);
//        addRelas(event)
//    }

}
