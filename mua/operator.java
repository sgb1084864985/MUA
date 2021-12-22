package mua;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
// import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public interface operator{
    value operate(token_stream paras,StackedNameSpace name_sets)throws mua_except;
    value_type getReturnType();

    public static value getOneValue(token_stream paras,StackedNameSpace name_sets,List<value_type> types_wanted)throws mua_except{
        if(!paras.has_next()){
            if(types_wanted.get(0)==value_type.VOID){
                return new value_void();
            }
            throw new muaParametersMissing();
        }

        token t = paras.nextToken();
        value val=paras.toValue(t, name_sets);

        if(t.isList()){
            val=val.lightClone();
            value_list vList=(value_list)val;
            vList.env=name_sets;
        }

        if(types_wanted.get(0)==value_type.ALL_TYPE){
            return val;
        }

        for(value_type type:types_wanted){
            if(val.getValueType()==type){
                return val;
            }
        }
        for(value_type type:types_wanted){
            if(val instanceof value_word){
                value_word word=(value_word)val;
                if(type==value_type.NUMBER && word.canBeNumber()){
                    return word.toNumber();
                }
                else if(type==value_type.BOOL && word.canBeBool()){
                    return word.toBool();
                }
            }
            if(type==value_type.WORD){
                if(val instanceof value_bool){
                    value_bool vBool=(value_bool) val;
                    return new value_word(vBool.toString());
                }
                if(val instanceof value_number){
                    value_number vNumber=(value_number) val;
                    return new value_word(vNumber.toString());
                }
            }
        }
        throw new muaTokenTypeMissMatch();
    }

    public static ArrayList<value> getValues(token_stream paras,StackedNameSpace name_sets,List<value_type> types_wanted)throws mua_except{
        ArrayList<value> values = new ArrayList<>();
        
        for(int i=0;i<types_wanted.size();i++){
            values.add(getOneValue(paras, name_sets, types_wanted.subList(i,i+1)));
        }
        return values;
    }

    default public void checkValueType(value v,value_type t)throws mua_except{
        if((v.getValueType()!=t)){
            throw new muaTokenTypeMissMatch(t.name());
        }
    }
}


class operator_make implements operator{
    @Override
    public value operate(token_stream paras,StackedNameSpace name_sets)throws mua_except{
        ArrayList<value> value_list=operator.getValues(paras, name_sets, List.of(value_type.WORD,value_type.ALL_TYPE));
        value_word name = (value_word) value_list.get(0);
        value val=value_list.get(1);
        name_sets.put(name.val, val);
        return val;
    }

    @Override
    public value_type getReturnType() {
        return value_type.ALL_TYPE;
    }
}

class operator_read implements operator{
    @Override
    public value operate(token_stream paras,StackedNameSpace name_sets)throws mua_except {
        String val=commonInput.input.next();
        return new value_word(val);
    }
    @Override
    public value_type getReturnType() {
        return value_type.WORD;
    }
}

class operator_print implements operator{
    @Override
    public value operate(token_stream paras,StackedNameSpace name_sets)throws mua_except {
        value val = operator.getOneValue(paras, name_sets, List.of(value_type.ALL_TYPE));
        System.out.println(val.printString());
        return val;
    }
    @Override
    public value_type getReturnType() {
        return value_type.ALL_TYPE;
    }
}

class operator_thing implements operator{
    @Override
    public value operate(token_stream paras,StackedNameSpace name_sets)throws mua_except {
        value val = operator.getOneValue(paras, name_sets, List.of(value_type.WORD));

        value_word name=(value_word)val;

        val=name_sets.get(name.val);
        if(val==null){
            throw new muaNameNotExist(name.val);
        }
        return val;
    }
    @Override
    public value_type getReturnType() {
        return value_type.ALL_TYPE;
    }
}

abstract class operator_arithmetic implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets)throws mua_except {
        ArrayList<value> values = operator.getValues(paras, name_sets,List.of(value_type.NUMBER,value_type.NUMBER));
        value_number a1,a2;
        a1=(value_number)(values.get(0));
        a2=(value_number)(values.get(1));
        return arithmeticOperate(a1, a2);
    }
    @Override
    public value_type getReturnType() {
        return value_type.NUMBER;
    }

    protected abstract value arithmeticOperate(value_number val1,value_number val2);
}

class operator_add extends operator_arithmetic{
    @Override
    protected value arithmeticOperate(value_number val1, value_number val2){
        return new value_number(val1.val+val2.val);        
    }
}

class operator_sub extends operator_arithmetic{
    @Override
    protected value arithmeticOperate(value_number val1, value_number val2) {
        return new value_number(val1.val-val2.val);        
    }
}
class operator_mul extends operator_arithmetic{
    @Override
    protected value arithmeticOperate(value_number val1, value_number val2) {
        return new value_number(val1.val*val2.val);        
    }
}
class operator_div extends operator_arithmetic{
    @Override
    protected value arithmeticOperate(value_number val1, value_number val2) {
        if(val2.val==0) throw new ArithmeticException("divide by zero");
        return new value_number(val1.val/val2.val);        
    }
}
class operator_mod extends operator_arithmetic{
    @Override
    protected value arithmeticOperate(value_number val1, value_number val2) {
        if(val2.val==0) throw new ArithmeticException("divide by zero");
        return new value_number(val1.val % val2.val);        
    }
}

class operator_colon implements operator{
    @Override
    public value operate(token_stream paras,StackedNameSpace name_sets)throws mua_except {
        if(!paras.has_next()){
            throw new muaParametersMissing();
        }

        token t=paras.nextToken();
        if(t.getType()!=token_type.IDENTIFIER){
            throw new muaTokenTypeMissMatch("WORD");
        }

        value val=name_sets.get(t.getValue());
        if(val==null){
            throw new muaNameNotExist(t.getValue());
        }
        return val;
    }
    @Override
    public value_type getReturnType() {
        return value_type.ALL_TYPE;
    }
}

class operator_start_list implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets)throws mua_except {
        return operate(paras, name_sets, true);
    }

    public value operate(token_stream paras, StackedNameSpace name_sets, boolean bindEnv)throws mua_except {
        token t;
        value_list list = new value_list();

        
        // if(bindEnv){
        //     list.env=name_sets.cloneSubSpaces(1);
        // }


        while(paras.has_next()){
            t=paras.nextTokenInList();
            switch(t.getType()){
                case WORD: 
                    list.add(paras.toValue(t, name_sets));
                    break;
                case OPERATOR: // only case of '[' & ']'
                    if(t.getValue().equals("[")){
                        list.add(operate(paras, name_sets, false));
                    }
                    else return list;
                default: break;
            }
        }
        throw new muaParametersMissing();
    }

    @Override
    public value_type getReturnType() {
        return value_type.LIST;
    }
}

class operator_double_quote implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets)throws mua_except {
        if(!paras.has_next()) throw new muaParametersMissing();
        String word=paras.nextCurrentWord();
        if(word==null) return new value_word("");
        return new value_word(word);
    }
    @Override
    public value_type getReturnType() {
        return value_type.WORD;
    }
}

class operator_run implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        value_list list = (value_list) operator.getOneValue(paras, name_sets, List.of(value_type.LIST));
        return run(list, name_sets);
    }

    static public value run(value_list cmd,StackedNameSpace name_sets) throws mua_except{
        token_stream list_scanner = new token_list_stream(cmd);
        hash_inter runner = new hash_inter();
        runner.set(list_scanner, name_sets);
        return runner.execute();        
    }

    static public value runFunctions(token t,token_stream paras,StackedNameSpace name_sets) throws mua_except{
        value v = name_sets.get(t.getValue());
        if(v==null) throw new muaNameNotExist(t.getValue());
        if(v instanceof value_list){
            value_list list = (value_list)v;
            if(list.mayBeFunction()){
                return runFunctions(list, paras, name_sets);
            }
            throw new muaTokenTypeMissMatch("runnable list");
        }
        throw new muaTokenTypeMissMatch("runnable list");
    }

    static public value runFunctions(value_list fun,token_stream paras,StackedNameSpace name_sets) throws mua_except{
        StackedNameSpace namespace = new StackedNameSpace();
        // namespace.addSpace(name_sets.getFirstSpace());
        // namespace.addSpace(name_sets.getLastSpace());
        if(fun.env!=null){
            namespace.addSpaces(fun.env);
        }
        namespace.addEmptySpace();

        value_list 
            args=(value_list)(fun.get(0)),
            operations=(value_list)(fun.get(1));

        for(value v:args){
            if(v instanceof value_word){
                value_word name = (value_word)v;
                namespace.set(name.val,operator.getOneValue(paras, name_sets, List.of(value_type.ALL_TYPE)));
            }
            else{
                throw new muaParametersMissing();
            }
        }
        token_stream input = new token_list_stream(operations);
        hash_inter runner = new hash_inter();
        runner.set(input, namespace);
        return  runner.execute();
    }

    @Override
    public value_type getReturnType() {
        return value_type.ALL_TYPE;
    }
}

class operator_erase implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        value_word name = (value_word)operator.getOneValue(paras, name_sets, List.of(value_type.WORD));
        value v = name_sets.get(name.val);
        name_sets.remove(name.val);
        return v;
    }
    @Override
    public value_type getReturnType() {
        return value_type.ALL_TYPE;
    }
}

class operator_isname implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        value_word word = (value_word)operator.getOneValue(paras, name_sets, List.of(value_type.WORD));
        if(name_sets.contains(word.val)) return new value_bool(true);
        // return new value_bool(opt_pool.match(word.val));
        return new value_bool(false);
    }
    @Override
    public value_type getReturnType() {
        return value_type.BOOL;
    }
}

abstract class operator_compare implements operator{
    protected
    int compare(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        ArrayList<value> values = operator.getValues(paras, name_sets,List.of(value_type.ALL_TYPE,value_type.ALL_TYPE));
        value a=values.get(0);
        value b=values.get(1);
        if(a.getClass().equals(b.getClass())){
            if(a instanceof value_number){
                return ((value_number)a).compareTo((value_number)b);
            }
            else if(a instanceof value_word){
                return ((value_word)a).compareTo((value_word)b);
            }
        }
        if(a instanceof value_number && b instanceof value_word){
            return ((value_number)a).compareTo(new value_number(b.toString()));
        }
        if(b instanceof value_number && a instanceof value_word){
            return (new value_number(a.toString())).compareTo((value_number)b);
        }
        throw new muaValueTypeMissMatch();
    }
    @Override
    public value_type getReturnType() {
        return value_type.BOOL;
    }
}


class operator_eq extends operator_compare{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        try{
            return new value_bool(compare(paras, name_sets) == 0);
        }
        catch(mua_except ex){
            return new value_bool(false);
        }
    }
}

class operator_lt extends operator_compare{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        return new value_bool(compare(paras, name_sets) < 0);
    }
}

class operator_gt extends operator_compare{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        return new value_bool(compare(paras, name_sets) > 0);
    }
}

abstract class operator_logic implements operator{
    public boolean[] logic_operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        ArrayList<value> values = operator.getValues(paras, name_sets,List.of(value_type.BOOL,value_type.BOOL));
        boolean [] result = new boolean[2];
        result[0] = ((value_bool)(values.get(0))).val;
        result[1] = ((value_bool)(values.get(1))).val;
        return result;
    }
    @Override
    public value_type getReturnType() {
        return value_type.BOOL;
    }
}

class operator_and extends operator_logic{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        boolean[] op = logic_operate(paras, name_sets);
        return new value_bool(op[0]&op[1]);
    }
}

class operator_or extends operator_logic{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        boolean[] op = logic_operate(paras, name_sets);
        return new value_bool(op[0]|op[1]);
    }
}

class operator_not implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        value_bool operand = (value_bool) operator.getOneValue(paras, name_sets,List.of(value_type.BOOL));
        return new value_bool(!(operand.val));
    }
    @Override
    public value_type getReturnType() {
        return value_type.BOOL;
    }
}

class operator_if implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        ArrayList<value> values=operator.getValues(paras, name_sets,List.of(value_type.BOOL,value_type.LIST,value_type.LIST));
        if(((value_bool)(values.get(0))).val){
            return operator_run.run(((value_list)(values.get(1))), name_sets);
        }
        return operator_run.run(((value_list)(values.get(2))), name_sets);
    }
    @Override
    public value_type getReturnType() {
        return value_type.ALL_TYPE;
    }
}

abstract class operator_is implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        return operator.getOneValue(paras, name_sets, List.of(value_type.ALL_TYPE));
    }
    @Override
    public value_type getReturnType() {
        return value_type.BOOL;
    }
}

class operate_isnumber extends operator_is{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        value v=super.operate(paras, name_sets);
        boolean flag=false;
        if(v instanceof value_number ||  v instanceof value_word && ((value_word)v).canBeNumber()){
            flag=true;
        }
        return new value_bool(flag);
    }
}

class operate_isword extends operator_is{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        value v=super.operate(paras, name_sets);
        return new value_bool(v instanceof value_word);
    }
}

class operate_isbool extends operator_is{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        value v=super.operate(paras, name_sets);
        boolean flag=false;
        if(v instanceof value_bool ||  v instanceof value_word && ((value_word)v).canBeBool()){
            flag=true;
        }
        return new value_bool(flag);
    }
}

class operate_islist extends operator_is{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        value v=super.operate(paras, name_sets);
        return new value_bool(v instanceof value_list);
    }
}

class operate_isempty extends operator_is{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        value v=super.operate(paras, name_sets);
        if(v instanceof value_word) return new value_bool(((value_word)v).val.length()==0);
        else if(v instanceof value_list) return new value_bool(((value_list)v).size()==0);
        throw new muaValueTypeMissMatch();
    }
}

interface MustReturn{}

class operate_return implements operator,MustReturn{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        value v=operator.getOneValue(paras, name_sets, List.of(value_type.ALL_TYPE));
        // if(v instanceof value_list){
            
        // }
        return v;
    }
    @Override
    public value_type getReturnType() {
        return value_type.ALL_TYPE;
    }
}

class operate_export implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        value_word name = (value_word)(operator.getOneValue(paras, name_sets, List.of(value_type.WORD)));
        String name_string=name.val;
        value v = name_sets.getLastSpace().get(name_string);
        if(v==null){throw new muaNameNotExist(name_string);}
        name_sets.export(name_string, v);
        return v;
    }
    @Override
    public value_type getReturnType() {
        return value_type.ALL_TYPE;
    }
}

class operate_readlist implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        value_list list = new value_list();
        boolean start = false;
        String str=null;
        int bracketsStack=0;
        while(commonInput.input.hasNext()){
            str=commonInput.input.next();
            if(!start){
                if(str.charAt(0)!='['){
                    throw new muaTokenTypeMissMatch("list");
                }
                start=true;

                bracketsStack++;
                if(str.length()==1) continue;
                str=str.substring(1);
            }
            bracketsStack+=charCountsInString(str, '[');
            bracketsStack-=charCountsInString(str, ']');
            if(bracketsStack==0){
                if(str.charAt(str.length()-1)!=']'){
                    throw new muaTokenTypeMissMatch("list");
                }
                start=false;
                if(str.length()>1){
                    list.add(new value_word(str.substring(0, str.length()-1)));
                }
                break;
            }
            list.add(new value_word(str));
        }
        if(start==true){
            throw new muaParametersMissing();
        }
        return list;
    }

    int charCountsInString(String str,char c){
        int cnt=0;
        for(int i=0;i<str.length();i++){
            if(str.charAt(i)==c){
                cnt++;
            }
        }
        return cnt;
    }

    @Override
    public value_type getReturnType() {
        return value_type.LIST;
    }
}

class operator_word implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        ArrayList<value> value_list=
            operator.getValues(paras, name_sets,List.of(value_type.WORD,value_type.WORD));
        value_word w1=(value_word)value_list.get(0);
        value_word w2=(value_word)value_list.get(1);
        return new value_word(w1.val+w2.val);
    }

    @Override
    public value_type getReturnType() {
        return value_type.WORD;
    }
}

class operator_sentence implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        ArrayList<value> vList=
            operator.getValues(paras, name_sets,List.of(value_type.ALL_TYPE,value_type.ALL_TYPE));
        value_list list = new value_list();
        for(value v:vList){
            if(v instanceof value_list){
                value_list vl = (value_list) v;
                for(value e:vl){
                    list.add(e);
                }
            }
            else{
                list.add(v);
            }
        }
        list.env=name_sets;
        return list;
    }

    @Override
    public value_type getReturnType() {
        return value_type.LIST;
    }
}

class operator_list implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        ArrayList<value> vList=
            operator.getValues(paras, name_sets,List.of(value_type.ALL_TYPE,value_type.ALL_TYPE));
        value_list list = new value_list();
        for(value v:vList){
            list.add(v);
        }
        list.env=name_sets;
        return list;
    }   
    @Override
    public value_type getReturnType() {
        return value_type.LIST;
    }
}

class operator_join implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        ArrayList<value> vList=
            operator.getValues(paras, name_sets,List.of(value_type.LIST,value_type.ALL_TYPE));
        value_list list = (value_list)vList.get(0).lightClone();
        list.add(vList.get(1));
        list.env=name_sets;
        return list;
    }   
    @Override
    public value_type getReturnType() {
        return value_type.LIST;
    }
}

abstract class operator_getByIndex implements operator{

    void addEnvIfList(value v,StackedNameSpace namespace){
        if(v instanceof value_list){
            value_list vList=(value_list)v;
            vList.env=namespace;
        }
    }

    public value get(token_stream paras, StackedNameSpace name_sets,int index) throws mua_except {
        value v=
            operator.getOneValue(paras, name_sets,List.of(value_type.LIST,value_type.WORD));
        value_sequence vs= (value_sequence) v;
        if(index<0) index += vs.getSize();
        value rslt=vs.get(index);
        addEnvIfList(rslt, name_sets);
        return rslt;
    }

    public value getSubSequence(token_stream paras, StackedNameSpace name_sets,int start,int end) throws mua_except {
        value v=
            operator.getOneValue(paras, name_sets,List.of(value_type.LIST,value_type.WORD));
        value_sequence vs= (value_sequence) v;
        if(end<0){
            end+=vs.getSize()+1;
        }
        value rslt=vs.getSubSequence(start, end);
        addEnvIfList(rslt, name_sets);
        return rslt;
    }

    @Override
    public value_type getReturnType() {
        return value_type.LIST;
    }
}

class operator_first extends operator_getByIndex{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        return get(paras, name_sets, 0);
    }   
}

class operator_last extends operator_getByIndex{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        return get(paras, name_sets, -1);
    }
}

class operator_butfirst extends operator_getByIndex{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        return getSubSequence(paras, name_sets, 1, -1);
    }
}

class operator_butlast extends operator_getByIndex{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        return getSubSequence(paras, name_sets, 0, -2);
    }
}

abstract class operator_unaryArithmetic implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        value_number v = (value_number) operator.getOneValue(paras, name_sets, List.of(value_type.NUMBER));
        return new value_number(calculate(v.val));
    }
    @Override
    public value_type getReturnType() {
        return value_type.NUMBER;
    }
    public abstract double calculate(double num);
}

abstract class operator_unaryOperator implements operator{
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        value v = operator.getOneValue(paras, name_sets, List.of(getWantedType()));
        return operate(v,name_sets);
    }
    public abstract value_type getWantedType();
    public abstract value operate(value v, StackedNameSpace name_sets) throws mua_except;
}

class operator_save extends operator_unaryOperator{
    @Override
    public value_type getReturnType() {
        return value_type.WORD;
    }
    @Override
    public value_type getWantedType() {
        return value_type.WORD;
    }
    @Override
    public value operate(value v, StackedNameSpace name_sets) throws mua_except{
        value_word fileWord = (value_word)v;
        try{
            PrintWriter writer = new PrintWriter(new File(fileWord.val));
            name_sets.getFirstSpace().forEach(
                (String name,value val)->{
                    writer.print("make \""+name+" ");
                    if(val instanceof value_word){
                        writer.print("\"");
                    }
                    writer.println(val);
                }
            );
            writer.close();
        }
        catch(FileNotFoundException ex){
            System.out.println("file cannot open");
        }
        return v;
    }
}

class operator_load extends operator_unaryOperator{
    @Override
    public value_type getReturnType() {
        return value_type.WORD;
    }
    @Override
    public value_type getWantedType() {
        return value_type.WORD;
    }
    @Override
    public value operate(value v, StackedNameSpace name_sets)throws mua_except{
        value_word fileWord = (value_word)v;
        try{
            Scanner reader = new Scanner(new File(fileWord.val));
            token_input_stream inputStream = new token_input_stream(reader);
            hash_inter hi = new hash_inter();
            hi.set(inputStream, name_sets);
            hi.execute();
        }
        catch(FileNotFoundException ex){
            System.out.println("file cannot open");
        }
        return v;
    }
}

class operator_erall implements operator{
    @Override
    public value_type getReturnType() {
        return value_type.BOOL;
    }
    @Override
    public value operate(token_stream paras, StackedNameSpace name_sets) throws mua_except {
        name_sets.clear();
        return new value_bool(true);
    }
}

class operator_random extends operator_unaryArithmetic{
    @Override
    public double calculate(double num) {
        return Math.random()*num;
    }
}

class operator_sqrt extends operator_unaryArithmetic{
    @Override
    public double calculate(double num) {
        return Math.sqrt(num);
    }
}

class operator_int extends operator_unaryArithmetic{
    @Override
    public double calculate(double num) {
        return Math.floor(num);
    }
}