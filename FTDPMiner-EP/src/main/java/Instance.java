import java.util.ArrayList;
import java.util.List;

//for each instance of valid pattern t, we record lp (its position of last event),
// u(utility of the instance) and all e- that needed to be extended so that t can be a temporal pattern.
class Instance{
    Integer lp;  //position of last event
    Integer u;   //utility of this instance
    List<Pair> EQUs = new ArrayList<Pair>();
    List<Pair> EQNUs = new ArrayList<Pair>();
    int fp = 0; // the first position of EQNUs, which is related to s-extension
    int lastTime = 0; //improve3 last time in this instance
}
