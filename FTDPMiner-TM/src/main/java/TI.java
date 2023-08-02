
/*
 the class is used to store a time interval event
 */
public class TI {

    /** variables of the class
     * @param st start time of the event
     * @param et end time of event
     * @param sym symbol of event   (deleted ??? )
     * @param dur duration of event
     */
    int st;
    int et;
    int sym;
    int dur;

    //default constructor
    public TI(){}

    // constructor
    public TI(int s, int e, int sy){
        st = s;
        et = e;
        sym = sy;
        dur = e-s;
    }
}
