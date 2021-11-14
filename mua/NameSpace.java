package mua;

import java.util.ArrayList;
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

class StackedNameSpace implements NameSpace{
    ArrayList<HashMap<String,value>> namespaces=new ArrayList<>();
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
        return namespaces.get(0);
    }

    public HashMap<String,value> getLastSpace(){
        return namespaces.get(namespaces.size()-1);
    }

    public StackedNameSpace cloneWithNewSpace(){
        StackedNameSpace other = new StackedNameSpace();
        other.namespaces.addAll(namespaces);
        other.namespaces.add(new HashMap<>());
        return other;
    }

    public StackedNameSpace limitedCloneWithNewSpace(){
        StackedNameSpace other = new StackedNameSpace();
        other.namespaces.add(namespaces.get(0));
        for(int i=namespaces.size()-LIMITED_LEVEL+2;i<namespaces.size();i++){
            if(i>0){
                other.namespaces.add(namespaces.get(i));
            }            
        }
        other.namespaces.add(new HashMap<>());
        return other;
    }

    @Override
    public value get(String name) {
        value v = null;
        for(int i= namespaces.size()-1;i>=0;i--){
            v=namespaces.get(i).get(name);
            if(v!=null) return v;
        }
        return v;
    }

    @Override
    public void set(String name, value v) {
        namespaces.get(namespaces.size()-1).put(name,v);
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