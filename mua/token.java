package mua;

public class token {
    private String value;
    private token_type type;
    private value bound_value=null;

    token(String value){
        this.value=value;
        type=analysis(value);
    }

    token(String value,token_type type){
        this.type=type;
        this.value=value;
    }

    token(value bound_value,token_type type){
        this.bound_value=bound_value;
        this.type=type;
        this.value=null;
    }

    value getBoundValue(){
        return bound_value;
    }
    String getValue(){
        return value;
    }

    token_type getType(){
        return type;
    }

    public boolean isList(){
        return 
            (type==token_type.LIST)||
            (type==token_type.OPERATOR)&&(value.matches("\\["));
    }

    static token_type analysis(String identifier){
        if(opt_pool.match(identifier)) return token_type.OPERATOR;
        else if(identifier.matches("true|false")) return token_type.BOOL;
        else if(identifier.matches("^[_a-zA-Z]\\w*")){
            return token_type.IDENTIFIER;
        }
        else if(identifier.matches("^(-?\\d+)(\\.\\d+)?$")){
            return token_type.NUMBER;
        }
        // else if(identifier.matches("^\".+")){
        //     return token_type.WORD;
        // }
        return token_type.UNKNOWN;
    }
}
