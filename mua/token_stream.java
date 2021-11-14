package mua;

// import java.util.HashMap;

public abstract class token_stream {
    String current_word=null;

    abstract boolean has_next();
    abstract String nextBlock();

    public String nextWord(){
        if(current_word==null){
            return nextBlock();
        }
        else{
            return nextCurrentWord();
        }
    }

    public String nextCurrentWord(){
        String tmp=current_word;
        current_word=null;
        return tmp;
    }

    public token nextToken(){
        int j=0;
        if(current_word==null){
            current_word=nextBlock();
        }

        for(;j<current_word.length();j++){
            if(opt_pool.punctuations.indexOf(current_word.charAt(j))!=-1){
                if(j==0){
                    String tmp=current_word.substring(0,1);
                    current_word=current_word.substring(1);
                    if(current_word.length()==0) current_word=null;
                    return new token(tmp);
                }
                else{
                    String tmp=current_word.substring(0,j);
                    current_word=current_word.substring(j);
                    return new token(tmp);
                }
            }
        }

        return new token(nextCurrentWord());
    }

    public token nextTokenInList(){
        int j=0;
        if(current_word==null){
            current_word=nextBlock();
        }
        
        for(;j<current_word.length();j++){
            if("[]".indexOf(current_word.charAt(j))!=-1){
                if(j==0){
                    String tmp=current_word.substring(0,1);
                    current_word=current_word.substring(1);
                    if(current_word.length()==0) current_word=null;
                    return new token(tmp,token_type.OPERATOR);
                }
                else{
                    String tmp=current_word.substring(0,j);
                    current_word=current_word.substring(j);
                    return new token(tmp,token_type.WORD);
                }
            }
        }

        return new token(nextCurrentWord(),token_type.WORD);        
    }

    public value toValue(token t,StackedNameSpace name_sets) throws mua_except{
        if(t.getBoundValue()!=null) return t.getBoundValue();
        switch(t.getType()){
            case NUMBER:
                return new value_number(t.getValue());
            case WORD:
                return new value_word(t.getValue());
            case BOOL:
                return new value_bool(t.getValue());
            case OPERATOR:
                return opt_pool.get(t.getValue()).operate(this,name_sets);
            case IDENTIFIER:
                return operator_run.runFunctions(t, this, name_sets);
            default: return new value_void();
        }
    }

}
