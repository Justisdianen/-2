package com.example.ruoyi.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel导入导出工具类
 */
public class ExcelUtil {

    /**
     * 导出Excel
     */
    public static <T> byte[] exportExcel(List<T> list, Class<T> clazz, String sheetName) throws IOException, IllegalAccessException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        // 创建标题行
        Row headerRow = sheet.createRow(0);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(fields[i].getName());
        }

        // 创建数据行
        for (int i = 0; i < list.size(); i++) {
            Row row = sheet.createRow(i + 1);
            T obj = list.get(i);
            for (int j = 0; j < fields.length; j++) {
                Cell cell = row.createCell(j);
                fields[j].setAccessible(true);
                Object value = fields[j].get(obj);
                setCellValue(cell, value);
            }
        }

        // 自动调整列宽
        for (int i = 0; i < fields.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    /**
     * 导入Excel
     */
    public static <T> List<T> importExcel(InputStream inputStream, Class<T> clazz) throws IOException, IllegalAccessException, InstantiationException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        List<T> list = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                T obj = clazz.newInstance();
                for (int j = 0; j < fields.length; j++) {
                    fields[j].setAccessible(true);
                    Cell cell = row.getCell(j);
                    Object value = getCellValue(cell, fields[j].getType());
                    fields[j].set(obj, value);
                }
                list.add(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        workbook.close();
        return list;
    }

    /**
     * 设置单元格值
     */
    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue(((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 获取单元格值
     */
    private static Object getCellValue(Cell cell, Class<?> type) {
        if (cell == null) return null;

        CellType cellType = cell.getCellType();
        if (cellType == CellType.BLANK) return null;

        String strValue = "";
        if (cellType == CellType.STRING) {
            strValue = cell.getStringCellValue();
        } else if (cellType == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                strValue = cell.getLocalDateTimeCellValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } else {
                strValue = String.valueOf(cell.getNumericCellValue());
            }
        } else if (cellType == CellType.BOOLEAN) {
            strValue = String.valueOf(cell.getBooleanCellValue());
        } else if (cellType == CellType.FORMULA) {
            strValue = cell.getCellFormula();
        }

        // 类型转换
        if (type == String.class) return strValue;
        if (type == Integer.class || type == int.class) return Integer.parseInt(strValue.split("\\.")[0]);
        if (type == Long.class || type == long.class) return Long.parseLong(strValue.split("\\.")[0]);
        if (type == Double.class || type == double.class) return Double.parseDouble(strValue);
        if (type == Boolean.class || type == boolean.class) return Boolean.parseBoolean(strValue);

        return strValue;
    }
}