import java.io.*;
import java.util.*;

public class AlgoVertTIRP {

    // database that is going to be mined
    Database DATABASE = new Database();

    // pairing strategy
    String PS = "bfseclmo";

    // the time at which the algorithm started
    public long startTimestamp = 0;

    // the time at which the algorithm ended
    public long endTimestamp1 = 0; // pre end
    public long endTimestamp2 = 0; // process end

    // the number of frequent TIRP(time interval related pattern)  generated
    public int ftirpCount = 0;



    // the number of STIRP that was constructed
    private int joinCount;

    // min support
    public double minSup;  // absolute value (since HUTPMiner is absolute)
    List<Integer> TIs = new ArrayList<Integer>();   // improve1
    public int eposilon;
    public int min_gap;
    public int max_gap;
    public int min_dur;
    public int max_dur;

    // Map to remember the sup of each event
    Map<Integer, Long> mapItemToSup;

    // writer to write the output file
    BufferedWriter writer = null;
    BufferedWriter writerPaint = null;   // writer to write the data file (for paint)


//    /**
//     * buffer for storing the current itemset that is mined when performing mining
//     * the idea is to always reuse the same buffer to reduce memory usage.
//     */
//    final int BUFFERS_SIZE = 200;
//    private int[] itemsetBuffer = null;

    /**
     * Default constructor
     */
    public AlgoVertTIRP() {
    }

    /**
     * This is the main algorithm of vertTIRP, and it represents the main idea of algorithm
     *
     * @param input      path of input file
     * @param output     path of output file
     * @param relaminSup min support to constrain vs
     * @param timeInt
     * @throws IOException
     */
    public void runAlgorithm(String input, BufferedWriter paintwriter, String output, double relaminSup, List<Integer> timeInt, int epos,
                             int min_g, int max_g, int min_d, int max_d) throws IOException {
        // reset maximum
        MemoryLogger.getInstance().reset();

        //minSup = relaminSup;
        eposilon = epos;
        min_gap = min_g;
        max_gap = max_g;
        min_dur = min_d;
        max_dur = max_d;
        TIs = timeInt;

        startTimestamp = System.currentTimeMillis();

        writer = new BufferedWriter(new FileWriter(output));
        //DataName = data;
        if (paintwriter != null) {
            writerPaint = paintwriter;   // writer to write the data file (for paint)
        }

        // We scan the database a first time to store it in memory
        BufferedReader myInput = null;
        String thisLine;
        try {
            // prepare the object for reading the file
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(input))));
            // for each line (transaction) until the end of file
            int sid = 0;
            while ((thisLine = myInput.readLine()) != null) {
                // if the line is  a comment, is  empty or is a
                // kind of metadata
                if (thisLine.isEmpty() == true || thisLine.charAt(0) == '#' ||
                        thisLine.charAt(0) == '%' || thisLine.charAt(0) == '@') {
                    continue;
                }
                Sequence sequence = new Sequence();
                String split[] = thisLine.split(";");
                int tsize = split.length;
                for (int i = 0; i < tsize; i++) {
                    String[] tuei = split[i].split(",");
                    int sym = Integer.parseInt(tuei[0]);
                    int st = Integer.parseInt(tuei[1]);
                    int et = Integer.parseInt(tuei[2]);
                    TI ti = new TI(st,et,sym);
                    sequence.addTi(ti);
                }
                //sid++;
                DATABASE.addSeq(sequence);
            }
        } catch (Exception e) {
            // catches exception if error while reading the input file
            e.printStackTrace();
        } finally {
            if (myInput != null) {
                myInput.close();
            }
        }

        // CREATE A STIRP TO STORE THE LIST OF events WITH sup>= MIN_UTILITY.
        List<STIRP> listOfStirps = new ArrayList<STIRP>();
        // CREATE A MAP TO STORE STIRP with each event
        // Key : event    Value :  STIRP (use the same memory with list of stirps)
        Map<Integer, STIRP> mapEventToStirp = new HashMap<Integer, STIRP>();

        // SECOND DATABASE PASS TO CONSTRUCT THE STIRPs
        // OF 1-event  HAVING sup  >= minSup (promising items)
        try {
            Database newDatabase = new Database();
            // get new sequence from old sequence by deleting unfrequent items
            for (int sid=0;sid<DATABASE.size;sid++) {
                Sequence sequence = DATABASE.Sequences.get(sid);
                Sequence newSeq = new Sequence();
                // remove non-freq events
                for (int i = 0; i < sequence.tis.size(); i++) {
                    TI ti = sequence.tis.get(i);
                    int sup = DATABASE.mapEventToSup.get(ti.sym);
                    if (sup >= minSup) {
                        newSeq.addTi(ti);
                    }
                }
                newDatabase.addSeq(newSeq);
                // construct listOfStirps by reading new sequence
                Map<Integer,List<Member>> eventsToMembers = newSeq.scanSeq();
                for (int event:eventsToMembers.keySet()){
                    List<Member> members = eventsToMembers.get(event);
                    Element element = new Element(sid,members);
                    // if we have had info of event
                    if (mapEventToStirp.keySet().contains(event)){
                        mapEventToStirp.get(event).addElem(element,newSeq.size);
                    }
                    // if we haven't had info of event
                    else {
                        STIRP stirp = new STIRP(event);
                        mapEventToStirp.put(event,stirp);
                        listOfStirps.add(stirp);
                        stirp.addElem(element,newSeq.size);
                    }
                }
            }
            DATABASE = newDatabase;
        } catch (Exception e) {
            // to catch error while reading the input file
            e.printStackTrace();
        } finally {
            if (myInput != null) {
                myInput.close();
            }
        }

        minSup = Math.round(relaminSup * DATABASE.size);

        // check the memory usage
        MemoryLogger.getInstance().checkMemory();

        endTimestamp1 = System.currentTimeMillis();

        // Mine the database recursively
        for (STIRP stirp : listOfStirps) {
            vertTIRP(stirp, listOfStirps, minSup);
        }

        // check the memory usage again and close the file.
        MemoryLogger.getInstance().checkMemory();
        // close output file
        writer.close();
        // record end time
        endTimestamp2 = System.currentTimeMillis();
    }

    /**
     * This is the recursive method to find all freq TIRPs.
     * @param prefix pattern that is going to be checked and extended
     * @param SLs 1-STIRP that is going to extend prefix
     * @param minSup
     */
    private void vertTIRP(STIRP prefix, List<STIRP> SLs, double minSup) throws IOException {

        // If prefix is a freq. TIRP. We save the itemset:  prefix and we explore extensions of prefix.
        List<Integer> preEvents = prefix.setEvents;
        for (NewRelations relas : prefix.mapRelasToTirp.keySet()) {
            double sup = prefix.mapRelasToTirp.get(relas).getVs(DATABASE.size);
            if (sup >= minSup&&relas!=null) {
                // save to file
                double mhs = prefix.mapRelasToTirp.get(relas).getMhs();
                double md = prefix.mapRelasToTirp.get(relas).getmd();
                writeOut(preEvents, relas, sup, mhs, md);
            }
        }

        // For each extension X of prefix, explore extensions of prefix
        List<STIRP> newPats = new ArrayList<>();
        List<STIRP> newL = new ArrayList<>();
        for (int i = 0; i < SLs.size(); i++) {
            STIRP Y = SLs.get(i);
            STIRP px;
            px = construct(prefix,Y);
            if (px != null){
                newPats.add(px);
                newL.add(Y);
            }
            }
        for (STIRP X:newPats){
            vertTIRP(X,newL,minSup);
        }
        }

    /**
     * Method to write a ftirp to the output file.
     * @param events set of events of pattern
     * @param relas vector of relations of pattern
     * @param sup vertical support of pattern
     * @param mhs
     * @param md
     * @throws IOException
     */
        private void writeOut (List < Integer > events, NewRelations relas, double sup, double mhs, double md) throws IOException {
            ftirpCount++; // increase the number of ftirpCount found

            //Create a string buffer
            StringBuilder buffer = new StringBuilder();
            // append the events
            buffer.append("events: ");
            for (int i = 0; i < events.size(); i++) {
                buffer.append(events.get(i));
                buffer.append(' ');
            }
            // append the relations
            if (relas == null){
                buffer.append("    relas: null");
            }
            else {
                buffer.append("    relas: ");
                String str = relas.Relations;
                List<List<Integer>> paras = relas.Parameters;
                for (int i=0;i<str.length();i++){
                    List<Integer> para = paras.get(i);
                    buffer.append(str.charAt(i));
                    buffer.append("(");
                    for (int j=0;j<para.size();j++){
                        buffer.append(para.get(j));
                    }
                    buffer.append(")");
                }
            }
            // append the statistical values
            buffer.append("  #VS: ");
            buffer.append(sup);
            buffer.append("  #MHS: ");
            buffer.append(mhs);
            buffer.append("  #MD: ");
            buffer.append(md);
            // write to file
            writer.write(buffer.toString());
            writer.newLine();
        }

    /**
     * Method to construct STIRP of XY
     * @param X STIRP of multi-events
     * @param Y STIRP of one-event
     * @return STIRP of XY
     */
        private STIRP construct (STIRP X, STIRP Y) {

            // get size of events in X, and calculate count of relations (k^2-k)/2
            int oldSize = X.Ssize;
            int oldRelaSize = ((oldSize*oldSize)-oldSize)/2;

            // construct new STIRP with new events
            STIRP Z = new STIRP(X.setEvents);
            Z.setEvents.add(Y.setEvents.get(0));
            Z.Ssize++;

            // fulfill the new STIRP Z, for each tirp of X tirpOfX and each sid in tirpOfX
            for (NewRelations relas:X.mapRelasToTirp.keySet()){
                TIRP tirpX = X.mapRelasToTirp.get(relas);
                // use two pointers i and j to retrieval the same sid
                int i = 0; List<Element> elemsX = tirpX.listElems; int sizeX = elemsX.size();
                int j = 0; List<Element> elemsY = Y.mapRelasToTirp.get(null).listElems; int sizeY = elemsY.size();
                while ( i<sizeX && j<sizeY ){
                    int sidX = elemsX.get(i).sid;
                    int sidY = elemsY.get(j).sid;
                    // find the same sid
                    if (sidX == sidY){
                        int sequence_size = DATABASE.Sequences.get(sidX).size;
                        Element elemX = elemsX.get(i);
                        Element elemY = elemsY.get(j);
                        for (Member memberX:elemX.listMems){
                            int eidX = memberX.eid;
//                            // use bisection to find eidY>eidX
//                            int first = 0;
//                            int last = elemY.listMems.size()-1;
                            int pos = -1;  // the position of elemY where eidY can be attached to eidX
//                            while (first <= last){
//                                int middle = ( first + last ) >>> 1; // divide by 2
//                                int lastEidY = 0;
//                                if (middle-1>0){
//                                    lastEidY = elemY.listMems.get(middle-1).eid;
//                                }
//                                int eidY = elemY.listMems.get(middle).eid;
//                                // last eid is <= eidX, and this eid is > eidX, satisfy, get position
//                                if( lastEidY<=eidX && eidY>eidX){
//                                    pos = middle;
//                                    break;
//                                }
//                                // eidY is smaller or equal to eidX, first plus
//                                else if(eidY<=eidX){
//                                    first = middle+1;
//                                }
//                                // lasteidY is larger than eidX, last minus
//                                else if (lastEidY>eidX){
//                                    last = middle-1;
//                                }
//                            }

                            // no such eidY can be attached to eidX
                            for (int s=0; s<elemY.listMems.size(); s++){
                                Member memberY = elemY.listMems.get(s);
                                int eidY = memberY.eid;
                                if (eidY>eidX){
                                    pos = s;
                                    break;
                                }

                            }
                            if (pos == -1){
                                break;
                            }
                            // from position, each eidY can be attached to eidX
                            else {
                                for (int point=pos;point<elemY.listMems.size();point++){
                                    Member memberY = elemY.listMems.get(point);
                                    int eidY = memberY.eid;
                                    // store relations that are going to extended into tirpOfX
                                    NewRelations addRelas = new NewRelations();
                                    // get relation between last event of ex and the only event of ey
                                    TransTable transTable = new TransTable(TIs,PS,eposilon,min_gap,max_gap);
//                                    int as = memberX.si.get(memberX.si.size()-1).st; int ae = memberX.si.get(memberX.si.size()-1).et;
//                                    int bs = memberY.si.get(0).st;              int be = memberY.si.get(0).et;
                                    TI tiA = memberX.si.get(memberX.si.size()-1);
                                    TI tiB = memberY.si.get(0);
                                    NewRelations indexLastXAndY = transTable.getRela_v("v",tiA,tiB,null,null);
                                    addRelas.addRelaPo(indexLastXAndY);
                                    // get relation of each time interval of element ex and the only time interval in element ey
                                    if (oldSize>1){
                                        for (int k=1; k<oldSize; k++){
                                            NewRelations s;
                                            int k_event = (oldSize-1)-k;
                                            int k_rela = oldRelaSize-k;
                                            tiA = memberX.si.get(k_event);
                                            tiB = memberY.si.get(0);
                                            s = transTable.getRela_v("v",tiA,tiB,
                                                    relas.Relations.substring(k_rela,k_rela+1),indexLastXAndY.Relations);
                                            addRelas.addRelaRo(s);
                                        }
                                    }
                                    if (relas != null){
                                        addRelas.addRelaRo(relas);
                                    }
                                    Member memberZ = new Member();
                                    memberZ.eid = Math.max(eidX,eidY);
                                    memberZ.st = Math.min(memberX.st,memberY.st);
                                    memberZ.et = Math.max(memberX.et,memberY.et);
                                    memberZ.addTi(memberX.si);
                                    memberZ.si.add(memberY.si.get(0));
                                    if (Z.mapRelasToTirp.keySet().contains(addRelas)){
                                        Z.mapRelasToTirp.get(addRelas).addMember(sidX,memberZ,sequence_size);
                                    }else {
                                        TIRP tirp = new TIRP();
                                        tirp.addMember(sidX,memberZ,sequence_size);
                                        Z.mapRelasToTirp.put(addRelas,tirp);
                                    }
                                }
                            }
                        }
                        i++;
                        j++;
                    }
                    // not same, and sidX is smaller, then i++
                    else if (sidX<sidY){
                        i++;
                    }
                    // not same, and sidY is smaller, then j++
                    else {
                        j++;
                    }
                }
            }
            Z = Z.isFreq(minSup,DATABASE.size);
            return Z;
        }

    /**
      * Print statistics about the latest execution to System.out.
      */
        public void printStats () throws IOException {
            if (writerPaint == null){
                System.out.println("=============  vertTIRP-MINER ALGORITHM - STATS =============");
                System.out.println(" Total time ~ "                  + (endTimestamp2 - startTimestamp) + " ms");
                System.out.println(" Pre time ~ "                  + (endTimestamp1 - startTimestamp) + " ms");
                System.out.println(" Process time ~ "                  + (endTimestamp2 - endTimestamp1) + " ms");
                System.out.println(" Memory ~ "                      + MemoryLogger.getInstance().getMaxMemory()  + " MB");
                System.out.println(" frequent time interval related patterns count : " + ftirpCount);
                System.out.println("===================================================");
            }
            else {
                writerPaint.write("results:"
                        +(endTimestamp2 - startTimestamp)+":"
                        +(endTimestamp1 - startTimestamp)+":"
                        +(endTimestamp2 - endTimestamp1)+":"
                        + MemoryLogger.getInstance().getMaxMemory()+ ":"
                        + ftirpCount);
                writerPaint.newLine();
            }
        }



    }


