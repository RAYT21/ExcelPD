import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class PDWorker {

    public static void maskingPD(String path, String password) throws IOException, InvalidFormatException {
        String[][] table = FileManager.reader(path);
        System.out.println("Input data:");
        for (int i = 0; i < table.length; i++) {
            System.out.printf("// ");
            for (int j = 0; j < table[i].length; j++) {
                System.out.printf(table[i][j] + " ");
            }
            System.out.printf("//");
            System.out.println(" ");
        }
        String[] hashCode = Integer.toString(password.hashCode()).split("");
        MaskMatcher.mask(table, hashCode);
        String newFilePath = path.substring(0,path.lastIndexOf("\\"))+"\\mask_table.xlsx";
        System.out.println("\n\n\n\nOutput data:");
        for (int i = 0; i < table.length; i++) {
            System.out.printf("// ");
            for (int j = 0; j < table[i].length; j++) {
                System.out.printf(table[i][j] + " ");
            }
            System.out.printf("//");
            System.out.println(" ");
        }
        FileManager.saver(table, newFilePath);
    }

    public static void demaskingPD(String path, String password) throws IOException, InvalidFormatException {
        String[][] table = FileManager.reader(path);
        String[] hashCode = Integer.toString(password.hashCode()).split("");
        System.out.println("Input data:");
        for (int i = 0; i < table.length; i++) {
            System.out.printf("// ");
            for (int j = 0; j < table[i].length; j++) {
                System.out.printf(table[i][j] + " ");
            }
            System.out.printf("//");
            System.out.println(" ");
        }
        DemaskMatcher.demask(table, hashCode);
        String newFilePath = path.substring(0,path.lastIndexOf("\\"))+"\\demask_table.xlsx";
        System.out.println("\n\n\n\nOutput data:");
        for (int i = 0; i < table.length; i++) {
            System.out.printf("// ");
            for (int j = 0; j < table[i].length; j++) {
                System.out.printf(table[i][j] + " ");
            }
            System.out.printf("//");
            System.out.println(" ");
        }
        FileManager.saver(table, newFilePath);
    }
}

class FileManager {

    public static String[][] reader(String fileName) throws IOException, InvalidFormatException {

        FileInputStream inputStream = new FileInputStream(new File(fileName));
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int nbLine = sheet.getLastRowNum() + 1;
        int nbCol = sheet.getRow(0).getLastCellNum();
        String[][] table = new String[nbLine][nbCol];
        for (int i = 0; i < nbLine; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < nbCol; j++) {
                Cell cell = row.getCell(j);
                table[i][j] = cell.toString();
            }
        }
        return  table;
    }

    public static void saver(String[][] table, String fileName) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("New List1");

        for (int i = 0; i < table.length; i++) {
            Row row = sheet.createRow(i);
            String[] rowData = table[i];
            for (int j = 0; j < rowData.length; j++) {
                row.createCell(j).setCellValue(rowData[j]);
            }
        }

        FileOutputStream out = null;
        out = new FileOutputStream(new File(fileName));
        workbook.write(out);
        out.close();
    }
}

class MaskMatcher{

    protected static int func(int i, String[] hashCode){
        int k = i% (hashCode.length - 1);
        return (int)Math.pow(-1,i)*Integer.parseInt(hashCode[k])*(i+1);
    }

    public static void mask(String[][] tmp, String[] hash){
        columnMixer(tmp,hash);
        for (int i = 0; i < tmp.length; i++) {
            rowCellMixer(tmp[i], hash);
        }
    }

    private static void columnMixer(String[][] table, String[] hashCode){
        int n = table.length;
        int m = table[0].length;
        for (int j = 0; j < m; j++) {
            int k = func(j,hashCode);
            for (int l = 0; l < Math.abs(k); l++) {
                if(k > 0){
                    moveColumnUp(table,j);
                }
                else{
                    moveColumnDown(table,j);
                }
            }
        }
    }


    private static void rowCellMixer(String[] str,String[] hash){
        for (int i = 0; i < str.length; i++) {
            if(isPhone(str[i])){
                str[i] = movePhone(str[i], hash);
                continue;
            }
            if(isCard(str[i])){
                str[i] = moveCard(str[i], hash);
                continue;
            }
            if(isDate(str[i])){
                str[i] = moveDate(str[i], hash);
                continue;
            }
            if(isPassport(str[i])){
                str[i] = movePassport(str[i], hash);
                continue;
            }
            if(isMail(str[i])){
                str[i] = moveMail(str[i], hash);
                continue;
            }
        }
    }


    protected static boolean isPhone(String str){
        if(str.matches("^((\\+7|7|\\+8|8)+([0-9]){10})$")) return true;
        else return false;
    }

    private static String movePhone(String str, String[] hashCode){
        String[] subName = str.substring(str.length()-9).split("");
        String newPhone = "";
        int k=0;
        for (int i = 0 ; i < subName.length; i++){
            if (k > hashCode.length -1) {k=0;}
            newPhone+=(Integer.parseInt(subName[i])+Integer.parseInt(hashCode[k]))%10;
            k++;
        }
        return str.substring(0,str.length()-9)+newPhone;
    }

    protected static boolean isMail(String str){
        if(str.matches("^[/\\S/u]+@([A-z0-9][-A-z0-9]+\\.)+[A-z]{2,4}$")) return true;
        else return false;
    }

    private static String moveMail(String str, String[] hashCode){
        char[] subName = str.substring(0,str.lastIndexOf("@")).toCharArray();

        String newMail = "";
        int k=0;
        for (int i = 0 ; i < subName.length; i++){
            if (k > hashCode.length -1) {k=0;}
            newMail+=(char)((int)subName[i]+Integer.parseInt(hashCode[k]));
            k++;
        }
        return newMail+str.substring(str.lastIndexOf("@"));
    }

    protected static boolean isDate(String str){
        if(str.matches("(0?[1-9]|[12][0-9]|3[01]).(0?[1-9]|1[012]).((19|20)\\d\\d)")) return true;
        else return false;
    }

    private static String moveDate(String str, String[] hashCode){
        System.out.println(hashCode[0]);
        int day = Integer.parseInt(str.substring(0,2))+Integer.parseInt(hashCode[0]);
        int month= (Integer.parseInt(str.substring(3,5))+Integer.parseInt(hashCode[1]));
        int year= (Integer.parseInt(str.substring(6,10))+Integer.parseInt(hashCode[2]));
        if (month > 12){
            month%=12;
        }
        if (month %2 != 0 && day > 30 ){
            day%=31;
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


    protected static boolean isPassport(String str){
        if(str.matches("^([0-9]{4}\\s{1}[0-9]{6})?$")) return true;
        else return false;
    }

    private static String movePassport(String str, String[] hashCode){
        String[] subName = str.split("");
        String newPassport = "";
        int k=0;
        for (int i = 0 ; i < subName.length; i++){
            if (k > hashCode.length -1) {k=0;}
            if(subName[i].equals(" ")) {
                newPassport+=" ";
            }
            else{
                newPassport += (Integer.parseInt(subName[i]) + Integer.parseInt(hashCode[k])) % 10;
                k++;
            }
        }
        return newPassport;
    }

    protected static boolean isCard(String str){
        if(str.matches("^([0-9]{4}\\s{1}[0-9]{4}\\s{1}[0-9]{4}\\s{1}[0-9]{4})?$")) return true;
        else return false;
    }

    private static String moveCard(String str, String[] hashCode){
        String[] subName = str.split("");
        String newCard = "";
        int k=0;
        for (int i = 0 ; i < subName.length; i++){
            if (k > hashCode.length -1) {k=0;}
            if(subName[i].equals(" ")) {
                newCard+=" ";
            }
            else{
                newCard += (Integer.parseInt(subName[i]) + Integer.parseInt(hashCode[k])) % 10;
                k++;
            }
        }
        return newCard;
    }

    protected static void moveColumnUp(String[][] table, int j){
        String temp= table[0][j];
        int n = table.length;
        for (int i = 1 ; i < n; i++)
            table[i-1][j]= table[i][j];
        table[n-1][j]= temp;
    }

    protected static void moveColumnDown(String[][] table, int j){
        int n = table.length;
        String temp= table[n-1][j];
        for (int i = n - 1; i>0; i--)
            table[i][j]= table[i-1][j];
        table[0][j]= temp;
    }
}

class DemaskMatcher extends MaskMatcher{

    public static void demask(String[][] table, String[] hash){
        for (int i = 0; i < table.length; i++) {
            rowCellDemixer(table[i],hash);
        }
        columnDemixer(table, hash);

    }

    private static void columnDemixer(String[][] table, String[] hashCode){
        int n = table.length;
        int m = table[0].length;
        for (int j = 0; j < m; j++) {
            int k = func(j,hashCode);
            for (int l = 0; l < Math.abs(k); l++) {
                if(k < 0){
                    moveColumnUp(table,j);
                }
                else{
                    moveColumnDown(table,j);
                }
            }
        }
    }


   private static void rowCellDemixer(String[] str, String[] hash){
        for (int i = 0; i < str.length; i++) {
            if(isPhone(str[i])){
                str[i] = demovePhone(str[i], hash);
                continue;
            }
            if(isCard(str[i])){
                str[i] = demoveCard(str[i], hash);
                continue;
            }
            if(isDate(str[i])){
                str[i] = demoveDate(str[i], hash);
                continue;
            }
            if(isPassport(str[i])){
                str[i] = demovePassport(str[i], hash);
                continue;
            }
            if(isMail(str[i])){
                str[i] = demoveMail(str[i], hash);
                continue;
            }
        }
    }

    private static String demovePhone(String str, String[] hashCode){
        String[] subName = str.substring(str.length()-9).split("");
        String newPhone = "";
        int k=0;
        for (int i = 0 ; i < subName.length; i++){
            if (k > hashCode.length -1) {k=0;}
            int tmp = Integer.parseInt(subName[i]) - Integer.parseInt(hashCode[k]);
            if (tmp < 0) {
                newPhone += (10 +tmp);
            }
            else{
                newPhone += tmp;
            }
            k++;
        }
        return str.substring(0,str.length()-9)+newPhone;
    }

    private static String demoveMail(String str, String[] hashCode){
        char[] subName = str.substring(0,str.lastIndexOf("@")).toCharArray();

        String newMail = "";
        int k=0;
        for (int i = 0 ; i < subName.length; i++){
            if (k > hashCode.length -1) {k=0;}
            newMail+=(char)((int)subName[i]-Integer.parseInt(hashCode[k]));
            k++;
        }
        return newMail+str.substring(str.lastIndexOf("@"));
    }

    private static String demoveDate(String str, String[] hashCode){
        int day = Integer.parseInt(str.substring(0,2))-Integer.parseInt(hashCode[0]);
        int month= (Integer.parseInt(str.substring(3,5))-Integer.parseInt(hashCode[1]));
        int year= (Integer.parseInt(str.substring(6,10))-Integer.parseInt(hashCode[2]));
        if(month <= 0){
            month= 12 + month;
        }
        else if(month > 12){
            month= month%12;
        }
        if(day < 0 && month % 2 != 0){
            day = 31 + day;
        }
        else if (day < 0 && month % 2 == 0){
            day = 32 + day;
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


    private static String demovePassport(String str, String[] hashCode){
        String[] subName = str.split("");
        String newPassport = "";
        int k=0;
        for (int i = 0 ; i < subName.length; i++){
            if (k > hashCode.length -1) {k=0;}
            if(subName[i].equals(" ")) {
                newPassport+=" ";
            }
            else{
                int tmp = Integer.parseInt(subName[i]) - Integer.parseInt(hashCode[k]);
                if (tmp < 0) {
                    newPassport += (10 +tmp);
                }
                else{
                    newPassport += tmp;
                }
                k++;
            }
        }
        return newPassport;
    }


    private static String demoveCard(String str, String[] hashCode){
        String[] subName = str.split("");
        String newCard = "";
        int k=0;
        for (int i = 0 ; i < subName.length; i++){
            if (k > hashCode.length -1) {k=0;}
            if(subName[i].equals(" ")) {
                newCard+=" ";
            }
            else{
                int tmp = Integer.parseInt(subName[i]) - Integer.parseInt(hashCode[k]);
                if (tmp < 0) {
                    newCard += (10 +tmp);
                }
                else{
                    newCard += tmp;
                }
                k++;
            }
        }
        return newCard;
    }
}