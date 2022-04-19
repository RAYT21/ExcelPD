import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Scanner;

public class Starter {

    public static void main(String[] args) throws IOException, InvalidFormatException {

        Scanner sc = new Scanner(System.in);
        System.out.println("mode(masking/demasking): ");
        String mode =  sc.nextLine();
        if (!mode.equals("masking") && !mode.equals("demasking")){
            System.out.println("Wrong mode");
            System.exit(1);
        }

        System.out.println("Path to file: ");
        String path =  sc.nextLine();
        if(!new File(path).exists()){
            System.out.println("Wrong way");
            System.exit(1);
        }
        if (!path.substring(path.lastIndexOf(".")+1).equals("xlsx")){
            System.out.println("Wrong extension");
            System.exit(1);
        }

        String password = "";

        if (mode.equals("masking")){
            System.out.println("Set a password for masking: ");
            password = sc.nextLine();
            sc.close();
            PDWorker.maskingPD(path, password);
        }
        else {
            System.out.println("Enter password to masking: ");
            password = sc.nextLine();
            sc.close();
            PDWorker.demaskingPD(path, password);

        }
    }
}
