import java.util.ArrayList;
import java.util.List;

public class Database {

    List<Sequence> database = new ArrayList<Sequence>();
    List<Integer> events = new ArrayList<Integer>();
    int size = 0;
    long utility = 0;

    public Database(){}

    public void add(Sequence sequence){
        database.add(sequence);
        for (int event:sequence.Events)
        { if (!events.contains(event))
          {events.add(event);}
        }
        size++;
        utility = utility+sequence.SU;
    }
}
