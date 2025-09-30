package com.evolveum.gizmo.component.modal;

import com.evolveum.gizmo.data.provider.SummaryUserDataProvider;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.UserSummary;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.wicket.model.IModel;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

public class DownloadTimeoffReportConfigPanel extends AbstractExcelDownloadPanel {

    public DownloadTimeoffReportConfigPanel(String id, IModel<ReportFilterDto> model) {
        super(id, model);
    }

    @Override protected String filePrefix() { return "user-summary"; }

    @Override protected String contextSuffix(ReportFilterDto f) {
        return f.getRealizators().size() == 1 ? slug(f.getRealizators().getFirst().getFamilyName()) : "";
    }

    @Override protected boolean supportsPerUser() { return false; }

    @Override
    protected void generateWorkbook(XSSFWorkbook wb, ReportFilterDto filter) {
        XSSFSheet sheet = getSheet(wb, "User summary");
        CellStyle header = headerStyle(wb);
        CellStyle dateStyle = dateStyle(wb);
        CellStyle textStyle = textStyle(wb);

        int rowIdx = 0;
        XSSFRow head = sheet.createRow(rowIdx++);
        createHeaderCell(head, 0, "User", header);
        createHeaderCell(head, 1, "Last report", header);
        createHeaderCell(head, 2, "Work", header);
        createHeaderCell(head, 3, "Time off", header);

        SummaryUserDataProvider provider = new SummaryUserDataProvider(getPageTemplate());
        List<UserSummary> rows = provider.createSummary(filter);

        for (UserSummary s : rows) {
            XSSFRow r = sheet.createRow(rowIdx++);
            createTextCell(r, 0, s.getFullName(), textStyle);

            LocalDate max = s.getMaxDate();
            XSSFCell c = r.createCell(1, CellType.NUMERIC);
            c.setCellValue(java.sql.Date.valueOf(max));
            c.setCellStyle(dateStyle);

            double allocation = s.getUserAllocation();
            createTextCell(r, 2, formatLength(s.getWork(), allocation), textStyle);
            createTextCell(r, 3, formatLength(s.getTimeOff(), allocation), textStyle);
        }

        for (int c = 0; c <= 3; c++) sheet.autoSizeColumn(c);
    }

    private void createHeaderCell(XSSFRow row, int col, String text, CellStyle style) {
        XSSFCell cell = row.createCell(col, CellType.STRING);
        cell.setCellValue(text);
        cell.setCellStyle(style);
    }

    private void createTextCell(XSSFRow row, int col, String text, CellStyle style) {
        XSSFCell cell = row.createCell(col, CellType.STRING);
        cell.setCellValue(text != null ? text : "");
        cell.setCellStyle(style);
    }

    private String formatLength(Double hours, double allocation) {
        if (hours == null) hours = 0d;
        double days = allocation == 0 ? 0 : hours / (8 * allocation);
        DecimalFormat twoDForm = new java.text.DecimalFormat("#.##");
        return hours + " (" + twoDForm.format(days) + "d)";
    }
}
