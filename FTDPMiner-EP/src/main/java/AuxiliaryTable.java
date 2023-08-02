import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuxiliaryTable {
    Pattern pattern = new Pattern();
    Map<Relation,AuxiliaryTableP> mapRelasToAuxi = new HashMap<>();

    public AuxiliaryTable(){}

    public AuxiliaryTable(Pattern p){
        pattern = p;
    }

    /**
     * method to return frequent AuxiliarytablePs in a Auxiliarytable
     * @param minSup given threshold, absolute not relative
     * @return if null, then no frequent tirp
     */
    public AuxiliaryTable isFreq(double minSup){

        if (mapRelasToAuxi.keySet().size()==0){
            return null;
        }
        AuxiliaryTable auxiliaryTable = new AuxiliaryTable();
        boolean hasFreq = false;
        for (Relation relas:mapRelasToAuxi.keySet()){
            AuxiliaryTableP p = mapRelasToAuxi.get(relas);
            if (p.getS() >= minSup){
                auxiliaryTable.mapRelasToAuxi.put(relas,p);
                hasFreq = true;
            }
        }
        if (!hasFreq){
            return null;
        }
        auxiliaryTable.pattern = pattern;
        return auxiliaryTable;
    }

}
