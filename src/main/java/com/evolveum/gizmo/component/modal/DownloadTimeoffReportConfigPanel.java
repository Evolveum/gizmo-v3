package com.evolveum.gizmo.component.modal;
import com.evolveum.gizmo.component.SimplePanel;
import com.evolveum.gizmo.component.form.EmptyOnChangeAjaxBehavior;
import com.evolveum.gizmo.data.provider.SummaryUserDataProvider;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.UserSummary;
import com.evolveum.gizmo.util.LoadableModel;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serial;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
public class DownloadTimeoffReportConfigPanel extends SimplePanel<ReportFilterDto>{
    private static final String ID_REPORT_NAME = "reportName";

    private IModel<DownloadSettingsDto> downloadModel;

    public DownloadTimeoffReportConfigPanel(String id, IModel<ReportFilterDto> model) {
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
        return ("user-summary-" + range + ".xlsx").replaceAll("__", "_");
    }

    private IModel<File> createDownloadReportModel() {
        return new IModel<>() {
            @Serial private static final long serialVersionUID = 1L;
            @Override
            public File getObject() {
                File tempFile = new File("export.xlsx");
                generateUserSummaryExcel(tempFile, getModelObject());
                return tempFile;
            }
        };
    }
    private void generateUserSummaryExcel(File tempFile, ReportFilterDto filter) {
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

                LocalDate max = resolveMaxDate(s);
                if (max != null) {
                    XSSFCell c = r.createCell(1, CellType.NUMERIC);
                    c.setCellValue(java.sql.Date.valueOf(max));
                    c.setCellStyle(dateStyle);
                } else {
                    createTextCell(r, 1, "", textStyle);
                }

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

    private LocalDate resolveMaxDate(UserSummary s) {
        try {
            Method m = s.getClass().getMethod("getMaxDate");
            Object val = m.invoke(s);
            return (val instanceof LocalDate) ? (LocalDate) val : null;
        } catch (Exception ignore) {}
        try {
            Field f = s.getClass().getDeclaredField("maxDate");
            f.setAccessible(true);
            Object val = f.get(s);
            return (val instanceof LocalDate) ? (LocalDate) val : null;
        } catch (Exception ignore) {}
        return null;
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
