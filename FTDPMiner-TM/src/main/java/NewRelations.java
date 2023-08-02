import java.util.ArrayList;
import java.util.List;

public class NewRelations {
    String Relations = "";
    List<List<Integer>> Parameters = new ArrayList<>();

    // default constructor
    public NewRelations(){}

    // constructor: assign values to class variables easily
    public NewRelations(String str,List<Integer> para){
        Relations = str;
        Parameters.add(para);
    }

    /**
     * method to add new relation with parameters into class
     * @param str new relation
     * @param para incidental parameters
     */
    public void addRela(String str,List<Integer> para){
        Relations = Relations + str;
        Parameters.add(para);
    }

    /**
     * method to add a newRelation into class by positive order
     * @param newRelations new relation
     */
    public void addRelaPo(NewRelations newRelations){
        Relations = Relations + newRelations.Relations;
        Parameters.addAll(newRelations.Parameters);
    }

    /**
     * method to add a newRelation into class by reverse order
     * @param newRelations new relation
     */
    public void addRelaRo(NewRelations newRelations){
        Relations = newRelations.Relations + Relations;
        List<List<Integer>> newParas = new ArrayList<>();
        for (List<Integer> a:newRelations.Parameters){
            List<Integer> b = new ArrayList<>();
            b.addAll(a);
            newParas.add(b);
        }
        for (List<Integer> a:Parameters){
            List<Integer> b = new ArrayList<>();
            b.addAll(a);
            newParas.add(b);
        }
        Parameters = newParas;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NewRelations other = (NewRelations) obj;
        if (!Relations.equals(other.Relations)){
            return false;
        }
        int i = Parameters.size();
        int j = other.Parameters.size();
        if (i != j){
            return false;
        }
        for (int point=0;point<i;point++){
            List<Integer> p1 = Parameters.get(point);
            List<Integer> p2 = other.Parameters.get(point);
            if (p1.size()!=p2.size()){
                return false;
            }
            for (int innerPoint=0;innerPoint<p1.size();innerPoint++){
                if (p1.get(innerPoint)!=p2.get(innerPoint)){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Relations.length();
        result = prime * result + Parameters.size();
        return result;
    }
}
