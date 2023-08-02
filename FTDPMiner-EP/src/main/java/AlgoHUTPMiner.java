import java.io.*;
import java.util.*;


public class AlgoHUTPMiner {

    Database DATABASE = new Database();
    long MinSup = 0;
    List<Pattern> HUPs = new ArrayList<Pattern>();
    int TDPsize = 0;
    List<Integer> TIs = new ArrayList<Integer>();   // improve1

    /** writer to write the output file  */
    BufferedWriter writer = null;
    BufferedWriter writerPaint = null;   // writer to write the data file (for paint)

    //empty construction
    public AlgoHUTPMiner(){}

    public void runAlgorithm(String input, BufferedWriter paintwriter, String output, double relaminS, List<Integer> TI) throws IOException {
        //record memory and start time
        MemoryLogger.getInstance().reset();
        long startTimestamp = System.currentTimeMillis();
        writer = new BufferedWriter(new FileWriter(output));
        TIs = TI;                         // improve1
        if (paintwriter != null){
            writerPaint = paintwriter;   // writer to write the data file (for paint)
        }

        //read database from .txt
        BufferedReader myInput = null;
        String thisLine = null;
        try {
            myInput = new BufferedReader(new InputStreamReader( new FileInputStream(new File(input))));
            while ((thisLine = myInput.readLine()) != null) {
                Sequence sequence = new Sequence();
                String split[] = thisLine.split(";");
                int tsize = split.length;
                for (int i=0;i<tsize;i++){
                    String[] tuei = split[i].split(",");
                    int u0 = Integer.parseInt(tuei[0]);int u1 = Integer.parseInt(tuei[1]);
                    int u2 = Integer.parseInt(tuei[2]);int u3 = Integer.parseInt(tuei[3]);
                    Uei uei = new Uei(u0,u1,u2,u3);
                    sequence.add(uei);
                }
                Collections.sort(sequence.times);
                sequence.version1();
                sequence.version2();
                DATABASE.add(sequence);
            }
        } catch (Exception e) {
            // catches exception if error while reading the input file
            e.printStackTrace();
        }finally {
            if(myInput != null){
                myInput.close();
            }
        }
        // sort events in Database by size
        Collections.sort(DATABASE.events);
        MinSup = Math.round(relaminS * DATABASE.size);

        List<AuxiliaryTable> auxiliaryTables = new ArrayList<AuxiliaryTable>();
        auxiliaryTables = conAuxiTab(DATABASE);

        // check the memory usage
        MemoryLogger.getInstance().checkMemory();

        long endTimestamp1 = System.currentTimeMillis();

        for (AuxiliaryTable auxiliaryTable:auxiliaryTables){
            AuxiliaryTable at = auxiliaryTable.isFreq(MinSup);
            if (at != null){
                writeOut1(auxiliaryTable.pattern);
                HUTPMiner(at);
            }
        }

        // check the memory usage again and close the file.
        MemoryLogger.getInstance().checkMemory();
        // close output file
        writer.close();
        // record end time
        long endTimestamp2 = System.currentTimeMillis();
        Print(startTimestamp,endTimestamp1,endTimestamp2,TDPsize);

    }

    private void Print(long startTimestamp, long endTimestamp1,long endTimestamp2, int size) throws IOException {
        if (writerPaint == null){
            System.out.println("=============  HUTPMiner ALGORITHM  =============");
            System.out.println(" Total time ~ "                  + (endTimestamp2 - startTimestamp) + " ms");
            System.out.println(" Pre time ~ "                  + (endTimestamp1 - startTimestamp) + " ms");
            System.out.println(" Process time ~ "                  + (endTimestamp2 - endTimestamp1) + " ms");
            System.out.println(" Memory ~ "                      + MemoryLogger.getInstance().getMaxMemory()  + " MB");
            System.out.println(" High-utility itemsets count : " + size);
            //System.out.println(" Candidate count : "             + candidateCount);
        }else {
            writerPaint.write("results:"
                    +(endTimestamp2 - startTimestamp)+":"
                    +(endTimestamp1 - startTimestamp)+":"
                    +(endTimestamp2 - endTimestamp1)+":"
                    + MemoryLogger.getInstance().getMaxMemory()+ ":"
                    + size);
            writerPaint.newLine();
        }

    }

    private void HUTPMiner(AuxiliaryTable t_auxiliaryTable) throws IOException {
        Pattern pattern = t_auxiliaryTable.pattern;
        Map<Pattern,AuxiliaryTable> childList = new HashMap<>();

        // t is ended with e+
        if (pattern.le.position == 1){
            // get relation
            for (Relation relation:t_auxiliaryTable.mapRelasToAuxi.keySet()){
                AuxiliaryTableP t_auxiliaryTableP = t_auxiliaryTable.mapRelasToAuxi.get(relation);
                //get instance
                for (Integer sid:t_auxiliaryTableP.sids){
                    Sequence sequence = DATABASE.database.get(sid);
                    Matrix matrix = DATABASE.database.get(sid).seqVersion2;
                    List<Instance> instances = t_auxiliaryTableP.mapSidToInst.get(sid);
                    for (Instance t_instance:instances){
                        int t_lp = t_instance.lp;
                        int lasttime = t_instance.lastTime;
                        //extension strategy 1 (e+)
                        //i-e+
                        int col = t_lp;                                       // the column of last event
                        int row = matrix.mapEventToRow.get(pattern.le.event); // the row of last event
                        int lnext = matrix.matrix[row][col].lnext;            // e+(in this line, just get the row_num) happened at the same time
                        while (lnext != -1){
                            HandleIB(sequence.times,lasttime,matrix,pattern,relation,sid,lnext,col,t_instance,childList);
                            lnext = matrix.matrix[lnext][col].lnext;
                        }
                        //s-e+
                        int fp = t_instance.fp;
                        if (fp == 0){
                            fp = matrix.matrix[0].length;
                        }
                        if ( t_lp+1 <= matrix.matrix[0].length-1 ){
                            // change lfy
                            for ( col=t_lp+1; col<fp;col++){
                                lnext = matrix.firstRowOfCol[col];
                                while (lnext != -1){
                                    HandleSB(sequence.times,matrix,pattern,relation,sid,lnext,col,t_instance,childList);
                                    lnext = matrix.matrix[lnext][col].lnext;
                                }
                            }
                            // end change lfy
                        }
                        //extension strategy 2 (e-)
                        //s-e-
                        Pair pair = t_instance.EQNUs.get(0);
                        int event = pair.event;
                        row = matrix.mapEventToRow.get(event);
                        col = pair.position;
                        int ti = getTI(sequence.times, t_lp,col);
                        HandleSF(sequence.times,matrix,pattern,relation,sid,row,col,t_instance,childList,ti);
                    }
                }
            }
        }
        // if t is ended with a e-
        else {
            // if t is a temporal pattern
            if (pattern.flag == 0) {
                // get relation
                for (Relation relation:t_auxiliaryTable.mapRelasToAuxi.keySet()){
                    AuxiliaryTableP t_auxiliaryTableP = t_auxiliaryTable.mapRelasToAuxi.get(relation);
                    //get instance
                    for (Integer sid : t_auxiliaryTableP.sids) {
                        Sequence sequence = DATABASE.database.get(sid);
                        Matrix matrix = DATABASE.database.get(sid).seqVersion2;
                        List<Instance> instances = t_auxiliaryTableP.mapSidToInst.get(sid);
                        for (Instance t_instance : instances) {
                            int t_lp = t_instance.lp;
                            int lasttime = t_instance.lastTime;
                            //extension strategy 3 (e+)
                            //i-e+
                            int col = t_lp;                                       // the column of last event
                            int row = matrix.mapEventToRow.get(pattern.le.event); // the row of last event
                            int lnext = matrix.matrix[row][col].lnext;            // e+(in this line, just get the row_num) happened at the same time
                            while (lnext != -1){
                                HandleIB(sequence.times, lasttime, matrix,pattern, relation, sid,lnext,col,t_instance,childList);
                                lnext = matrix.matrix[lnext][col].lnext;
                            }
                            //s-e+
                            int fp = matrix.matrix[0].length;
                            if ( t_lp+1 <= matrix.matrix[0].length-1 ){
                                // change lfy
                                for ( col=t_lp+1; col<fp;col++){
                                    lnext = matrix.firstRowOfCol[col];
                                    while (lnext != -1){
                                        HandleSB(sequence.times,matrix,pattern,relation,sid,lnext,col,t_instance,childList);
                                        lnext = matrix.matrix[lnext][col].lnext;
                                    }
                                }
                                // end change lfy
                            }
                        }
                    }

                }

            }
            // t is not a temporal pattern
            else{
                // get relation
                for (Relation relation:t_auxiliaryTable.mapRelasToAuxi.keySet()) {
                    AuxiliaryTableP t_auxiliaryTableP = t_auxiliaryTable.mapRelasToAuxi.get(relation);
                    //get instance
                    for (Integer sid : t_auxiliaryTableP.sids) {
                        Sequence sequence = DATABASE.database.get(sid);
                        Matrix matrix = DATABASE.database.get(sid).seqVersion2;
                        List<Instance> instances = t_auxiliaryTableP.mapSidToInst.get(sid);
                        for (Instance t_instance : instances) {
                            int t_lp = t_instance.lp;
                            int lasttime = t_instance.lastTime;
                            //extension strategy 5
                            // i-e-
                            Pair pair = t_instance.EQNUs.get(0);
                            int event = pair.event;
                            int row = matrix.mapEventToRow.get(event);
                            int col = pair.position;
                            if (t_lp == col){
                                HandleIF(sequence.times,matrix,pattern,relation,sid,row,col,t_instance,childList);
                            }
                            else {
                                //extension strategy 5
                                //s-e-
                                row = matrix.mapEventToRow.get(t_instance.EQNUs.get(0).event);
                                col = pair.position;
                                int ti = getTI(sequence.times, t_lp,col);
                                HandleSF(sequence.times, matrix,pattern, relation, sid,row,col,t_instance,childList, ti);
                                //extension strategy 4
                                //i-e+
                                col = t_lp;                                       // the column of last event
                                row = matrix.mapEventToRow.get(pattern.le.event); // the row of last event
                                int lnext = matrix.matrix[row][col].lnext;            // e+(in this line, just get the row_num) happened at the same time
                                while (lnext != -1){
                                    HandleIB(sequence.times, lasttime, matrix,pattern, relation, sid,lnext,col,t_instance,childList);
                                    lnext = matrix.matrix[lnext][col].lnext;
                                }
                                // s-e+
                                int fp = t_instance.fp;
                                if (fp == 0){
                                    fp = matrix.matrix[0].length;
                                }
                                if ( t_lp+1 <= matrix.matrix[0].length-1 ){
                                    // change lfy
                                    for ( col=t_lp+1; col<fp;col++){
                                        lnext = matrix.firstRowOfCol[col];
                                        while (lnext != -1){
                                            HandleSB(sequence.times,matrix,pattern,relation,sid,lnext,col,t_instance,childList);
                                            lnext = matrix.matrix[lnext][col].lnext;
                                        }
                                    }
                                    // end change lfy
                                }

                            }
                        }
                    }
                }

            }

        }

        for(Pattern child:childList.keySet()){
            boolean notSingle = false;
            if (child.patternE.size()>2){
                notSingle = true;
            }
            if (child.patternE.size()==2){
                if (child.patternE.get(0).size()>1){
                    notSingle = true;
                }
            }

            AuxiliaryTable at = childList.get(child).isFreq(MinSup);
            if (at != null && child.flag==0 && notSingle){
                {
                    //HUPs.add(child);
                    // save to file
                    writeOut2(at);
                }
            }
            if (at != null){
                HUTPMiner(at);
            }
        }
    }

    //according to order of times and two last positions, to get the time interval of a s-extension
    public int getTI(List<Integer> times, int t_lp, int col) {
        int diff = times.get(col)-times.get(t_lp);
        for (int i=0;i<TIs.size();i++){
            if (diff<=TIs.get(i)){
                return i+1;
            }
        }
        return TIs.size()+1;
    }

    private void writeOut2(AuxiliaryTable at) throws IOException {
        Pattern pattern = at.pattern;
        for (Relation relation: at.mapRelasToAuxi.keySet()){
            TDPsize++;
            writeOut(pattern,relation,at.mapRelasToAuxi.get(relation).getS());
        }
    }

    private void writeOut1(Pattern pattern) throws IOException {
        //Create a string buffer
        StringBuilder buffer = new StringBuilder();
        // append
        buffer.append("prefix: ");
        buffer.append(pattern.patternE.get(0).get(0));
        buffer.append("+");
        buffer.append(" ------------------------------------------------------------------------------------------------------");
        // write to file
        writer.write(buffer.toString());
        writer.newLine();
    }

    private void writeOut(Pattern pattern, Relation relation, int sup) throws IOException {
        //Create a string buffer
        StringBuilder buffer = new StringBuilder();
        // append
        buffer.append("< ");
        for (int i = 0; i < pattern.patternE.size(); i++) {
            buffer.append("(");
            for (int j = 0; j < pattern.patternE.get(i).size(); j++){
                buffer.append(pattern.patternE.get(i).get(j));
                if (pattern.patternT.get(i).get(j) == 1) {
                    buffer.append("+");
                }else {
                    buffer.append("-");
                }
                buffer.append(",");
            }
            buffer.append(")");
        }
        buffer.append(" >");

        // start improve3
        buffer.append("  relation: ");
        for (int i = 0;i<relation.rela.size();i++){
            for (int j = 0;j<relation.rela.get(i).size();j++){
                buffer.append(relation.rela.get(i).get(j));
            }
        }
        // end improve3

        buffer.append("  Sup: ");
        buffer.append(sup);
        // write to file
        writer.write(buffer.toString());
        writer.newLine();
    }

    /**
     * i-e-
     * @param times
     * @param matrix
     * @param pattern
     * @param relation
     * @param sid
     * @param row
     * @param col
     * @param t_instance
     * @param childList
     */
    private void HandleIF(List<Integer> times, Matrix matrix, Pattern pattern, Relation relation, Integer sid, int row, int col, Instance t_instance, Map<Pattern, AuxiliaryTable> childList) {
        Pattern newPattern = new Pattern(pattern);
        int newEvent = matrix.mapRowToEvent.get(row);
        newPattern.add(newEvent,-1,2);
        int thistime = times.get(col);

        Relation newRelation = new Relation(relation);

        List<Pair> t_equs = t_instance.EQUs;
        List<Pair> t_eqnus = t_instance.EQNUs;
        Tuple tuple = matrix.matrix[row][col];          // the tuple of e+ waiting to be extended

        // start improve3 construct newEQs
        List<Pair> newEqus = new ArrayList<Pair>();
        for (int i=0;i<t_equs.size();i++){
            newEqus.add(t_equs.get(i));
        }
        newEqus.add(t_eqnus.get(0));
        List<Pair> newEqnus = new ArrayList<Pair>();
        for (int i=1;i<t_eqnus.size();i++){
            newEqnus.add(t_eqnus.get(i));
        }
        // end improve3

        Collections.sort(newEqus,new Comparator<Pair>(){
            @Override
            public int compare(Pair o1, Pair o2) {
                if(o1.position>o2.position){
                    return 1;
                }
                else
                    return -1;
            }
        });
        Collections.sort(newEqnus,new Comparator<Pair>(){
            @Override
            public int compare(Pair o1, Pair o2) {
                if(o1.position>o2.position){
                    return 1;
                }
                else
                    return -1;
            }
        });

        contains(childList,newPattern,newRelation,sid,col,newEqus,newEqnus, thistime);
    }

    /**
     * s-e-
     * @param times
     * @param matrix
     * @param pattern
     * @param relation
     * @param sid
     * @param row
     * @param col
     * @param t_instance
     * @param childList
     * @param ti
     */
    private void HandleSF(List<Integer> times, Matrix matrix, Pattern pattern, Relation relation, Integer sid, int row, int col, Instance t_instance, Map<Pattern, AuxiliaryTable> childList, int ti) {

        Pattern newPattern = new Pattern(pattern);
        int newEvent = matrix.mapRowToEvent.get(row);
        newPattern.add(newEvent,-1,1);
        int thistime = times.get(col);

        Relation newRelation = new Relation(relation);

        List<Pair> t_equs = t_instance.EQUs;
        List<Pair> t_eqnus = t_instance.EQNUs;

        // start improve3 construct newEQs
        List<Pair> newEqus = new ArrayList<Pair>();
        for (int i=0;i<t_equs.size();i++){
            newEqus.add(t_equs.get(i));
        }
        newEqus.add(t_eqnus.get(0));
        List<Pair> newEqnus = new ArrayList<Pair>();
        for (int i=1;i<t_eqnus.size();i++){
            newEqnus.add(t_eqnus.get(i));
        }
        // end improve3

        Collections.sort(newEqus,new Comparator<Pair>(){
            @Override
            public int compare(Pair o1, Pair o2) {
                if(o1.position>o2.position){
                    return 1;
                }
                else
                    return -1;
            }
        });
        Collections.sort(newEqnus,new Comparator<Pair>(){
            @Override
            public int compare(Pair o1, Pair o2) {
                if(o1.position>o2.position){
                    return 1;
                }
                else
                    return -1;
            }
        });

        contains(childList,newPattern,newRelation,sid,col,newEqus,newEqnus, thistime);
    }

    /**
     * s-e+
     * @param times
     * @param matrix
     * @param pattern
     * @param relation
     * @param sid
     * @param lnext
     * @param col
     * @param t_instance
     * @param childList
     */
    private void HandleSB(List<Integer> times, Matrix matrix, Pattern pattern, Relation relation, Integer sid, int lnext, int col, Instance t_instance, Map<Pattern, AuxiliaryTable> childList) {
        // ti is time of new added event

        Pattern newPattern = new Pattern(pattern);
        int newEvent = matrix.mapRowToEvent.get(lnext);
        newPattern.add(newEvent,1,1);
        int thistime = times.get(col);

        Relation newRelation = new Relation(relation);

        // start improve3
        List<Pair> t_equs = t_instance.EQUs;
        List<Pair> t_eqnus = t_instance.EQNUs;
        Tuple tuple = matrix.matrix[lnext][col];          // the tuple of e+ waiting to be extended

        //  construct newEQs
        int newFy = tuple.fy;
        Pair pair = new Pair(newEvent,newFy);
        List<Pair> newEqus = new ArrayList<Pair>();
        for (int i=0;i<t_equs.size();i++){
            newEqus.add(t_equs.get(i));
        }
        List<Pair> newEqnus = new ArrayList<Pair>();
        for (int i=0;i<t_eqnus.size();i++){
            newEqnus.add(t_eqnus.get(i));
        }

        // added duration information
        List<Integer> addedDIs = new ArrayList<>();
        addedDIs.add(getTI(times, col,newFy));
        for (Pair pair1:newEqus){
            addedDIs.add(getTI(times,pair1.position,col));
        }
        for (Pair pair2:newEqnus){
            addedDIs.add(getTI(times,col,pair2.position));
        }
        newRelation.rela.add(addedDIs);
        // end improve3

        newEqnus.add(pair);

        Collections.sort(newEqus,new Comparator<Pair>(){
            @Override
            public int compare(Pair o1, Pair o2) {
                if(o1.position>o2.position){
                    return 1;
                }
                else
                    return -1;
            }
        });
        Collections.sort(newEqnus,new Comparator<Pair>(){
            @Override
            public int compare(Pair o1, Pair o2) {
                if(o1.position>o2.position){
                    return 1;
                }
                else
                    return -1;
            }
        });

        contains(childList,newPattern,newRelation,sid,col,newEqus,newEqnus,thistime);
    }

    /**
     * i-e+
     * in fact, the function just add an instance of newPattern into auxiliary table
     * @param times
     * @param lasttime
     * @param matrix
     * @param pattern 'pattern' is a prefix and 't_instance' is the instance of pattern in the sequence
     * @param relation
     * @param sid we have 'sid' and 'matrix' of where the instance lies;
     * @param lnext 'lnext' and 'col' is the position of e+ that is waiting to be extended
     * @param col
     * @param t_instance
     * @param childList
     */
    private void HandleIB(List<Integer> times, int lasttime, Matrix matrix, Pattern pattern, Relation relation, Integer sid, int lnext, int col, Instance t_instance, Map<Pattern, AuxiliaryTable> childList) {

        Pattern newPattern = new Pattern(pattern);
        int newEvent = matrix.mapRowToEvent.get(lnext);
        newPattern.add(newEvent,1,2);
        int thistime = times.get(col);

        Relation newRelation = new Relation(relation);

        // start improve3
        List<Pair> t_equs = t_instance.EQUs;
        List<Pair> t_eqnus = t_instance.EQNUs;
        Tuple tuple = matrix.matrix[lnext][col];          // the tuple of e+ waiting to be extended

        //  construct newEQs
        int newFy = tuple.fy;
        Pair pair = new Pair(newEvent,newFy);
        List<Pair> newEqus = new ArrayList<Pair>();
        for (int i=0;i<t_equs.size();i++){
            newEqus.add(t_equs.get(i));
        }
        List<Pair> newEqnus = new ArrayList<Pair>();
        for (int i=0;i<t_eqnus.size();i++){
            newEqnus.add(t_eqnus.get(i));
        }

        // added duration information
        List<Integer> addedDIs = new ArrayList<>();
        addedDIs.add(getTI(times, col,newFy));
        for (Pair pair1:newEqus){
            addedDIs.add(getTI(times,pair1.position,col));
        }
        for (Pair pair2:newEqnus){
            addedDIs.add(getTI(times,col,pair2.position));
        }
        newRelation.rela.add(addedDIs);
        // end improve3

        newEqnus.add(pair);

        Collections.sort(newEqus,new Comparator<Pair>(){
            @Override
            public int compare(Pair o1, Pair o2) {
                if(o1.position>o2.position){
                    return 1;
                }
                else
                    return -1;
            }
        });
        Collections.sort(newEqnus,new Comparator<Pair>(){
            @Override
            public int compare(Pair o1, Pair o2) {
                if(o1.position>o2.position){
                    return 1;
                }
                else
                    return -1;
            }
        });

        contains(childList,newPattern,newRelation,sid,col,newEqus,newEqnus, thistime);

    }

    /**
     * check if childlist contains auxiliaryTableP of Pattern-other and Relation-relation, and update childList
     * @param childlist
     * @param other
     * @param relation
     * @param sid
     * @param col
     * @param newEqus
     * @param newEqnus
     * @param thistime
     */
    public void contains(Map<Pattern, AuxiliaryTable> childlist, Pattern other, Relation relation,
                         Integer sid, int col, List<Pair> newEqus, List<Pair> newEqnus, int thistime){
        //Res res = new Res();

        int flag = 0;

        for (Pattern pattern:childlist.keySet()){
            // check if the pattern equals other
            flag = 1;

            if (pattern.patternE.size() != other.patternE.size()){
                //res.para1 = false;
                flag = 0;
            }
            else {
                for (int i=0;i<pattern.patternE.size();i++){
                    if (pattern.patternE.get(i).size() != other.patternE.get(i).size()){
                        //res.para1 = false;
                        flag = 0;
                    }
                    else{
                        for (int j=0;j<pattern.patternE.get(i).size();j++){
                            if (!pattern.patternE.get(i).get(j).equals(other.patternE.get(i).get(j)) ){
                                //res.para1 = false;
                                flag = 0;
                            }
                        }
                    }

                }
            }

            if (pattern.patternT.size() != other.patternT.size()){
                //res.para1 = false;
                flag = 0;
            }
            else {
                for (int i=0;i<pattern.patternT.size();i++){
                    if (pattern.patternT.get(i).size() != other.patternT.get(i).size()){
                        //res.para1 = false;
                        flag = 0;
                    }
                    else {
                        for (int j=0;j<pattern.patternT.get(i).size();j++){
                            if (!pattern.patternT.get(i).get(j) .equals(other.patternT.get(i).get(j)) ){
                                //res.para1 = false;
                                flag = 0;
                            }
                        }
                    }
                }
            }

            // conatin pattern
            if (flag == 1){
                // contain relation
                if (childlist.get(pattern).mapRelasToAuxi.keySet().contains(relation)){
                    AuxiliaryTableP p = childlist.get(pattern).mapRelasToAuxi.get(relation);
                    p.add(sid,col,newEqus,newEqnus,thistime);
                }
                // do not contain relation
                else {
                    AuxiliaryTable auxiliaryTable = childlist.get(pattern);
                    AuxiliaryTableP p = new AuxiliaryTableP();
                    p.add(sid,col,newEqus,newEqnus,thistime);
                    auxiliaryTable.mapRelasToAuxi.put(relation,p);
                }
                break;
            }
        }

        // do not contain the pattern
        if (flag==0){
            AuxiliaryTable auxiliaryTable = new AuxiliaryTable();
            auxiliaryTable.pattern = other;
            AuxiliaryTableP p = new AuxiliaryTableP();
            p.add(sid,col,newEqus,newEqnus,thistime);
            auxiliaryTable.mapRelasToAuxi.put(relation,p);
            childlist.put(other,auxiliaryTable);
        }

    }

    /**
     * construct auxiliary table for each e+ in database (do not consider MDU)
     * @param database
     * @return
     */
    private List<AuxiliaryTable> conAuxiTab(Database database) {
        List<AuxiliaryTable> auxiliaryTables = new ArrayList<AuxiliaryTable>();
        // for each event
        for (Integer event: database.events){

            // creat auxiliarytable for event
            Pattern pattern = new Pattern();
            pattern.add1(event,1);
            AuxiliaryTable auxiliaryTable = new AuxiliaryTable(pattern);

            // for each sequence
            for (int i=0;i< database.size;i++){            // scan each sequence in database to help construct auxiliary tables
                Sequence sequence = database.database.get(i);
                if (sequence.Events.contains(event)){      // verify if the sequence contains event
                    Matrix matrix = sequence.seqVersion2;
                    int row = matrix.mapEventToRow.get(event);
                    int col = 0;
                    Tuple tuple = matrix.matrix[row][col]; // get first tuple of event in matrix
                    int u = tuple.u;
                    int nextP = tuple.tnext;
                    // the first occurence of event in sequence
                    if (u>0){
                        // start improve3
                        Relation relation = new Relation();
                        List<Pair> equs = new ArrayList<Pair>();
                        List<Pair> eqnus = new ArrayList<Pair>();
                        int thistime = sequence.times.get(col);
                        Pair pair = new Pair(event,sequence.seqVersion1.mapGet(event,col));
                        eqnus.add(pair);
                        List<Integer> addedDIs = new ArrayList<>();
                        addedDIs.add(getTI(sequence.times, col, pair.position));
                        relation.rela.add(addedDIs);
                        // if exists relation, then update AuxiliaryTableP p matched relation in auxiliarytable
                        if (auxiliaryTable.mapRelasToAuxi.containsKey(relation)){
                            AuxiliaryTableP p = auxiliaryTable.mapRelasToAuxi.get(relation);
                            p.add(i,col,equs,eqnus,thistime);   // add first instance into table
                        }
                        // if no this relation, then creat AuxiliaryTableP p to match relation and store in auxiliarytable
                        else {
                            AuxiliaryTableP p = new AuxiliaryTableP();
                            auxiliaryTable.mapRelasToAuxi.put(relation,p);
                            // the following is details
                            p.add(i,col,equs,eqnus,thistime);
                        }
                    }
                    // the following occurences of event in sequence
                    while (nextP!=0){
                        col = nextP;
                        tuple = matrix.matrix[row][col];
                        u = tuple.u;
                        nextP = tuple.tnext;
                        if (u>0){
                            Relation relation = new Relation();
                            List<Pair> equs = new ArrayList<Pair>();
                            List<Pair> eqnus = new ArrayList<Pair>();
                            int thistime = sequence.times.get(col);
                            Pair pair = new Pair(event,sequence.seqVersion1.mapGet(event,col));
                            eqnus.add(pair);
                            List<Integer> addedDIs = new ArrayList<>();
                            addedDIs.add(getTI(sequence.times, col, pair.position));
                            relation.rela.add(addedDIs);
                            // if exists relation, then update AuxiliaryTableP p matched relation in auxiliarytable
                            if (auxiliaryTable.mapRelasToAuxi.containsKey(relation)){
                                AuxiliaryTableP p = auxiliaryTable.mapRelasToAuxi.get(relation);
                                p.add(i,col,equs,eqnus,thistime);   // add first instance into table
                            }
                            // if no this relation, then creat AuxiliaryTableP p to match relation and store in auxiliarytable
                            else {
                                AuxiliaryTableP p = new AuxiliaryTableP();
                                auxiliaryTable.mapRelasToAuxi.put(relation,p);
                                // the following is details
                                p.add(i,col,equs,eqnus,thistime);
                            }
                        }
                    }
                }
            }
            auxiliaryTables.add(auxiliaryTable);
        }
        return auxiliaryTables;
    }

}
