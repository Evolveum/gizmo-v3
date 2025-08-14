package com.evolveum.gizmo.component.modal;

import com.evolveum.gizmo.data.provider.SummaryPartsDataProvider;
import com.evolveum.gizmo.dto.PartSummary;
import com.evolveum.gizmo.dto.ReportFilterDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.wicket.model.IModel;

import java.text.DecimalFormat;
import java.util.List;

public class DownloadProjectReportConfigPanel extends AbstractExcelDownloadPanel {

    public DownloadProjectReportConfigPanel(String id, IModel<ReportFilterDto> model) {
        super(id, model);
    }

    @Override protected String filePrefix() { return "part-summary"; }

    @Override protected String contextSuffix(ReportFilterDto f) {
        return f.getCustomerProjectPartDtos().size() == 1
                ? slug(f.getCustomerProjectPartDtos().getFirst().getProjectName())
                : "";
    }

    @Override protected boolean supportsPerUser() { return false; }

    @Override
    protected void generateWorkbook(XSSFWorkbook wb, ReportFilterDto filter, boolean perUser) throws Exception {
        XSSFSheet sheet = getSheet(wb, "Project summary");
        CellStyle header = headerStyle(wb);
        CellStyle text = textStyle(wb);

        int rowIdx = 0;
        XSSFRow head = sheet.createRow(rowIdx++);
        createHeaderCell(head, 0, "User", header);
        createHeaderCell(head, 1, "Part", header);
        createHeaderCell(head, 2, "Work", header);
        createHeaderCell(head, 3, "Invoice", header);

        SummaryPartsDataProvider provider = new SummaryPartsDataProvider(getPageTemplate());
        List<PartSummary> rows = provider.createSummary(filter);

        double sumWork = 0d;
        double sumInvoice = 0d;

        for (PartSummary s : rows) {
            XSSFRow r = sheet.createRow(rowIdx++);
            createTextCell(r, 0, s.getFullName(), text);
            createTextCell(r, 1, s.getName(), text);

            double allocation = s.getUserAllocation();
            double workH = s.getLength();
            double invoiceH = s.getInvoice();
            sumWork += workH; sumInvoice += invoiceH;

            createTextCell(r, 2, formatLength(workH, allocation), text);
            createTextCell(r, 3, formatLength(invoiceH, allocation), text);
        }

        CellStyle sumStyle = headerStyle(wb);
        XSSFRow sum = sheet.createRow(rowIdx);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 1));

        XSSFCell label = sum.createCell(0, CellType.STRING);
        label.setCellValue("Summary");
        label.setCellStyle(sumStyle);

        XSSFCell sumWorkCell = sum.createCell(2, CellType.STRING);
        sumWorkCell.setCellValue(formatLength(sumWork, 1));
        sumWorkCell.setCellStyle(sumStyle);

        XSSFCell sumInvoiceCell = sum.createCell(3, CellType.STRING);
        sumInvoiceCell.setCellValue(formatLength(sumInvoice, 1));
        sumInvoiceCell.setCellStyle(sumStyle);

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
