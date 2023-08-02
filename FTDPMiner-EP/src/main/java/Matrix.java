import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Matrix {

    Tuple[][] matrix;

    public int[] firstRowOfCol;

    Map<Integer,Integer> mapEventToRow = new HashMap<Integer, Integer>();
    Map<Integer,Integer> mapRowToEvent = new HashMap<Integer, Integer>();


    // construct matrix with given row number and column number
    public Matrix(int r, int c, List<Integer> events){
        matrix = new Tuple[r][c];
        for (int i=0;i<r;i++){
            for (int j=0;j<c;j++){
                Tuple tuple = new Tuple(0,0,0,0,-1);
                matrix[i][j] = tuple;
            }
        }

        firstRowOfCol = new int[c];
        for (int j=0;j<c;j++){
            firstRowOfCol[j] = -1;
        }

        for (int i=0;i<events.size();i++){
            mapEventToRow.put(events.get(i),i);
            mapRowToEvent.put(i,events.get(i));
        }
    }

}
