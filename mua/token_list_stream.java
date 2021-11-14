package mua;
import java.util.Iterator;

// token_list_stream.java

class token_list_stream extends token_stream{
    Iterator<value> it;
    value_list currentList=null;
    token_list_stream(value_list list){
        it=list.iterator();
    }
    @Override
    boolean has_next() {
        return current_word!=null || currentList!=null || it.hasNext();
    }
    @Override
    String nextBlock() {
        value v = it.next();
        if(v instanceof value_word) return ((value_word)v).val;
        else if (v instanceof value_list){
            currentList=(value_list)v;
        }
        return null;
    }

    @Override
    public token nextToken() {
        if(current_word != null){
            return super.nextToken();
        }
        else{
            current_word = nextBlock();
            if(currentList!=null){
                value tmp = currentList;
                currentList=null;
                return new token(tmp,token_type.LIST);
            }
            return super.nextToken();
        }
    }
}