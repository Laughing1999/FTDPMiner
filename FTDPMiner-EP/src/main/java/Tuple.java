//the structure is used to an element of matrix
public class Tuple {
    int u;
    int ru;
    int fy;
    int tnext;
    int lnext; //note: it records the position of an event in matrix, not the lable of event

    public Tuple(int u,int ru,int fy,int tnext,int lnext){
        this.u = u;
        this.ru = ru;
        this.fy = fy;
        this.tnext = tnext;
        this.lnext = lnext;
    }
}
