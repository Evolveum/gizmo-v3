package com.evolveum.gizmo.component.modal;
import com.evolveum.gizmo.component.SimplePanel;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.data.provider.SummaryUserDataProvider;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.UserSummary;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.ss.usermodel.*;
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

public class DownloadTimeoffReportConfigPanel extends SimplePanel<ReportFilterDto>{

    private static final String ID_REPORT_NAME = "reportName";

    public DownloadTimeoffReportConfigPanel(String id, IModel<ReportFilterDto> model) {
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

    private String defaultFileName(ReportFilterDto filter) {
        LocalDate from = filter.getDateFrom();
        LocalDate to = filter.getDateTo();
        String realizatorPart = "";
        if (filter.getRealizators().size() == 1) {
            User u = filter.getRealizators().getFirst();
            String last = u.getFamilyName();
            realizatorPart = "-" + slug(last);
        }
        String range = (from.toString() + "_" + (to.toString()));
        return ("user-summary-" + realizatorPart + "-" + range + ".xlsx").replaceAll("__", "_").replaceAll("--", "-");
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

    private void generateExcel(File tempFile, ReportFilterDto filter) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = createSheet("User summary", workbook);

            CellStyle header = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle textStyle = createDefaultCellStyle(workbook);

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

            for (int c = 0; c <= 3; c++) {
                sheet.autoSizeColumn(c);
            }

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

    private CellStyle createDateStyle(XSSFWorkbook wb) {
        CellStyle dateStyle = createDefaultCellStyle(wb);
        CreationHelper ch = wb.getCreationHelper();
        dateStyle.setDataFormat(ch.createDataFormat().getFormat("dd/mm/yyyy"));
        return dateStyle;
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
