package mua;

// import java.util.ArrayList;
import java.util.HashMap;

public interface NameSpace{
    value get(String name);
    void set(String name,value v);
    void remove(String name);
    boolean contains(String name);
}

class OneLevelNameSpace implements NameSpace{
    HashMap<String,value> names;
    @Override
    public value get(String name) {
        return names.get(name);
    }
    @Override
    public void set(String name, value v) {
        names.put(name, v);
    }
    @Override
    public boolean contains(String name) {
        return names.containsKey(name);
    }
    @Override
    public void remove(String name) {
        names.remove(name);
    }
}

class TwoLevelNameSpace implements NameSpace {
    HashMap<String,value> global_names,local_names;
    TwoLevelNameSpace(HashMap<String,value> g,HashMap<String,value> l){
        global_names=g;
        local_names=l;
    }
    TwoLevelNameSpace(HashMap<String,value> g){
        global_names=g;
        local_names=new HashMap<>();
    }
    TwoLevelNameSpace(){
        local_names=new HashMap<>();
    }

    public void setAsGlobalNames(TwoLevelNameSpace space){
        global_names=space.local_names;
    }

    @Override
    public value get(String name) {
        value v = local_names.get(name);
        if(v==null){
            v=global_names.get(name);
        }
        return v;
    }
    @Override
    public void set(String name, value v) {
        local_names.put(name,v);
    }
    public void setGlobal(String name,value v){
        global_names.put(name,v);
    }
    @Override
    public void remove(String name) {
        local_names.remove(name);
    }
    @Override
    public boolean contains(String name) {
        return local_names.containsKey(name) || global_names.containsKey(name);
    }
    public boolean containsInLocal(String name){
        return local_names.containsKey(name);
    }
    public boolean containsInGlobal(String name){
        return global_names.containsKey(name);
    }
}

