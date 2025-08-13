package com.evolveum.gizmo.component.modal;

import com.evolveum.gizmo.component.SimplePanel;
import com.evolveum.gizmo.data.AbstractTask;
import com.evolveum.gizmo.data.QAbstractTask;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.data.Work;
import com.evolveum.gizmo.data.provider.ReportDataProvider;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serial;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DownloadOverviewReportConfigPanel extends SimplePanel<ReportFilterDto> {

    private static final String ID_PER_USER = "perUser";
    private static final String ID_REPORT_NAME = "reportName";
    private TextField<String> reportNameField;
    private IModel<DownloadSettingsDto> downloadModel;

    public DownloadOverviewReportConfigPanel(String id, IModel<ReportFilterDto> model) {
        super(id, model);
    }

    @Override
    protected void initLayout() {
        downloadModel = new LoadableModel<>(false) {
            private DownloadSettingsDto cache;
            @Override
            protected DownloadSettingsDto load() {
                if (cache == null) {
                    cache = new DownloadSettingsDto();
                    cache.setReportName(defaultFileName(getModelObject()));

                }
                return cache;
            }
            @Override
            public void detach() {
            }
        };

        Form<DownloadSettingsDto> form = new Form<>("form");
        add(form);

        reportNameField = new TextField<>(ID_REPORT_NAME,
                new PropertyModel<>(downloadModel, DownloadSettingsDto.F_REPORT_NAME));
        reportNameField.setOutputMarkupId(true);
        form.add(reportNameField);

        AjaxCheckBox perUser = new AjaxCheckBox(ID_PER_USER,
                new PropertyModel<>(downloadModel, DownloadSettingsDto.F_PER_USER)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(reportNameField);
            }
        };
        form.add(perUser);

        DownloadLink exportExcel = new DownloadLink("export",
                createDownloadReportModel(),
                () -> {
                    DownloadSettingsDto s = downloadModel.getObject();
                    return defaultFileName(getModelObject());
                })
                .setCacheDuration(Duration.ofMillis(0))
                .setDeleteAfterDownload(true);

        exportExcel.add(new AttributeAppender("onclick", Model.of("$('.modal.show').modal('hide');"), ";"
        ));
        form.add(exportExcel);
    }

    public void syncReportNameWithFilter(AjaxRequestTarget target) {
        String fresh = defaultFileName(getModelObject());
        downloadModel.getObject().setReportName(fresh);
        if (reportNameField != null) {
            target.add(reportNameField);
        }
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
        return ("overview-" + realizatorPart + "-" + range + ".xlsx").replaceAll("__", "_");
    }

    private static String slug(String s) {
        if (s == null || s.isBlank()) return "";
        String noDia = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String cleaned = noDia.replaceAll("[^A-Za-z0-9._-]+", "-")
                .replaceAll("[-_]{2,}", "-")
                .replaceAll("(^-|-$)", "");
        return cleaned.toLowerCase(java.util.Locale.ROOT);
    }

    private IModel<File> createDownloadReportModel() {
        return new IModel<>() {
            @Serial
            private static final long serialVersionUID = 1L;
            @Override
            public File getObject() {
                File tempFile = new File("overview.xlsx");
                generateExcelReport(tempFile, downloadModel.getObject());
                return tempFile;
            }
        };
    }

    private void generateExcelReport(File tempFile, DownloadSettingsDto downloadSettings) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            ReportFilterDto filterDto = getModelObject();

            if (downloadSettings.isPerUser()) {
                generateReportPerUser(workbook, filterDto);
            } else {
                generateUsersReport(workbook, "Overview report", filterDto, ReportType.GENERIC);
            }

            try (FileOutputStream os = new FileOutputStream(tempFile)) {
                workbook.write(os);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateReportPerUser(XSSFWorkbook workbook, ReportFilterDto filterDto) {
        List<User> realizators = filterDto.getRealizators().isEmpty() ?
                getPageTemplate().getUserRepository().findAllEnabledUsers() : filterDto.getRealizators();
        for (User realizator : realizators) {
            List<AbstractTask> tasks = loadWork(realizator, filterDto);
            if (tasks.isEmpty()) continue;
            String sheetName = realizator.getFullName() + "(" + realizator.getId() + ")";
            generateExcel(workbook, sheetName, tasks, ReportType.GENERIC);
        }
    }

    private List<AbstractTask> loadWork(User realizator, ReportFilterDto filterDto) {
        QAbstractTask task = QAbstractTask.abstractTask;
        JPAQuery<Work> query = GizmoUtils.createWorkQuery(getPageTemplate().getEntityManager());
        BooleanBuilder predicates = new BooleanBuilder(task.realizator.name.eq(realizator.getName()));

        if (filterDto.getDateFrom() != null) {
            predicates.and(task.date.goe(filterDto.getDateFrom()));
        }
        if (filterDto.getDateTo() != null) {
            predicates.and(task.date.loe(filterDto.getDateTo()));
        }
        if (CollectionUtils.isNotEmpty(filterDto.getCustomerProjectPartDtos())) {
            BooleanBuilder projectPredicate = new BooleanBuilder();
            for (CustomerProjectPartDto project : filterDto.getCustomerProjectPartDtos()) {
                projectPredicate.or(ReportDataProvider.createPredicate(project));
            }
            predicates.and(projectPredicate);
        }
        query.where(predicates);
        return query.select(task).fetch();
    }

    private void generateUsersReport(XSSFWorkbook workbook, String sheetName,
                                     ReportFilterDto filterDto, ReportType reportType) {
        List<AbstractTask> tasks = listLoggedWork(filterDto);
        generateExcel(workbook, sheetName, tasks, reportType);
    }

    private List<AbstractTask> listLoggedWork(ReportFilterDto filterDto) {
        QAbstractTask task = QAbstractTask.abstractTask;
        JPAQuery<Work> query = GizmoUtils.createWorkQuery(getPageTemplate().getEntityManager());
        List<Predicate> predicates = new ArrayList<>();

        Predicate p = createListPredicate(filterDto.getRealizators(), task.realizator);
        if (p != null) predicates.add(p);
        if (filterDto.getDateFrom() != null) predicates.add(task.date.goe(filterDto.getDateFrom()));
        if (filterDto.getDateTo() != null) predicates.add(task.date.loe(filterDto.getDateTo()));
        if (CollectionUtils.isNotEmpty(filterDto.getCustomerProjectPartDtos())) {
            BooleanBuilder projectPredicate = new BooleanBuilder();
            for (CustomerProjectPartDto project : filterDto.getCustomerProjectPartDtos()) {
                projectPredicate.or(ReportDataProvider.createPredicate(project));
            }
            predicates.add(projectPredicate);
        }

        BooleanBuilder bb = new BooleanBuilder();
        Predicate combined = bb.orAllOf(predicates.toArray(new Predicate[0]));
        query.where(combined);

        return query.select(task).fetch();
    }

    private static <T> Predicate createListPredicate(List<T> list, EntityPathBase<T> base) {
        if (list == null || list.isEmpty()) return null;
        if (list.size() == 1) return base.eq(list.get(0));

        BooleanExpression expr = base.eq(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            expr = expr.or(base.eq(list.get(i)));
        }
        return expr;
    }

    private <T> void generateExcel(XSSFWorkbook workbook, String sheetName, List<T> tasks, ReportType reportType) {
        List<WorkCellType> cells = WorkCellType.getCellsForReport(reportType);
        List<CellDefinitionType> cellDefinitionTypes = new ArrayList<>();

        int j = 0;
        for (WorkCellType cell : cells) {
            Class<?> fieldType = cell.getType();
            CellStyle style = LocalDate.class.equals(fieldType)
                    ? createDateStyle(workbook)
                    : createDefaultCellStyle(workbook);

            cellDefinitionTypes.add(new CellDefinitionType(
                    cell.getDisplayName(),
                    j,
                    cell.getType(),
                    style,
                    cell.getGetMethod()
            ));
            j++;
        }

        int startRowNumber = 0;
        XSSFSheet sheet = getSheet(workbook, sheetName);
        if (sheet.getLastRowNum() != 0) {
            startRowNumber = sheet.getLastRowNum() + 2;
        }

        XSSFRow header = sheet.createRow(startRowNumber);
        CellStyle headerStyle = createHeaderDefaultStyle(workbook);
        for (CellDefinitionType def : cellDefinitionTypes) {
            XSSFCell headerCell = header.createCell(def.getPosition(), CellType.STRING);
            headerCell.setCellValue(def.getDisplayName());
            headerCell.setCellStyle(headerStyle);
        }

        for (int i = 0; i < tasks.size(); i++) {
            T workTask = tasks.get(i);
            XSSFRow row = sheet.createRow(startRowNumber + i + 1);
            for (CellDefinitionType def : cellDefinitionTypes) {
                XSSFCell cell = row.createCell(def.getPosition(), def.getCellType());
                cell.setCellStyle(def.getStyle());
                try {
                    String[] methods = def.getGetMethod().split("\\.");
                    Object value = workTask;
                    for (String method : methods) {
                        if (value == null) break;
                        value = value.getClass().getMethod(method).invoke(value);
                    }
                    cell.setCellValue(value == null ? "" : value.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private CellStyle createHeaderDefaultStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setShrinkToFit(true);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createDefaultCellStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createDateStyle(XSSFWorkbook workbook) {
        CellStyle dateStyle = createDefaultCellStyle(workbook);
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
        return dateStyle;
    }

    private XSSFSheet getSheet(XSSFWorkbook workbook, String sheetName) {
        XSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
            sheet.setDefaultColumnWidth(20);
            sheet.getPrintSetup().setLandscape(true);
            sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
        }
        return sheet;
    }
}

