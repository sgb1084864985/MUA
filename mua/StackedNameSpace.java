package mua;

// import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
// import java.util.List;
import java.util.ListIterator;

public class StackedNameSpace implements NameSpace{
    LinkedList<HashMap<String,value>> namespaces=new LinkedList<>();
    static final int LIMITED_LEVEL=3;
    public void addEmptySpace(){
        namespaces.add(new HashMap<>());
    }
    public void addSpace(HashMap<String,value> space){
        namespaces.add(space);
    }
    public void addSpaces(StackedNameSpace space){
        namespaces.addAll(space.namespaces);
    }
    public HashMap<String,value> getFirstSpace(){
        return namespaces.getFirst();
    }

    public HashMap<String,value> getLastSpace(){
        return namespaces.getLast();
    }

    public StackedNameSpace cloneWithNewSpace(){
        StackedNameSpace other = new StackedNameSpace();
        other.namespaces.addAll(namespaces);
        other.namespaces.add(new HashMap<>());
        return other;
    }
    public StackedNameSpace cloneSubSpaces(int start){
        return cloneSubSpaces(start, namespaces.size());
    }


    // end not included
    public StackedNameSpace cloneSubSpaces(int start,int end){
        StackedNameSpace ret = new StackedNameSpace();
        Iterator<HashMap<String,value>> it=namespaces.iterator();
        while(it.hasNext()){
            ret.namespaces.add(it.next());
        }
        for(int i=0;it.hasNext() && i<end;i++){
            HashMap<String,value> tmp=it.next();
            if(i>=start){
                ret.namespaces.add(tmp);
            }
        }
        return ret;
    }

    public StackedNameSpace cloneSubSpacesReverse(int start,int end){
        StackedNameSpace ret = new StackedNameSpace();
        Iterator<HashMap<String,value>> it=namespaces.descendingIterator();
        while(it.hasNext()){
            ret.namespaces.add(it.next());
        }
        for(int i=namespaces.size()-1;it.hasNext() && i>=start;i--){
            HashMap<String,value> tmp=it.next();
            if(i<end){
                ret.namespaces.add(tmp);
            }
        }
        return ret;
    }


    public StackedNameSpace limitedCloneWithNewSpace(){
        StackedNameSpace other = new StackedNameSpace();
        other.namespaces.add(namespaces.getFirst());
        try{
            ListIterator<HashMap<String, value>> listIt=namespaces.listIterator
            (Math.
                max(1,namespaces.size()-LIMITED_LEVEL+2)
            );

            while(listIt.hasNext()){
                other.namespaces.add(listIt.next());
            }
        }
        catch(IndexOutOfBoundsException ex){}

        // for(int i=namespaces.size()-LIMITED_LEVEL+2;i<namespaces.size();i++){
        //     if(i>0){
        //         other.namespaces.add(namespaces.get(i));
        //     }            
        // }
        other.namespaces.add(new HashMap<>());
        return other;
    }

    @Override
    public value get(String name) {
        value v = null;
        // for(int i= namespaces.size()-1;i>=0;i--){
        //     v=namespaces.get(i).get(name);
        //     if(v!=null) return v;
        // }

        Iterator<HashMap<String,value>> it = namespaces.descendingIterator();
        while(it.hasNext()){
            v=it.next().get(name);
            if(v!=null){
                return v;
            }
        }

        return null;
    }

    @Override
    public void set(String name, value v) {
        getLastSpace().put(name,v);
    }

    public void export(String name,value v){
        getFirstSpace().put(name, v);
    }

    public void put(String name,value v){
        set(name,v);
    }

    @Override
    public boolean contains(String name) {
        for(int i= namespaces.size()-1;i>=0;i--){
            if(namespaces.get(i).containsKey(name))
                return true;
        }
        return false;
    }

    public boolean containsInLocal(String name){
        return getLastSpace().containsKey(name);
    }

    @Override
    public void remove(String name) {
        namespaces.get(namespaces.size()-1).remove(name);
    }
}