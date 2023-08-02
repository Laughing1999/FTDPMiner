import java.util.ArrayList;
import java.util.List;

//the structure is used to represent original sequence like <(e1,st,ft,u),(e2,st,ft,u),(e3,st,ft,u)...>
// and other basic inf of seq like event set, ordered time set, and seq utility.
// besides, seq version 1 and 2 are also stored in the structure
public class Sequence {

    List<Uei> Sequence = new ArrayList<Uei>();
    List<Integer> Events = new ArrayList<Integer>();  //events happened in this sequence(distinct)
    List<Integer> times = new ArrayList<Integer>();   //times mentioned in this sequence, which is sorted <.
    int SU = 0;                                 // sequence utility

    //第一阶段的列表形态--集合
    SeqVersion1 seqVersion1;
    //第二阶段的列表形态--矩阵
    Matrix seqVersion2;

    public Sequence(){}

    public void add(Uei uei){
        Sequence.add(uei);
        SU+= uei.utility;
        if (!Events.contains(uei.event)){
            Events.add(uei.event);
        }
        if (!times.contains(uei.st)){
                times.add(uei.st);
            }
        if (!times.contains(uei.ft)){
            times.add(uei.ft);
        }
    }

    // for an original sequence, call the function, then seq version1 is got.
    public void version1(){
        seqVersion1 = new SeqVersion1();
        // creat space for each subset
        for (int i=0;i<times.size();i++){
            List<Uei> seqn = new ArrayList<Uei>();
            List<Uei> seqp = new ArrayList<Uei>();
            seqVersion1.seqVersionN.add(seqn);
            seqVersion1.seqVersionP.add(seqp);
            seqVersion1.particialSU.add(0);
        }
        // add element for each subset
        for (Uei uei:Sequence){
            int index_st = times.indexOf(uei.st);
            int index_ft = times.indexOf(uei.ft);
            Uei tuei1 = new Uei(uei.event, uei.st, uei.ft, uei.utility);
            tuei1.flag = 1;
            seqVersion1.seqVersionP.get(index_st).add(tuei1);
            seqVersion1.particialSU.set(index_st, seqVersion1.particialSU.get(index_st)+uei.utility);
            seqVersion1.mapAdd(uei.event,index_st,index_ft);
            Uei tuei2 = new Uei(uei.event, uei.st, uei.ft, 0);
            tuei2.flag = -1;
            seqVersion1.seqVersionN.get(index_ft).add(tuei2);
        }
    }

    //get seqVersion2(matrix) from seqVersion1(set of set)
    public void version2(){

        int event_num = Events.size(); // this is row number
        int time_num = times.size();   // this is column number

        //create space for matrix
        seqVersion2 = new Matrix(event_num,time_num,Events);
        int ru = SU;

        for (int i=0;i<time_num;i++){
            List<Uei> ep = seqVersion1.seqVersionP.get(i);
            if (ep.size()>0){
                for (int j=0;j<ep.size();j++){

                    Uei uei = ep.get(j);
                    int row = seqVersion2.mapEventToRow.get(uei.event);
                    // corrected by lfy
                    // update lnext, so that every e- can point to the first e+ in this time
                    if (j == 0){
                        backMatrixLnext(seqVersion2,row,i);
                        seqVersion2.firstRowOfCol[i] = row;
                    }
                    // end corrected by lfy
                    Tuple tuple = seqVersion2.matrix[row][i];
                    tuple.u = uei.utility;                         // this.u(1)
                    // corrected by lfy
                    if (tuple.lnext!=-1){
                        tuple.lnext = -1;
                    }
                    // end corrected by lfy
                    Pair pair = new Pair(uei.event,i);
                    tuple.fy = seqVersion1.mapPairToEn.get(pair);  // this.fy(3)
                    tuple.ru = ru;                                 // this.ru(2)
                    backMatrixrow(seqVersion2,uei.event,ru,i);        // updata previous column of 2 and 4
                    if (j!=0){
                       backMatrixcol(seqVersion2,uei.event,i,row);        // updata previous column of 5
                    }
                    ru = ru-uei.utility;
                }
            }
        }
    }

    private void backMatrixLnext(Matrix seqVersion2, int row, int col) {
        for (int i=0;i<seqVersion2.matrix.length;i++){
            if (i!=row){
                seqVersion2.matrix[i][col].lnext = row;
            }
        }
    }

    //updata column 2 and 4 in previous set
    public void backMatrixrow(Matrix matrix,int event,int ru,int p){
        int row = matrix.mapEventToRow.get(event);
        for (int i=p-1;i>=0;i--){
            Tuple tuple1 = matrix.matrix[row][i];
            if (tuple1.ru == 0){
                tuple1.ru = ru;
            }
            if (tuple1.tnext == 0){
                tuple1.tnext = p;
            }
        }
    }

    //updata column 5 in previous set
    public void backMatrixcol(Matrix matrix,int event,int p,int row){
        int row1 = matrix.mapEventToRow.get(event);
        for (int i=0;i<seqVersion2.matrix.length;i++){
            if (i!=row1){
                int u = matrix.matrix[i][p].u;
                int lnext = matrix.matrix[i][p].lnext;
                // corrected by lfy
                if (u>0 && lnext==-1){
                    Tuple tuple1 = matrix.matrix[i][p];
                    tuple1.lnext = row;
                }
                // end corrected by lfy
            }
        }
    }

}
