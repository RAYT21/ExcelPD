import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;

public class Starter {

    public static void main(String[] args) throws IOException, InvalidFormatException {
        if (args.length != 2) {
            System.out.println("//---// Wrong number arguments //---//");
        }
        else {
            if (args[1].substring(args[1].lastIndexOf(".")+1).equals("xls") && new File(args[1]).exists()){
                if (args[0].equals("masking")){
                    PDWorker.maskingPD(args[1]);
                }
                else if (args[0].equals("demasking")){
                    PDWorker.demaskingPD(args[1]);
                }
                else {
                    System.out.println("//---// Incorrect 1'st argument //---//");
                }
            }
            else{
                System.out.println("//---// File not exist or incorrect format //---//");
            }
        }
    }
}
