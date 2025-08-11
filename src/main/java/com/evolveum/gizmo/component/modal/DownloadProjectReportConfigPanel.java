package com.evolveum.gizmo.component.modal;

import com.evolveum.gizmo.component.SimplePanel;
import com.evolveum.gizmo.component.form.EmptyOnChangeAjaxBehavior;
import com.evolveum.gizmo.data.provider.SummaryPartsDataProvider;
import com.evolveum.gizmo.dto.PartSummary;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.util.LoadableModel;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serial;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;


public class DownloadProjectReportConfigPanel extends SimplePanel<ReportFilterDto> {

    private static final String ID_REPORT_NAME = "reportName";

    private IModel<DownloadSettingsDto> downloadModel;

    public DownloadProjectReportConfigPanel(String id, IModel<ReportFilterDto> model) {
        super(id, model);
    }

    @Override
    protected void initLayout() {
        downloadModel = new LoadableModel<>(false) {
            @Override
            protected DownloadSettingsDto load() {
                DownloadSettingsDto s = new DownloadSettingsDto();
                try {
                    Method m = s.getClass().getMethod("setReportName", String.class);
                    m.invoke(s, defaultFileName(getModelObject()));
                } catch (Exception ignore) {}
                return s;
            }
        };

        Form<DownloadSettingsDto> form = new Form<>("form");
        add(form);

        TextField<String> reportName = new TextField<>(ID_REPORT_NAME,
                new PropertyModel<>(downloadModel, DownloadSettingsDto.F_REPORT_NAME));
        reportName.setOutputMarkupId(true);
        reportName.add(new EmptyOnChangeAjaxBehavior());
        form.add(reportName);

        IModel<String> fileNameModel = new IModel<>() {
            @Override public String getObject() {
                try {
                    Method g = downloadModel.getObject().getClass().getMethod("getReportName");
                    Object v = g.invoke(downloadModel.getObject());
                    String name = v != null ? v.toString() : null;
                    return (name == null || name.isBlank()) ? defaultFileName(getModelObject()) : name;
                } catch (Exception e) {
                    return defaultFileName(getModelObject());
                }
            }
        };

        DownloadLink exportExcel = new DownloadLink("export",
                createDownloadReportModel(),
                fileNameModel)
                .setCacheDuration(Duration.ofMillis(0))
                .setDeleteAfterDownload(true);
        form.add(exportExcel);
    }

    private String defaultFileName(ReportFilterDto filter) {
        LocalDate from = filter != null ? filter.getDateFrom() : null;
        LocalDate to = filter != null ? filter.getDateTo() : null;
        String range = (from != null ? from.toString() : "") + "_" + (to != null ? to.toString() : "");
        return ("project-summary-" + range + ".xlsx").replaceAll("__", "_");
    }

    private IModel<File> createDownloadReportModel() {
        return new IModel<>() {
            @Serial private static final long serialVersionUID = 1L;
            @Override
            public File getObject() {
                File tempFile = new File("export.xlsx");
                generateProjectSummaryExcel(tempFile, getModelObject());
                return tempFile;
            }
        };
    }

    private void generateProjectSummaryExcel(File tempFile, ReportFilterDto filter) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = createSheet("Project summary", workbook);

            CellStyle header = createHeaderStyle(workbook);
            CellStyle text = createDefaultCellStyle(workbook);

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

                String userName = s.getFullName();
                createTextCell(r, 0, userName, text);

                String partName = s.getName();
                createTextCell(r, 1, partName, text);

                double allocation = s.getUserAllocation();
                double workH = s.getLength();
                double invoiceH = s.getInvoice();
                sumWork += workH;
                sumInvoice += invoiceH;

                createTextCell(r, 2, formatLength(workH, allocation), text);
                createTextCell(r, 3, formatLength(invoiceH, allocation), text);
            }

            CellStyle sumStyle = createHeaderStyle(workbook);
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

            try (FileOutputStream os = new FileOutputStream(tempFile)) {
                workbook.write(os);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private XSSFSheet createSheet(String name, XSSFWorkbook wb) {
        XSSFSheet sheet = wb.createSheet(name);
        sheet.setDefaultColumnWidth(20);
        sheet.getPrintSetup().setLandscape(true);
        sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
        return sheet;
    }

    private CellStyle createHeaderStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createDefaultCellStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
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
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return hours + " (" + twoDForm.format(days) + "d)";
    }
}