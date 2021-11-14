package mua;

import java.util.Scanner;

public class token_input_stream extends token_stream{
    Scanner input;
    token_input_stream(Scanner input){
        this.input=input;
    }
    @Override
    boolean has_next() {
        return (current_word!=null)||input.hasNext();
    }

    @Override
    String nextBlock() {
        return input.next();
    }
}
