import java.util.ArrayList;
import java.util.List;

public class Relation {
    List<List<Integer>> rela = new ArrayList<>();

    public Relation(){}

    public Relation(Relation other){
        for (int i = 0; i< other.rela.size(); i++){
            List<Integer> list_i = new ArrayList<>();
            for (int j = 0; j < other.rela.get(i).size(); j++){
                list_i.add(other.rela.get(i).get(j));
            }
            rela.add(list_i);
            }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Relation other = (Relation) obj;

        if (rela.size() != other.rela.size()){
            return false;
        }
        for (int i = 0; i< rela.size(); i++){
            if (rela.get(i).size() != other.rela.get(i).size()){
                return false;
            }
            for (int j = 0; j< rela.get(i).size(); j++){
                if (rela.get(i).get(j) != other.rela.get(i).get(j)){
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
        result = prime * result + rela.size();
        return result;
    }

}
