package mua;


import java.util.HashMap;


public class opt_pool {
    static HashMap<String,operator> pool_= new HashMap<>();
    static String punctuations=":[\"]";
    static{
        pool_.put("make",new operator_make());
        pool_.put("print",new operator_print());
        pool_.put("read",new operator_read());
        pool_.put(":",new operator_colon());
        pool_.put("thing",new operator_thing());
        pool_.put("add",new operator_add());
        pool_.put("sub",new operator_sub());
        pool_.put("mul",new operator_mul());
        pool_.put("div",new operator_div());
        pool_.put("mod",new operator_mod());
        pool_.put("\"", new operator_double_quote());
        pool_.put("[", new operator_start_list());
        pool_.put("run", new operator_run());
        pool_.put("erase", new operator_erase());
        pool_.put("isname",new operator_isname());
        pool_.put("eq",new operator_eq());
        pool_.put("lt",new operator_lt());
        pool_.put("gt",new operator_gt());
        pool_.put("and",new operator_and());
        pool_.put("or",new operator_or());
        pool_.put("not",new operator_not());
        pool_.put("if",new operator_if());
        pool_.put("isnumber", new operate_isnumber());
        pool_.put("isword", new operate_isword());
        pool_.put("islist", new operate_islist());
        pool_.put("isbool", new operate_isbool());
        pool_.put("isempty", new operate_isempty());
        pool_.put("return", new operate_return());
        pool_.put("export", new operate_export());
        pool_.put("readlist", new operate_readlist());
        pool_.put("word", new operator_word());
        pool_.put("sentence", new operator_sentence());
        pool_.put("list", new operator_list());
        pool_.put("join", new operator_join());
        pool_.put("first", new operator_first());
        pool_.put("last", new operator_first());
        pool_.put("butfirst", new operator_butfirst());
        pool_.put("butlast", new operator_butlast());
        pool_.put("random", new operator_random());
        pool_.put("int", new operator_int());
        pool_.put("sqrt", new operator_sqrt());
        pool_.put("save", new operator_save());
        pool_.put("load", new operator_load());
        pool_.put("erall", new operator_erall());
    }
    static operator get(String opt_name){
        return pool_.get(opt_name);
    }

    static boolean match(String opt_name){
        return pool_.containsKey(opt_name);
    }
}
