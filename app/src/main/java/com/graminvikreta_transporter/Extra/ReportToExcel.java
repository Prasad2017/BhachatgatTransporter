package com.graminvikreta_transporter.Extra;

import android.os.Handler;
import android.os.Looper;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportToExcel {

    private static Handler handler = new Handler(Looper.getMainLooper());

    private String mExportPath;
    private HashMap<String, String> mPrettyNameMapping = null;
    private ExportCustomFormatter mCustomFormatter = null;
    private String workbookName = null;
    private List<List<String>> listOfData = null;

    public ReportToExcel(String workBookName, String exportPath) {
        mExportPath = exportPath;
        try {
            workbookName = workBookName;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportData(final String fileName) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(prettyNameMapping(workbookName));
        createSheet(sheet);
        File file = new File(mExportPath, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        workbook.write(fos);
        fos.flush();
        fos.close();
        workbook.close();
    }

    public void exportReport(final List<List<String>> data, final String fileName, ExportListener listener) {
        listOfData = data;
        startExportReport(fileName, listener);
    }

    private void startExportReport(final String fileName, final ExportListener listener) {
        if (listener != null) {
            listener.onStart();
        }
        new Thread(() -> {
            try {
                exportData(fileName);
                if (listener != null) {
                    handler.post(() -> listener.onCompleted(mExportPath + fileName));
                }
            } catch (final Exception e) {
                if (listener != null)
                    handler.post(() -> listener.onError(e));
            }
        }).start();
    }

    private void createSheet(HSSFSheet sheet) {
        HSSFRow rowA = sheet.createRow(0);
        ArrayList<String> columns = new ArrayList<>(listOfData.get(0));
        int cellIndex = 0;
        for (int i = 0; i < columns.size(); i++) {
            String columnName = prettyNameMapping("" + columns.get(i));
            HSSFCell cellA = rowA.createCell(cellIndex);
            cellA.setCellValue(new HSSFRichTextString(columnName));
            cellIndex++;
        }
        insertItemToSheet(sheet, columns);
    }

    private void insertItemToSheet(HSSFSheet sheet, ArrayList<String> columns) {
        sheet.createDrawingPatriarch();
        for (int n = 1; n < listOfData.size(); ++n) {
            HSSFRow rowA = sheet.createRow(n);
            int cellIndex = 0;
            for (int j = 0; j < columns.size(); j++) {
                String columnName = "" + columns.get(j);
                HSSFCell cellA = rowA.createCell(cellIndex);
                String value = listOfData.get(n).get(j);
                if (null != mCustomFormatter) {
                    value = mCustomFormatter.process(columnName, value);
                }
                cellA.setCellValue(new HSSFRichTextString(value));
                cellIndex++;
            }
        }
    }


    private String prettyNameMapping(String name) {
        if (null != mPrettyNameMapping) {
            if (mPrettyNameMapping.containsKey(name)) {
                name = mPrettyNameMapping.get(name);
            }
        }
        return name;
    }

    public interface ExportListener {
        void onStart();

        void onCompleted(String filePath);

        void onError(Exception e);
    }

    /**
     * Interface class for the custom formatter
     */
    public interface ExportCustomFormatter {
        String process(String columnName, String value);
    }
}