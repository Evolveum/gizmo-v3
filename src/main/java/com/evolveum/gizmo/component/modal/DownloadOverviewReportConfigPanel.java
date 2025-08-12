package com.evolveum.gizmo.component.modal;

import com.evolveum.gizmo.component.SimplePanel;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.data.provider.ReportDataProvider;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.WorkDto;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.behavior.AttributeAppender;


import java.io.File;
import java.io.FileOutputStream;
import java.io.Serial;
import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Locale;

public class DownloadOverviewReportConfigPanel extends SimplePanel<ReportFilterDto> {

    private static final String ID_REPORT_NAME = "reportName";

    public DownloadOverviewReportConfigPanel(String id, IModel<ReportFilterDto> model) {
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

        exportExcel.add(new AttributeAppender(
                "onclick",
                Model.of("$('.modal.show').modal('hide');"),
                ";"
        ));
        form.add(exportExcel);
    }

    private String defaultFileName(ReportFilterDto f) {
        LocalDate from = f.getDateFrom();
        LocalDate to   = f.getDateTo();

        String realizatorPart = "";
        if (f.getRealizators().size() == 1) {
            User u = f.getRealizators().getFirst();
            String last = u.getFamilyName();
            realizatorPart = "-" + slug(last);
        }
        String range = (from.toString()) + "_" + (to.toString());
        return ("overview" + realizatorPart + "-" + range + ".xlsx")
                .replaceAll("__", "_")
                .replaceAll("--", "-");
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

    private void generateExcel(File file, ReportFilterDto filter) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = createSheet("Overview", wb);
            CellStyle header = createHeaderStyle(wb);
            CellStyle dateStyle = createDateStyle(wb);
            CellStyle text = createDefaultCellStyle(wb);

            int row = 0;
            XSSFRow h = sheet.createRow(row++);
            createHeaderCell(h, 0, "Date", header);
            createHeaderCell(h, 1, "Invoice", header);
            createHeaderCell(h, 2, "Time range", header);
            createHeaderCell(h, 3, "Realizator", header);
            createHeaderCell(h, 4, "Project", header);
            createHeaderCell(h, 5, "Description", header);
            createHeaderCell(h, 6, "Track ID", header);

            List<WorkDto> rows = fetchAllRows(filter);
            DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

            for (WorkDto dto : rows) {
                XSSFRow r = sheet.createRow(row++);
                LocalDate d = dto.getDate();
                if (d != null) {
                    XSSFCell c = r.createCell(0, CellType.NUMERIC);
                    c.setCellValue(java.sql.Date.valueOf(d));
                    c.setCellStyle(dateStyle);
                } else {
                    createTextCell(r, 0, "", text);
                }
                createTextCell(r, 1, String.valueOf(dto.getWorkLength()), text);
                LocalTime from = dto.getFrom();
                LocalTime to = dto.getTo();
                String range = (tf.format(from)) + (" - ") + (tf.format(to));
                createTextCell(r, 2, range, text);
                String fullName = dto.getRealizator() != null ? dto.getRealizator().getFullName() : "";
                createTextCell(r, 3, fullName, text);
                String project = buildProjectCell(dto.getCustomerProjectPart());
                createTextCell(r, 4, project, text);
                createTextCell(r, 5, dto.getDescription(), text);
                createTextCell(r, 6, dto.getTrackId(), text);
            }

            for (int c = 0; c <= 6; c++) sheet.autoSizeColumn(c);
            try (FileOutputStream os = new FileOutputStream(file)) { wb.write(os); }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildProjectCell(List<CustomerProjectPartDto> list) {
        if (list == null || list.isEmpty()) return "";
        return list.stream()
                .map(cpp -> cpp.getCustomerName() + " / " + cpp.getProjectName() + " / " + cpp.getPartName())
                .collect(Collectors.joining("\n"));
    }

    private List<WorkDto> fetchAllRows(ReportFilterDto filter) {
        ReportDataProvider provider = new ReportDataProvider(getPageTemplate());
        provider.setFilter(filter);
        long size = provider.size();
        List<WorkDto> out = new ArrayList<>((int)Math.min(size, Integer.MAX_VALUE));
        final long page = 500;
        for (long first = 0; first < size; first += page) {
            Iterator<? extends WorkDto> it = provider.iterator(first, Math.min(page, size - first));
            while (it.hasNext()) out.add(it.next());
        }
        provider.detach();
        return out;
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
        Font font = wb.createFont(); font.setBold(true); style.setFont(font); return style;
    }
    private CellStyle createDefaultCellStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setWrapText(true); return style;
    }
    private CellStyle createDateStyle(XSSFWorkbook wb) {
        CellStyle dateStyle = createDefaultCellStyle(wb);
        CreationHelper ch = wb.getCreationHelper();
        dateStyle.setDataFormat(ch.createDataFormat().getFormat("dd/mm/yyyy"));
        return dateStyle;
    }
    private void createHeaderCell(XSSFRow row, int col, String text, CellStyle style) {
        XSSFCell cell = row.createCell(col, CellType.STRING); cell.setCellValue(text); cell.setCellStyle(style);
    }
    private void createTextCell(XSSFRow row, int col, String text, CellStyle style) {
        XSSFCell cell = row.createCell(col, CellType.STRING); cell.setCellValue(text != null ? text : ""); cell.setCellStyle(style);
    }

}
