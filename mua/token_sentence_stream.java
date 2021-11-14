package mua;

public class token_sentence_stream extends token_stream{
    int i;
    String []words;

    token_sentence_stream(String sentence){
        i=0;
        words=sentence.split("\\s");
        while(has_next()&&words[i].length()==0)i++;
    }

    boolean has_next(){
        return i>=0&&i<words.length;
    }

    String nextBlock(){
        String ret=words[i++];
        while(has_next()&&words[i].length()==0)i++;
        return ret;
    }
}
