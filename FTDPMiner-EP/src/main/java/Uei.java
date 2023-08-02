// the structure is used to represent (e,st,ft,u), (e+,st,ft,u), (e-,st,ft,0)

public class Uei {
    int event;
    int st;
    int ft;
    int utility;
    // flag=0 <--> e; 1 <--> e+; -1<--> e-;
    int flag = 0;

    public Uei(int e, int s, int f, int u){
        this.event = e;
        this.st = s;
        this.ft = f;
        this.utility = u;
    }

}
