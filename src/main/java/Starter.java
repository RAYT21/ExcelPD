import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;

public class Starter {

    public static void ma(String[] args) throws IOException, InvalidFormatException {
        if (args.length != 2) {
            System.out.println("//---// Wrong number arguments //---//");
        }
        else {
            if (args[1].substring(args[1].lastIndexOf(".")+1).equals("xls") && new File(args[1]).exists()){
                if (args[0].equals("masking")){
                    PDWorker.maskingPD(args[1], args[2]);
                }
                else if (args[0].equals("demasking")){
                    PDWorker.demaskingPD(args[1],args[2]);
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


    public static void main(String[] args){
        String password = "14235";
        String[] hashCode = Integer.toString(password.hashCode()).split("");
        String str = "28.07.2001";
        String newPassport = moveDate(str, hashCode);
        System.out.println(newPassport);
        String Passport = demoveDate(newPassport, hashCode);
        System.out.println(Passport);


    }



    private static String moveDate(String str, String[] hashCode){
        int day = Integer.parseInt(str.substring(0,2))+Integer.parseInt(hashCode[0]);
        int month= (Integer.parseInt(str.substring(3,5))+Integer.parseInt(hashCode[1]));
        System.out.println(Integer.parseInt(hashCode[1]));
        int year= (Integer.parseInt(str.substring(6,10))+Integer.parseInt(hashCode[2]));
        if (month > 12){
            month%=12;
        }
        if (month %2 != 0 && day > 30 ){
            day%=30;
        }
        else{
            day%=32;
        }
        String newDate ="";
        if (day < 10){
            newDate+= "0"+day+".";
        }
        else {
            newDate+= day+".";
        }
        if (month < 10) {
            newDate+= "0"+month+".";
        }
        else{
            newDate+= month+".";
        }
        return newDate+year;
    }

    private static String demoveDate(String str, String[] hashCode){
        int day = Integer.parseInt(str.substring(0,2))-Integer.parseInt(hashCode[0]);
        int month= (Integer.parseInt(str.substring(3,5))-Integer.parseInt(hashCode[1]));
        int year= (Integer.parseInt(str.substring(6,10))-Integer.parseInt(hashCode[2]));
        if(month <= 0){
            month+= (12 +Integer.parseInt(hashCode[1]));
        }
        else{

        }
        if(day < 0 && month % 2 != 0){
            day += 31 + Integer.parseInt(hashCode[0]);
        }
        else if (day < 0 && month % 2 == 0){
            day += 30 + Integer.parseInt(hashCode[0]);
        }
        String newDate ="";
        if (day < 10){
            newDate+= "0"+day+".";
        }
        else {
            newDate+= day+".";
        }
        if (month < 10) {
            newDate+= "0"+month+".";
        }
        else{
            newDate+= month+".";
        }
        return newDate+year;
    }
}
