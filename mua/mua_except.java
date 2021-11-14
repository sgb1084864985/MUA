package mua;

public class mua_except extends Exception{
    
}

class muaValueTypeMissMatch extends mua_except{

}

class muaTokenTypeMissMatch extends mua_except{
    String type;
    muaTokenTypeMissMatch(){}
    muaTokenTypeMissMatch(String type){
        this.type=type;
    }
    @Override
    public String toString() {
        return String.format("The type should be %s",type);
    }
}

class muaNameNotExist extends mua_except{
    String need_name;
    muaNameNotExist(String name){
        need_name=name;
    }

    @Override
    public String toString() {
        return String.format("name %s not exists",need_name);
    }    
}

class muaDivideByZero extends mua_except{

}

class muaParametersMissing extends mua_except{
    String para_type="some_type";
    muaParametersMissing(){}
    muaParametersMissing(String type){
        para_type=type;
    }
    @Override
    public String toString() {
        return String.format("need parameter of %s",para_type);
    }
}

class muaTooMuchWords extends mua_except{
    
}

class muaUnreachableException extends mua_except{

}