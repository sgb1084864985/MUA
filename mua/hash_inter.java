package mua;

import java.util.Scanner;

public class hash_inter implements interpreter{
    public value execute(token_stream stream,StackedNameSpace namespace) throws mua_except{
        this.words_=stream;
        if(namespace==null){
            name_set=new StackedNameSpace();
            name_set.addEmptySpace();
        }
        else{
            name_set=namespace.limitedCloneWithNewSpace();
        }
        return execute();
    }

    public void set(token_stream stream,StackedNameSpace namespace){
        words_=stream;
        if(namespace!=null)
            name_set=namespace;
    }

    public value execute() throws mua_except{
        value v=new value_void();
        while(words_.has_next()){
            token t= words_.nextToken();
            switch(t.getType()){
                case OPERATOR:
                    operator op=opt_pool.get(t.getValue());
                    v=op.operate(words_, name_set);
                    if(op instanceof MustReturn){
                        return v;
                    }
                    break;
                case IDENTIFIER:
                    v=operator_run.runFunctions(t, words_, name_set);
                    break;
                default: 
                    v=words_.toValue(t, name_set);
                    break;
            }
        }
        return v;
    }

    @Override
    public void execute(Scanner input){
        try{
            execute(new token_input_stream(input),null);
        }
        catch(mua_except ex){
            ex.printStackTrace();
        }
    }

    // hash_inter(){

    // }

    // members
    token_stream words_;
    StackedNameSpace name_set;
    // StackedNameSpace name_set=new HashMap<>();
}
