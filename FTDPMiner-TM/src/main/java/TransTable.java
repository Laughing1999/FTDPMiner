import java.util.ArrayList;
import java.util.List;

public class TransTable {

    List<Integer> TimeInter = new ArrayList<Integer>();
    String PS;
    int epos;
    int minGap;
    int maxGap;

    public TransTable(List<Integer> timeInter,String ps,int epo, int mingap,int maxgap){
        TimeInter = timeInter;
        PS = ps;
        epos = epo;
        minGap = mingap;
        maxGap = maxgap;
    }

    /**
     * method to return true relation between time event A and B, with transitivity relations
     * @param type vertTIRP or Allen
     * @param tiA time interval of A
     * @param tiB time interval of B
     * @param fisrtR relation between time interval A and (middle interval) E
     * @param secondR relation between (middle interval) E and time interval B
     * @return relation between time event A and B
     */
    public NewRelations getRela_v(String type, TI tiA, TI tiB, String fisrtR, String secondR){
        NewRelations newRelations = new NewRelations();
        String res;
        if (fisrtR==null&&secondR==null){
            res = PS;
        }else {
            res = getTrans(type, fisrtR, secondR, PS);
        }
        int index = calc_v(0,res,tiA.st,tiA.et,tiB.st,tiB.et);
        if (index>=0){
            newRelations = getNewRela(res.substring(index,index+1),tiA.st,tiA.et,tiB.st,tiB.et);
            return newRelations;
        }else {
            return null;
        }
    }

    /**
     * method to get new relation
     * @param substring relation type
     * @param st
     * @param et
     * @param st1
     * @param et1
     * @return new relation
     */
    private NewRelations getNewRela(String substring, int st, int et, int st1, int et1) {
        List<Integer> paras = new ArrayList<>();
        NewRelations newRelations = new NewRelations(substring,paras);
        // start improve2
        if (substring.equals("b")){
            int para1 = getTimeInt(et-st);
            int para2 = getTimeInt(st1-et);
            int para3 = getTimeInt(et1-st1);
            paras.add(para1);
            paras.add(para2);
            paras.add(para3);
        }
        else if (substring.equals("f")){
            int para1 = getTimeInt(et-st);
            int para2 = getTimeInt(et1-st1);
            paras.add(para1);
            paras.add(para2);
        }
        else if (substring.equals("s")){
            int para1 = getTimeInt(et-st);
            int para2 = getTimeInt(et1-st1);
            paras.add(para1);
            paras.add(para2);
        }
        else if (substring.equals("e")){
            int para1 = getTimeInt(et-st);
            paras.add(para1);
        }
        else if (substring.equals("c")){
            int para1 = getTimeInt(et-st);
            int para2 = getTimeInt(et-st1);
            int para3 = getTimeInt(et1-st1);
            paras.add(para1);
            paras.add(para2);
            paras.add(para3);
        }
        else if (substring.equals("m")){
            int para1 = getTimeInt(et-st);
            int para2 = getTimeInt(et1-st1);
            paras.add(para1);
            paras.add(para2);
        }
        else if (substring.equals("o")){
            int para1 = getTimeInt(et-st);
            int para2 = getTimeInt(et-st1);
            int para3 = getTimeInt(et1-st1);
            paras.add(para1);
            paras.add(para2);
            paras.add(para3);
        }
        // end improve2
        else {
            System.out.println("Error--Class:TransTable--Func:getNewRela!");
        }
        return newRelations;
    }

    private int getTimeInt(int diff) {
        if (diff == 0){
            return 0;
        }
        for (int i=0;i<TimeInter.size();i++){
            if (diff<=TimeInter.get(i)){
                return i+1;
            }
        }
        return TimeInter.size()+1;
    }

    /**
     * method to return possible sorted relations
     * @param type relations of vertTIRP or Allen
     * @param fisrtR first relation r(A,B)
     * @param secondR second relation r(B,C)
     * @param PS pairing strategy
     * @return possible sorted relations of r(A,C)
     */
    private String getTrans(String type, String fisrtR, String secondR, String PS){
        String res = "";
        // check type is vertTIRP or Allen
        if (!(type.equals("v")||type.equals("a"))){
            System.out.println("Error parameter of TransTable!");
            return null;
        }
        // Trans table based of the vertTIRP article
        if (type .equals("v") ){
            if (fisrtR .equals("b") ){
                res =  "b";
            }
            else if (fisrtR .equals("m") ){
                if (secondR .equals("b") ){
                    res = "b";
                }
                else {
                    res = "bm";
                }
            }
            else if (fisrtR .equals("c") ){
                if (secondR.equals("o") ||secondR.equals("m") ||secondR.equals("s") ){
                    res = "cfo";
                }
                if (secondR.equals("f") ||secondR.equals("e") ){
                    res = "cf";
                }
                if (secondR.equals("c") ||secondR.equals("l") ){
                    res = "c";
                }
                if (secondR.equals("b") ){
                    res = "bcfmo";
                }
            }
            else if (fisrtR.equals("o") ){
                if (secondR.equals("s") ||secondR.equals("e") ){
                    res = "mo";
                }
                if (secondR.equals("b") ){
                    res = "b";
                }
                if (secondR.equals("c") ){
                    res = "bcfmo";
                }
                if (secondR.equals("o") ){
                    res = "bmo";
                }
                if (secondR.equals("m") ){
                    res = "bm";
                }
                if (secondR.equals("f") ){
                    res = "bfmo";
                }
                if (secondR.equals("l") ){
                    res = "cfo";
                }
            }
            else if (fisrtR.equals("s") ){
                if (secondR.equals("o") ||secondR.equals("f") ){
                    res = "bmo";
                }
                if (secondR.equals("b") ){
                    res = "b";
                }
                if (secondR.equals("c") ){
                    res = "bcfmo";
                }
                if (secondR.equals("m") ){
                    res = "bm";
                }
                if (secondR.equals("s") ){
                    res = "ms";
                }
                if (secondR.equals("e") ){
                    res = "emos";
                }
                if (secondR.equals("l") ){
                    res = "cflmo";
                }
            }
            else if (fisrtR.equals("f") ){
                if (secondR.equals("c") ||secondR.equals("l") ){
                    res = "cf";
                }
                if (secondR.equals("o") ||secondR.equals("s") ){
                    res = "fmo";
                }
                if (secondR.equals("b") ){
                    res = "bm";
                }
                if (secondR.equals("m") ){
                    res = "bmo";
                }
                if (secondR.equals("f") ){
                    res = "cfmo";
                }
                if (secondR.equals("e") ){
                    res = "cflmo";
                }
            }
            else if (fisrtR.equals("l") ){
                if (secondR.equals("c") ||secondR.equals("l") ){
                    res = "c";
                }
                if (secondR.equals("b") ){
                    res = "bcfmo";
                }
                if (secondR.equals("o") ){
                    res = "cfo";
                }
                if (secondR.equals("m") ){
                    res = "cmo";
                }
                if (secondR.equals("s") ){
                    res = "celo";
                }
                if (secondR.equals("f") ){
                    res = "cf";
                }
                if (secondR.equals("e") ){
                    res = "cefl";
                }
            }
            else if (fisrtR.equals("e") ){
                if (secondR.equals("b") ){
                    res = "bm";
                }
                if (secondR.equals("c") ){
                    res = "cf";
                }
                if (secondR.equals("o") ){
                    res = "fmo";
                }
                if (secondR.equals("m") ){
                    res = "bemo";
                }
                if (secondR.equals("s") ){
                    res = "eos";
                }
                if (secondR.equals("f") ){
                    res = "cfm";
                }
                if (secondR.equals("e") ){
                    res = "cefo";
                }
                if (secondR.equals("l") ){
                    res = "cfl";
                }
            }
        }
        // Trans table based on Allen
        if (type.equals("a") ){
            if (fisrtR.equals("b") ){
                res = "b";
            }
            else if (fisrtR.equals("e") ){
                res = secondR;
            }
            else if (fisrtR.equals("m") ){
                if (secondR.equals("s") ||secondR.equals("e") ){
                    res = "m";
                }
                else {
                    res = "b";
                }
            }
            else if (fisrtR.equals("c") ){
                if (secondR.equals("c") ||secondR.equals("f") ||secondR.equals("e") ){
                    res = "c";
                }
                else if (secondR.equals("o") ||secondR.equals("m") ||secondR.equals("s") ){
                    res = "cfo";
                }
                else if (secondR.equals("b") ){
                    res = "bcfmo";
                }
            }
            else if (fisrtR.equals("o") ){
                if (secondR.equals("b") ||secondR.equals("m") ){
                    res = "b";
                }
                else if (secondR.equals("s") ||secondR.equals("e") ){
                    res = "o";
                }
                else if (secondR.equals("o") ||secondR.equals("f") ){
                    res = "bmo";
                }
                else if (secondR.equals("c") ){
                    res = "bcfmo";
                }
            }
            else if (fisrtR.equals("s") ){
                if (secondR.equals("b") ||secondR.equals("m") ){
                    res = "b";
                }
                else if (secondR.equals("s") ||secondR.equals("e") ){
                    res = "s";
                }
                else if (secondR.equals("o") ||secondR.equals("f") ){
                    res = "bmo";
                }
                else if (secondR.equals("c") ){
                    res = "bcfmo";
                }
            }
            else if (fisrtR.equals("f") ){
                if (secondR.equals("f") ||secondR.equals("e") ){
                    res = "f";
                }
                else if (secondR.equals("s") ||secondR.equals("o") ){
                    res = "o";
                }
                else {
                    res = secondR;
                }
            }
        }
        res = rsort(PS,res);
        return res;
    }

    /**
     * method to resort relations by PS, used in func. getTrans in class TransTable
     * @param PS pairing strategy
     * @param res relations
     * @return sorted relations
     */
    private String rsort(String PS, String res){
        String newRes = "";
        if (res.length()==0){
            System.out.println("Error, no relation return!");
        }
        else if (res.length()==1){
            return res;
        }
        else {
            for (int i=0;i<PS.length();i++){
                String str = PS.substring(i,i+1);
                if (res.contains(str)){
                    newRes = newRes+str;
                }
                if (newRes.length()==res.length()){
                    break;
                }
            }
        }
        return newRes;
    }

    /**
     * method to get index of true relation in promising relation set
     * @param flag current index
     * @param res promising relation set
     * @param as
     * @param ae
     * @param bs
     * @param be
     * @return index of true relation in promising relation set
     */
    public int calc_v(int flag,String res,int as,int ae,int bs,int be) {
        if (flag<res.length()){
            // check relation before
            if (res.substring(flag,flag+1).equals("b")){
                boolean findTrueRela;
                findTrueRela = checkb(ae, bs, epos, minGap, maxGap);
                // if before is true, return the index
                if ( findTrueRela ){
                    return flag;
                }
                // if before is false, recall calc_v with flag+1
                else {
                    flag = flag+1;
                    int getIndex = calc_v(flag,res,as,ae,bs,be);
                    return getIndex;
                }
            }
            // check relation meet
            else if (res.substring(flag,flag+1).equals("m")){
                String str;
                str = checkm(as,ae,bs,be,epos);
                // if meet is true, return the index
                if (str == null){
                    return flag;
                }
                // if meet is false, recall calc_v with calculated flag
                else {
                    flag = getIndex(flag,str,res);
                    int getIndex = calc_v(flag,res,as,ae,bs,be);
                    return getIndex;
                }

            }
            // check relation overlap
            else if (res.substring(flag,flag+1).equals("o")){
                String str;
                str = checko(as, ae, bs, be, epos);
                // if overlap is true, return the index
                if (str == null){
                    return flag;
                }
                // if overlap is false, recall calc_v with calculated flag
                else {
                    flag = getIndex(flag,str,res);
                    int getIndex = calc_v(flag,res,as,ae,bs,be);
                    return getIndex;
                }
            }
            // check relation left_contain
            else if (res.substring(flag,flag+1).equals("l")){
                String str;
                str = checkl(as, ae, bs, be, epos);
                // if left_contain is true, return the index
                if (str == null){
                    return flag;
                }
                // if left_contain is false, recall calc_v with calculated flag
                else {
                    flag = getIndex(flag,str,res);
                    int getIndex = calc_v(flag,res,as,ae,bs,be);
                    return getIndex;
                }
            }
            // check relation contain
            else if (res.substring(flag,flag+1).equals("c")){
                String str;
                str = checkc(as, ae, bs, be, epos);
                // if contain is true, return the index
                if (str == null){
                    return flag;
                }
                // if contain is false, recall calc_v with calculated flag
                else {
                    flag = getIndex(flag,str,res);
                    int getIndex = calc_v(flag,res,as,ae,bs,be);
                    return getIndex;
                }
            }
            // check relation finished by
            else if (res.substring(flag,flag+1).equals("f")){
                String str;
                str = checkf(as, ae, bs, be, epos);
                // if finished by is true, return the index
                if (str == null){
                    return flag;
                }
                // if finished by is false, recall calc_v with calculated flag
                else {
                    flag = getIndex(flag,str,res);
                    int getIndex = calc_v(flag,res,as,ae,bs,be);
                    return getIndex;
                }
            }
            // check relation equal
            else if (res.substring(flag,flag+1).equals("e")){
                String str;
                str = checke(as, ae, bs, be, epos);
                // if equal is true, return the index
                if (str == null){
                    return flag;
                }
                // if equal is false, recall calc_v with calculated flag
                else {
                    flag = getIndex(flag,str,res);
                    int getIndex = calc_v(flag,res,as,ae,bs,be);
                    return getIndex;
                }
            }
            // check relation starts
            else if (res.substring(flag,flag+1).equals("s")){
                String str;
                str = checks(as, ae, bs, be, epos);
                // if starts is true, return the index
                if (str == null){
                    return flag;
                }
                // if starts is false, recall calc_v with calculated flag
                else {
                    flag = getIndex(flag,str,res);
                    int getIndex = calc_v(flag,res,as,ae,bs,be);
                    return getIndex;
                }
            }
            // if the relation we need to check is not in the 8 relations
            else {
                System.out.println("Error relation in func. calc_v in class TransTable! -1");
                return -1;
            }
        }
        // if the flag has exceeded the index
        else {
            System.out.println("Error index in func. calc_v in class TransTable! -2");
            return -2;
        }
    }

    /**
     * when use group strategy, we get some unpromising relations. this method
     * help us get index of next promising relation in res.
     * @param flag current index
     * @param str unpromising relations set
     * @param res possible relations set got by transitivity
     * @return index of next promising relation in res
     */
    private int getIndex(int flag, String str, String res) {
        flag = flag+1;
        while (true && flag<res.length()){
            if (!str.contains(res.substring(flag,flag+1))){
                break;
            }
            flag = flag+1;
            if (flag == res.length()){
                System.out.println("Error in getIndex in TransTable!");
            }
        }
        return flag;
    }


    /**
     * the next five functions are group strategy of vertTIRP
     * @return group relation with common condition
     */
    private String bsas() {
        return "mocf";
    }

    private String beae() {
        return "mos";
    }

    private String aebe() {
        return "lc";
    }

    private String absBsas() {
        return "les";
    }

    private String absBeae() {
        return "fe";
    }


    /**
     * the next 8 functions are to check the relation between two time intervals in vertTIRP;
     * @param as start time of time interval A
     * @param ae end time of time interval A
     * @param bs start time of time interval B
     * @param be end time of time interval B
     * @param epos eposilon
     * @return group relations that are unpromising
     */
    private String checks(int as, int ae, int bs, int be, int epos) {
        // first condition passes
        if (Math.abs(bs-as)<=epos){
            // second condition passes, true, return null
            if (be-ae>epos){
                return null;
            }
            // second condition does not pass, group strategy
            else {
                return beae();
            }
        }
        // first condition does not pass, group strategy
        else {
            return absBsas();
        }
    }

    // equal is special, because sometimes, event with smaller eid may have equal relation with target event
    public String checke(int as, int ae, int bs, int be, int epos) {
        // first condition passes
        if (Math.abs(bs-as)<=epos){
            // second condition passes, true, return null
            if (Math.abs(be-ae)<=epos){
                return null;
            }
            // second condition does not pass, group strategy
            else {
                return absBeae();
            }
        }
        // first condition does not pass, group strategy
        else {
            return absBsas();
        }
    }

    private String checkf(int as, int ae, int bs, int be, int epos) {
        // first condition passes
        if (bs-as>epos){
            // second condition passes, true, return null
            if (Math.abs(be-ae)<=epos){
                return null;
            }
            // second condition does not pass, group strategy
            else {
                return absBeae();
            }
        }
        // first condition does not pass, group strategy
        else {
            return bsas();
        }
    }

    private String checkc(int as, int ae, int bs, int be, int epos) {
        // first condition passes
        if (bs-as>epos){
            // second condition passes, true, return null
            if (ae-be>epos){
                return null;
            }
            // second condition does not pass, group strategy
            else {
                return aebe();
            }
        }
        // first condition does not pass, group strategy
        else {
            return bsas();
        }
    }

    private String checkl(int as, int ae, int bs, int be, int epos) {
        // first condition passes
        if (Math.abs(bs-as)<=epos){
            // second condition passes, true, return null
            if (ae-be>epos){
                // third condition passes, true, return null
                if (epos>0){
                    return null;
                }
                // third condition does not pass, no group strategy
                else {
                    return "";
                }
            }
            // second condition does not pass, group strategy
            else {
                return aebe();
            }
        }
        // first condition does not pass, group strategy
        else{
            return absBsas();
        }
    }

    private String checko(int as,int ae,int bs,int be,int epos) {
        // first condition passes
        if (bs-as>epos){
            // second condition passes
            if (be-ae>epos){
                // third condition passes, true
                if (ae-bs>epos){
                    return null;
                }
                // third condition does not pass, no group strategy
                else {
                    return "";
                }
            }
            // second condition does not pass, group strategy
            else {
                return beae();
            }
        }
        // first condition does not pass, group strategy
        else {
            return bsas();
        }
    }

    private String checkm(int as,int ae,int bs,int be,int epos) {
            // first condition passes
            if (bs-as>epos){
                // second condition passes
                if (be-ae>epos){
                    // third condition passes
                    if (Math.abs(bs-ae)<=epos){
                        return null;
                    }
                    // third condition does not pass
                    else {
                        return "";
                    }
                }
                // second condition does not pass, group strategy
                else {
                    return "os";
                }
            }
            // first condition does not pass, group strategy
            else{
                return "oce";
            }
    }

    private boolean checkb(int ae,int bs,int epos,int minG,int maxG) {
        if ( bs-ae>epos && bs-ae<maxG && bs-ae>minG){
            return true;
        }
        return false;
    }
}
