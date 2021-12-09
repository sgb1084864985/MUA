package mua;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class value{
    protected abstract void setValue(String val);
    public abstract value_type getValueType();
    public abstract value lightClone();
}

class value_void extends value{
    @Override
    public String toString() {
        return "`void`";
    }
    @Override
    public value_type getValueType() {
        return value_type.VOID;
    }
    @Override
    protected void setValue(String val) {
        
    }
    @Override
    public value lightClone() {
        // TODO Auto-generated method stub
        return this;
    }
}

class value_number extends value implements Comparable<value_number>{
    double val;
    @Override
    public String toString() {
        return String.valueOf(val);
    }
    @Override
    public value_type getValueType(){
        return value_type.NUMBER;
    }
    @Override
    protected void setValue(String val) {
        this.val=Double.valueOf(val);
    }
    value_number(String val){
        setValue(val);
    }
    value_number(double val){
        this.val=val;
    }
    @Override
    public int compareTo(value_number o) {
        return ((Double)val).compareTo((Double)(o.val));
    }
    @Override
    public value lightClone() {
        return new value_number(val);
    }
}

class value_word extends value implements Comparable<value_word>{
    String val;
    @Override
    public String toString() {
        return val;
    }

    @Override
    public value_type getValueType(){
        return value_type.WORD;
    }
    @Override
    protected void setValue(String val) {
        this.val=val;
    }

    value_number toNumber(){
        return new value_number(val);
    }
    boolean canBeNumber(){
        return val.matches("^(-?\\d+)(\\.\\d+)?$");
    }
    value_bool toBool(){
        return new value_bool(val);
    }
    boolean canBeBool(){
        return val.matches("true|false");
    }
    value_word(String val){
        setValue(val);
    }
    @Override
    public int compareTo(value_word o) {
        return val.compareTo(o.val);
    }
    @Override
    public value lightClone() {
        return new value_word(val);
    }
}

class value_bool extends value{
    boolean val;
    @Override
    public String toString() {
        if(val==true) return "true";
        else return "false";
    }

    @Override
    public value_type getValueType(){
        return value_type.BOOL;
    }

    @Override
    protected void setValue(String val) {
        this.val=Boolean.valueOf(val);
    }
    value_bool(String val){
        setValue(val);
    }
    value_bool(boolean val){
        this.val=val;
    }
    @Override
    public value lightClone() {
        return new value_bool(val);
    }
}

class value_list extends value implements Iterable<value>{
    ArrayList<value> elements = new ArrayList<>();
    StackedNameSpace env=null;

    @Override
    public value lightClone() {
        value_list v =new value_list();
        v.elements=elements;
        v.env=env;
        return v;
    }

    public void add(value v){
        elements.add(v);
    }

    public value get(int index){
        return elements.get(index);
    }

    public boolean mayBeFunction(){
        return 
        elements.size()==2 && 
        elements.get(0) instanceof value_list && 
        elements.get(1) instanceof value_list;
    }

    public int size(){
        return elements.size();
    }

    @Override
    public Iterator<value> iterator() {
        return elements.iterator();
    }

    @Override
    public String toString() {
        StringBuilder string_builder=new StringBuilder();
        string_builder.append("[ ");
        Iterator<value> it=elements.iterator();
        value v;
        while(it.hasNext()){
            v=it.next();
            string_builder.append(v.toString()+" ");
        }
        string_builder.append("]");
        return string_builder.toString();
    }
    @Override
    public value_type getValueType() {
        return value_type.LIST;
    }
    @Override
    protected void setValue(String val) {
        
    }
}