package com.evolveum.gizmo.component.modal;

import com.evolveum.gizmo.component.SimplePanel;
import com.evolveum.gizmo.data.provider.SummaryPartsDataProvider;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.PartSummary;
import com.evolveum.gizmo.dto.ReportFilterDto;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serial;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;


public class DownloadProjectReportConfigPanel extends SimplePanel<ReportFilterDto> {

    private static final String ID_REPORT_NAME = "reportName";

    public DownloadProjectReportConfigPanel(String id, IModel<ReportFilterDto> model) {
        super(id, model);
    }

    @Override
    protected void initLayout() {
        Form<Void> form = new Form<>("form");
        add(form);

        IModel<String> nameModel = new LoadableDetachableModel<>() {
            @Override
            protected String load() {
                return defaultFileName(getModelObject());
            }
        };

        TextField<String> reportName = new TextField<>(ID_REPORT_NAME, nameModel) {
            @Override public String getInputName() { return ID_REPORT_NAME; }
        };
        reportName.setOutputMarkupId(true);
        form.add(reportName);

        DownloadLink exportExcel = new DownloadLink("export",
                createDownloadModel(),
                () -> {
                    String name = nameModel.getObject();
                    return defaultFileName(getModelObject());
                })
                .setCacheDuration(Duration.ofMillis(0))
                .setDeleteAfterDownload(true);
        exportExcel.add(new AttributeAppender("onclick", Model.of("$('.modal.show').modal('hide');"), ";"
        ));
        form.add(exportExcel);
    }

    private IModel<File> createDownloadModel() {
        return new IModel<>() {
            @Serial private static final long serialVersionUID = 1L;
            @Override public File getObject() {
                File tmp = new File("export.xlsx");
                generateExcel(tmp, getModelObject());
                return tmp;
            }
        };
    }

    private String defaultFileName(ReportFilterDto filter) {
        LocalDate from = filter.getDateFrom();
        LocalDate to = filter.getDateTo();
        String projectPart = "";
        if (filter.getCustomerProjectPartDtos().size() == 1) {
            CustomerProjectPartDto u = filter.getCustomerProjectPartDtos().getFirst();
            String project = u.getProjectName();
            projectPart = "-" + slug(project);
        }
        String range = (from.toString() + "_" + (to.toString()));
        return ("part-summary-" + projectPart + "-" + range + ".xlsx").replaceAll("__", "_").replaceAll("--", "-");
    }

    private static String slug(String s) {
        if (s == null || s.isBlank()) return "";
        String noDia = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String cleaned = noDia.replaceAll("[^A-Za-z0-9._-]+", "-")
                .replaceAll("[-_]{2,}", "-")
                .replaceAll("(^-|-$)", "");
        return cleaned.toLowerCase(Locale.ROOT);
    }

    private void generateExcel(File tempFile, ReportFilterDto filter) {
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