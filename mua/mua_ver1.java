package mua;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class mua_ver1 implements app{
    @Override
    public void run(String []args){
        Scanner input=null;
        if(args==null || args.length<=0){
            input=commonInput.input;
        }
        else{
            String filename=args[0];
            File file = new File(filename);

            try{
                input=new Scanner(file);
            }
            catch(FileNotFoundException ex){
                System.out.printf("can not open file %s\n",filename);
                return;
            }
        }

        interpreter parser = new hash_inter();
        parser.execute(input);
    }
}
