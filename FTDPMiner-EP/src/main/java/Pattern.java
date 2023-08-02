//import com.sun.javafx.collections.ListListenerHelper;

import java.util.ArrayList;
import java.util.List;

//the structure is used to represent a valid pattern
public class Pattern {

    //event and e+,e- are stored respectively
    //timeInt is used to store duration information, like modified relations in vertTDP
    //patternE is used to store event, like a,b,a,d
    //patternT is used to store type, like +,+,-,+, ==>then we can get a+,b+,a-,d+
    // le is used to store last event in pattern, including its event and type
    //flag is used to check if the pattern is valid (>=0)
//    List<List<Integer>> timeInt = new ArrayList<>();  // improve3
    List<List<Integer>> patternE = new ArrayList<List<Integer>>();
    List<List<Integer>> patternT = new ArrayList<List<Integer>>();
    Pair le = new Pair();
    int flag = 0;

    public Pattern(){}
    public Pattern(Pattern other){

        for (int i=0;i<other.patternE.size();i++){
            List<Integer> list = new ArrayList<Integer>();
            for (int j=0;j<other.patternE.get(i).size();j++){
                list.add(other.patternE.get(i).get(j));
                }
            patternE.add(list);
        }

        for (int i=0;i<other.patternT.size();i++){
            List<Integer> list = new ArrayList<Integer>();
            for (int j=0;j<other.patternT.get(i).size();j++){
                list.add(other.patternT.get(i).get(j));
            }
            patternT.add(list);
        }

        le.event = other.le.event;
        le.position = other.le.position;
        flag = other.flag;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pattern other = (Pattern) obj;

        if (patternE.size() != other.patternE.size()){
            return false;
        }
        for (int i=0;i<patternE.size();i++){
            if (patternE.get(i).size() != other.patternE.get(i).size()){
                return false;
            }
            for (int j=0;j<patternE.get(i).size();j++){
                if (patternE.get(i).get(j) != other.patternE.get(i).get(j)){
                    return false;
                }
            }
        }

        if (patternT.size() != other.patternT.size()){
            return false;
        }
        for (int i=0;i<patternT.size();i++){
            if (patternT.get(i).size() != other.patternT.get(i).size()){
                return false;
            }
            for (int j=0;j<patternT.get(i).size();j++){
                if (patternT.get(i).get(j) != other.patternT.get(i).get(j)){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
//        int value = patternT.hashCode();
//        value = value*31 + patternE.hashCode();
//        value = value*31 + timeInt.size();
//        return value;
        final int prime = 31;
        int result = 1;
        result = prime * result + patternT.size();
        result = prime * result + patternE.size();
        return result;
    }

    // extend an e+ or e-;
    // type equals to 1(e+) or -1(e-);
    // eType represent extension type and it equals to 1(s-extension) or 2(i-ext)
    // ti represent time interval that is going to be extended
    public void add(Integer event, Integer type, Integer eType){

        //s-extension
        if (eType==1){
            List<Integer> temp1 = new ArrayList<Integer>();
            temp1.add(event);
            patternE.add(temp1);
            List<Integer> temp2 = new ArrayList<Integer>();
            temp2.add(type);
            patternT.add(temp2);
            flag+=type;
            if (flag<0){
                System.out.println("invalid pattern!");
            }
        }
        //i-extension
        else {
            List<Integer> temp1 = patternE.get(patternE.size()-1);
            temp1.add(event);
            List<Integer> temp2 = patternT.get(patternT.size()-1);
            temp2.add(type);
            flag+=type;
            if (flag<0){
                System.out.println("invalid pattern!");
            }
        }
        //update last event
        le.event = event;
        le.position = type;
    }

    public void add1(Integer event, Integer type) {
        //s-extension
        List<Integer> temp1 = new ArrayList<Integer>();
        temp1.add(event);
        patternE.add(temp1);
        List<Integer> temp2 = new ArrayList<Integer>();
        temp2.add(type);
        patternT.add(temp2);
        flag+=type;
        //update last event
        le.event = event;
        le.position = type;
        if (flag<0){
            System.out.println("invalid pattern - e-!");
        }
    }
}
