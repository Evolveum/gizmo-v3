package com.evolveum.gizmo.component.modal;

import com.evolveum.gizmo.component.AjaxSubmitButton;
import com.evolveum.gizmo.component.SimplePanel;
import com.evolveum.gizmo.dto.ReportFilterDto;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;

import java.io.File;
import java.io.FileOutputStream;
import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Locale;

public abstract class AbstractExcelDownloadPanel extends SimplePanel<ReportFilterDto> {

    protected static final String ID_FORM = "form";
    protected static final String ID_REPORT_NAME = "reportName";
    protected static final String ID_PER_USER = "perUser";

    protected static final String ID_EXPORT = "export";
    protected static final String ID_CONFIRM_DOWNLOAD = "confirmDownload";


    protected TextField<String> reportNameField;
    protected IModel<DownloadSettingsDto> downloadModel;

    protected AbstractExcelDownloadPanel(String id, IModel<ReportFilterDto> model) {
        super(id, model);
    }

    @Override
    protected void initLayout() {
        Form<?> form = new Form<>(ID_FORM);
        add(form);

        buildFields(form);

        DownloadLink export = new DownloadLink("export",
                createDownloadModel(),
                this::currentFileName)
                .setCacheDuration(Duration.ofMillis(0))
                .setDeleteAfterDownload(true);


        export.add(new AttributeAppender("onclick", Model.of("$('.modal.show').modal('hide');"), ";"));
        form.add(export);
    }


    protected abstract String filePrefix();

    protected abstract String contextSuffix(ReportFilterDto filter);

    protected boolean supportsPerUser() { return false; }

    protected abstract void generateWorkbook(XSSFWorkbook wb, ReportFilterDto filter, boolean perUser);


    protected void buildFields(Form<?> form) {
        if (supportsPerUser()) {
            downloadModel = new LoadableDetachableModel<>() {
                private DownloadSettingsDto cache;
                @Override protected DownloadSettingsDto load() {
                    if (cache == null) {
                        cache = new DownloadSettingsDto();
                        cache.setReportName(defaultFileName(getModelObject()));
                    }
                    return cache;
                }
            };

            reportNameField = new TextField<>(ID_REPORT_NAME,
                    new PropertyModel<>(downloadModel, DownloadSettingsDto.F_REPORT_NAME));
            reportNameField.setOutputMarkupId(true);
            form.add(reportNameField);

            AjaxCheckBox perUser = new AjaxCheckBox(ID_PER_USER,
                    new PropertyModel<>(downloadModel, DownloadSettingsDto.F_PER_USER)) {
                @Override protected void onUpdate(AjaxRequestTarget target) { target.add(reportNameField); }
            };
            perUser.setOutputMarkupId(true);
            form.add(perUser);

            form.add(new FormComponentLabel("perUserLabel", perUser));
        } else {
            IModel<String> nameModel = new LoadableDetachableModel<>() {
                @Override protected String load() {
                    return defaultFileName(getModelObject());
                }
            };
            reportNameField = new TextField<>(ID_REPORT_NAME, nameModel) {
                @Override public String getInputName() { return ID_REPORT_NAME; }
            };
            reportNameField.setOutputMarkupId(true);
            form.add(reportNameField);

        }
    }

    protected IModel<File> createDownloadModel() {
        return new IModel<>() {
            @Override public File getObject() {
                try {
                    File tmp = new File(filePrefix() + ".xlsx");
                    try (XSSFWorkbook wb = new XSSFWorkbook()) {
                        boolean perUser = supportsPerUser() && downloadModel.getObject().isPerUser();
                        generateWorkbook(wb, getModelObject(), perUser);
                        try (FileOutputStream os = new FileOutputStream(tmp)) {
                            wb.write(os);
                        }
                    }
                    return tmp;
                } catch (Exception e) {
                    handleGuiExceptionFromPanel("Message.couldntGenerateReport",
                            e, RequestCycle.get().find(AjaxRequestTarget.class).orElse(null));
                    return new File(filePrefix() + "-error.xlsx");
                }
            }
        };
    }

    protected String defaultFileName(ReportFilterDto filter) {
        LocalDate from = filter.getDateFrom();
        LocalDate to = filter.getDateTo();
        String ctx = contextSuffix(filter);
        String range = from + "_" + to;
        return (filePrefix() + (ctx.isBlank() ? "" : "-" + ctx) + "-" + range + ".xlsx")
                .replaceAll("__", "_").replaceAll("--", "-");
    }

    protected String currentFileName() {
        if (supportsPerUser()) {
            return downloadModel.getObject().getReportName();
        }
        return defaultFileName(getModelObject());
    }


    public void syncReportNameWithFilter(AjaxRequestTarget target) {
        if (supportsPerUser() && downloadModel != null) {
            downloadModel.getObject().setReportName(defaultFileName(getModelObject()));
            if (reportNameField != null) target.add(reportNameField);
        }
    }


    protected XSSFSheet getSheet(XSSFWorkbook wb, String name) {
        XSSFSheet sheet = wb.getSheet(name);
        if (sheet == null) {
            sheet = wb.createSheet(name);
            sheet.setDefaultColumnWidth(20);
            sheet.getPrintSetup().setLandscape(true);
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
        }
        return sheet;
    }

    protected CellStyle headerStyle(XSSFWorkbook wb) {
        CellStyle st = wb.createCellStyle();
        st.setBorderBottom(BorderStyle.MEDIUM);
        st.setBorderLeft(BorderStyle.MEDIUM);
        st.setBorderRight(BorderStyle.MEDIUM);
        st.setBorderTop(BorderStyle.MEDIUM);
        Font f = wb.createFont(); f.setBold(true); st.setFont(f);
        return st;
    }

    protected CellStyle textStyle(XSSFWorkbook wb) {
        CellStyle st = wb.createCellStyle();
        st.setBorderBottom(BorderStyle.THIN);
        st.setBorderLeft(BorderStyle.THIN);
        st.setBorderRight(BorderStyle.THIN);
        st.setBorderTop(BorderStyle.THIN);
        st.setWrapText(true);
        return st;
    }

    protected CellStyle dateStyle(XSSFWorkbook wb) {
        CellStyle st = textStyle(wb);
        st.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/mm/yyyy"));
        return st;
    }

    protected static String slug(String s) {
        if (s == null || s.isBlank()) return "";
        String noDia = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String cleaned = noDia.replaceAll("[^A-Za-z0-9._-]+", "-")
                .replaceAll("[-_]{2,}", "-")
                .replaceAll("(^-|-$)", "");
        return cleaned.toLowerCase(Locale.ROOT);
    }
}
