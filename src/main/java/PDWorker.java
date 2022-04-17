import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import java.io.*;

public class PDWorker {

    public static void maskingPD(String path) throws IOException, InvalidFormatException {
        String[][] table = FileManager.reader(path);
        MaskMatcher.mask(table);
        String newFilePath = path.substring(0,path.lastIndexOf("\\"))+"\\mask_table.xls";
        FileManager.saver(table, newFilePath);
    }

    public static void demaskingPD(String path) throws IOException, InvalidFormatException {
        String[][] table = FileManager.reader(path);
        DemaskMatcher.demask(table);
        String newFilePath = path.substring(0,path.lastIndexOf("\\"))+"\\demask_table.xls";
        FileManager.saver(table, newFilePath);
    }
}

class FileManager {

    public static String[][] reader(String fileName) throws IOException, InvalidFormatException {
        Workbook workbook = new HSSFWorkbook(new POIFSFileSystem(new FileInputStream(fileName)));
        Sheet sheet = workbook.getSheetAt(0);
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
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("New List1");

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

//дописать преобразователи строк в метчеры
class MaskMatcher{

    protected static int func(int i, int m,int n){ //позволяет определить сдвиг относительно столбца
        return (int)Math.pow(-1,i)*(m%n)*i;
    }

    public static void mask(String[][] tmp){
        columnMixer(tmp);// мешаем колонны
        for (int i = 0; i < tmp.length; i++) {
            rowCellMixer(tmp[i]);//мешаем ячейки в зависимости от типа
        }
    }

    private static void columnMixer(String[][] table){
        int n = table.length;//y length
        int m = table[0].length;//x length
        for (int j = 0; j < m; j++) {
            int k = func(j,m,n);
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


    private static void rowCellMixer(String[] str){
        for (int i = 0; i < str.length; i++) {
            if(isPhone(str[i])){
                movePhone(str[i]);
                continue;
            }
            if(isCard(str[i])){
                moveCard(str[i]);
                continue;
            }
            if(isDate(str[i])){
                moveDate(str[i]);
                continue;
            }
            if(isPassport(str[i])){
                movePassport(str[i]);
                continue;
            }
            if(isMail(str[i])){
                moveMail(str[i]);
                continue;
            }
        }
    }


    protected static boolean isPhone(String str){
        if(str.matches("^((\\+7|7|8)+([0-9]){10})$")) return true;
        else return false;
    }

    private static String movePhone(String str){
        return str;
    }

    protected static boolean isMail(String str){
        if(str.matches("^[-\\w.]+@([A-z0-9][-A-z0-9]+\\.)+[A-z]{2,4}$")) return true;
        else return false;
    }

    private static String moveMail(String str){
        return str;
    }

    protected static boolean isDate(String str){
        if(str.matches("(0?[1-9]|[12][0-9]|3[01]).(0?[1-9]|1[012]).((19|20)\\d\\d)")) return true;
        else return false;
    }

    private static String moveDate(String str){
        return str;
    }

    protected static boolean isPassport(String str){
        if(str.matches("^([0-9]{4}\\s{1}[0-9]{6})?$")) return true;
        else return false;
    }

    private static String movePassport(String str){
        return str;
    }

    protected static boolean isCard(String str){
        if(str.matches("^([0-9]{4}\\s{1}[0-9]{4}\\s{1}[0-9]{4}\\s{1}[0-9]{4})?$")) return true;
        else return false;
    }

    private static String moveCard(String str){
        return str;
    }

    protected static void moveColumnUp(String[][] table, int j){
        String temp= table[0][j];//сохраняем первый индекс
        int n = table.length;//y length
        for (int i = n - 1; i>0; i--)
            table[i-1][j]= table[i][j];//двигаемся снизу вверх и сдвигаем все вниз
        table[n-1][j]= temp;//устанавливаем на последнее место первый индекс
    }

    protected static void moveColumnDown(String[][] table, int j){
        int n = table.length;//y length
        String temp= table[n-1][j];//сохраняем последний индекс
        for (int i = n - 1; i>0; i--)
            table[i][j]= table[i-1][j];//двигаемся снизу вверх и сдвигаем все вниз
        table[0][j]= temp;//устанавливаем на первое место последний индекс
    }
}

class DemaskMatcher extends MaskMatcher{

    public static void demask(String[][] table){
        for (int i = 0; i < table.length; i++) {
            rowCellDemixer(table[i]);//мешаем ячейки в зависимости от типа
        }
        columnDemixer(table);// мешаем колонны

    }

    private static void columnDemixer(String[][] table){
        int n = table.length;//y length
        int m = table[0].length;//x length
        for (int j = 0; j < m; j++) {
            int k = func(j,m,n);
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


   private static void rowCellDemixer(String[] str){
        for (int i = 0; i < str.length; i++) {
            if(isPhone(str[i])){
                movePhone(str[i]);
                continue;
            }
            if(isCard(str[i])){
                moveCard(str[i]);
                continue;
            }
            if(isDate(str[i])){
                moveDate(str[i]);
                continue;
            }
            if(isPassport(str[i])){
                movePassport(str[i]);
                continue;
            }
            if(isMail(str[i])){
                moveMail(str[i]);
                continue;
            }
        }
    }

    private static String movePhone(String str){
        return str;
    }

    private static String moveMail(String str){
        return str;
    }

    private static String moveDate(String str){
        return str;
    }

    private static String movePassport(String str){
        return str;
    }

    private static String moveCard(String str){
        return str;
    }
}