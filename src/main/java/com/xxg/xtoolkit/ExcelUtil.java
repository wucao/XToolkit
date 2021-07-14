package com.xxg.xtoolkit;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class ExcelUtil {

    public static byte[] generateXls(String[][] data) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Workbook wb = new HSSFWorkbook();
            generateExcel(wb, data, outputStream);
            return outputStream.toByteArray();
        }
    }

    public static byte[] generateXlsx(String[][] data) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Workbook wb = new XSSFWorkbook();
            generateExcel(wb, data, outputStream);
            return outputStream.toByteArray();
        }
    }

    public static void generateXls(String[][] data, OutputStream outputStream) throws IOException {
        Workbook wb = new HSSFWorkbook();
        generateExcel(wb, data, outputStream);
    }

    public static void generateXlsx(String[][] data, OutputStream outputStream) throws IOException {
        Workbook wb = new XSSFWorkbook();
        generateExcel(wb, data, outputStream);
    }

    public static void generateXls(String[][] data, File file) throws IOException {
        Workbook wb = new HSSFWorkbook();
        generateExcel(wb, data, file);
    }

    public static void generateXlsx(String[][] data, File file) throws IOException {
        Workbook wb = new XSSFWorkbook();
        generateExcel(wb, data, file);
    }

    public static void generateExcel(Workbook wb, String[][] data, File file) throws IOException {
        try (OutputStream fileOut = new FileOutputStream(file)) {
            generateExcel(wb, data, fileOut);
        }
    }

    public static void generateExcel(Workbook wb, String[][] data, OutputStream outputStream) throws IOException {
        Sheet sheet = wb.createSheet("sheet");

        for (int i = 0; i < data.length; i++) {
            String[] rowData = data[i];
            Row row = sheet.createRow(i);
            for (int j = 0; j < rowData.length; j++) {
                row.createCell(j).setCellValue(rowData[j]);
            }
        }

        wb.write(outputStream);
    }
}
