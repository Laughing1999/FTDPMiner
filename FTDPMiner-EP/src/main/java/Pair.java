//define a class pair to represent " e+ in a position "
public class Pair{
    int event;
    int position;

    public Pair(){}
    public Pair(int e, int p){
        event = e;
        position = p;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair other = (Pair) obj;
        if (event != other.event)
            return false;
        if (position != other.position)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + event;
        result = prime * result + position;
        return result;
    }

}
