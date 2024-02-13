/*
 *  Copyright (C) 2024 Evolveum
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.evolveum.gizmo.component.modal;

import com.evolveum.gizmo.component.SimplePanel;
import com.evolveum.gizmo.component.form.EmptyOnChangeAjaxBehavior;
import com.evolveum.gizmo.data.AbstractTask;
import com.evolveum.gizmo.data.QAbstractTask;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.data.Work;
import com.evolveum.gizmo.data.provider.ReportDataProvider;
import com.evolveum.gizmo.data.provider.SummaryPartsDataProvider;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.PartSummary;
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
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serial;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DownloadReportConfigPanel extends SimplePanel<ReportFilterDto> {

    private static final String ID_PER_USER = "perUser";
    private static final String ID_SHOW_SUMMARY = "showSummary";
    private static final String ID_CUSTOMER_REPORT = "customerReport";
    private static final String ID_REPORT_NAME = "reportName";

    private IModel<DownloadSettingsDto> downloadModel;

    public DownloadReportConfigPanel(String id, IModel<ReportFilterDto> model) {
        super(id, model);
    }

    @Override
    protected void initLayout() {

        downloadModel = new LoadableModel<>(false) {
            @Override
            protected DownloadSettingsDto load() {
                return new DownloadSettingsDto();
            }
        };

        Form<DownloadSettingsDto> form = new Form<>("form");
        add(form);


        TextField<String> reportName = new TextField<>(ID_REPORT_NAME, new PropertyModel<>(downloadModel, DownloadSettingsDto.F_REPORT_NAME));
        reportName.add(new EmptyOnChangeAjaxBehavior());
        form.add(reportName);

        AjaxCheckBox perUser = new AjaxCheckBox(ID_PER_USER, new PropertyModel<>(downloadModel, DownloadSettingsDto.F_PER_USER)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        };
        perUser.setOutputMarkupId(true);
        form.add(perUser);

        AjaxCheckBox showSummary = new AjaxCheckBox(ID_SHOW_SUMMARY, new PropertyModel<>(downloadModel, DownloadSettingsDto.F_SUMMARY)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        };
        form.add(showSummary);

        AjaxCheckBox customerReport = new AjaxCheckBox(ID_CUSTOMER_REPORT, new PropertyModel<>(downloadModel, DownloadSettingsDto.F_CUSTOMER_REPORT)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        };
        form.add(customerReport);

        DownloadLink exportExcel = new DownloadLink("export",
                createDownloadReportModel(),
                new PropertyModel<>(downloadModel, DownloadSettingsDto.F_REPORT_NAME))
                .setCacheDuration(Duration.ofMillis(0))
                .setDeleteAfterDownload(true);

        form.add(exportExcel);
    }

    private IModel<File> createDownloadReportModel() {
        return new IModel<>() {
            @Serial
            private static final long serialVersionUID = 1L;


            @Override
            public File getObject()
            {
                File tempFile = new File("export.xlsx");
                DownloadSettingsDto downloadSettings = downloadModel.getObject();
                generateExcelReport(tempFile, downloadSettings);
                return tempFile;
            }
        };
    }

    private void generateExcelReport(File tempFile, DownloadSettingsDto downloadSettings) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            ReportFilterDto filterDto = getModelObject();

            if (downloadSettings.isCustomerReport()) {
                generateCustomerReport(filterDto, workbook);
                FileOutputStream os = new FileOutputStream(tempFile);
                workbook.write(os);

                return;
            }

            if (downloadSettings.isPerUser()) {
                generateReportPerUser(workbook, filterDto);
            } else {
                generateUsersReport(workbook, "Users report", filterDto,  ReportType.GENERIC);
            }

            if (downloadSettings.isSummary()) {
                generateSummaryReport(workbook, "Summary report", filterDto);
            }

            FileOutputStream os = new FileOutputStream(tempFile);
            workbook.write(os);


        } catch (Exception e) {
            System.out.println("exception " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void generateReportPerUser(XSSFWorkbook workbook, ReportFilterDto filterDto) {
        List<User> realizators = filterDto.getRealizators().isEmpty() ?
                getPageTemplate().getUserRepository().findAllEnabledUsers() : filterDto.getRealizators();
        for (User realizator : realizators) {
            List<AbstractTask> tasks = loadWork(realizator, filterDto);
            if (tasks.isEmpty()) {
                continue;
            }

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

    private void generateUsersReport(XSSFWorkbook workbook, String sheetName, ReportFilterDto filterDto, ReportType reportType) {
        List<AbstractTask> tasks = listLoggedWork(filterDto);
        generateExcel(workbook, sheetName, tasks, reportType);
    }

    private List<AbstractTask> listLoggedWork(ReportFilterDto filterDto) {
        QAbstractTask task = QAbstractTask.abstractTask;
        JPAQuery<Work> query = GizmoUtils.createWorkQuery(getPageTemplate().getEntityManager());

        List<Predicate> list = new ArrayList<>();
        Predicate p = createListPredicate(filterDto.getRealizators(), task.realizator);
        if (p != null) {
            list.add(p);
        }
        if (filterDto.getDateFrom() != null) {
            list.add(task.date.goe(filterDto.getDateFrom()));
        }
        if (filterDto.getDateTo() != null) {
            list.add(task.date.loe(filterDto.getDateTo()));
        }
        if (CollectionUtils.isNotEmpty(filterDto.getCustomerProjectPartDtos())) {
            BooleanBuilder projectPredicate = new BooleanBuilder();
            for (CustomerProjectPartDto project : filterDto.getCustomerProjectPartDtos()) {
                projectPredicate.or(ReportDataProvider.createPredicate(project));
            }
            list.add(projectPredicate);
        }

        BooleanBuilder bb = new BooleanBuilder();
        Predicate predicates = bb.orAllOf(list.toArray(new Predicate[list.size()]));
        query.where(predicates);

        return query.select(task).fetch();
    }

    private void generateSummaryReport(XSSFWorkbook workbook, String sheetName, ReportFilterDto filterDto) {
        SummaryPartsDataProvider partsProvider = new SummaryPartsDataProvider(getPageTemplate());
        List<PartSummary> partSummaries = partsProvider.createSummary(filterDto);
        generateExcel(workbook, sheetName, partSummaries, ReportType.SUMMARY);

        XSSFSheet sheet = workbook.getSheet(sheetName);

        CellStyle style = createHeaderDefaultStyle(workbook);
        XSSFRow summaryRow = sheet.createRow(sheet.getLastRowNum() + 1);
        sheet.addMergedRegion(new CellRangeAddress(summaryRow.getRowNum(), summaryRow.getRowNum(), 0, 1));
        XSSFCell summary = summaryRow.createCell(0, CellType.STRING);
        summary.setCellStyle(style);
        summary.setCellValue("Summary");

        XSSFCell work = summaryRow.createCell(2, CellType.NUMERIC);
        work.setCellStyle(style);
        work.setCellValue(countLength(partSummaries));

        XSSFCell invoice = summaryRow.createCell(3, CellType.NUMERIC);
        invoice.setCellStyle(style);
        invoice.setCellValue(countInvoice(partSummaries));
    }

    private Double countLength(List<PartSummary> partSummaries) {
        double all = 0;
        for (PartSummary summary : partSummaries) {
            all += summary.getLength();
        }
        return all;
    }

    private Double countInvoice(List<PartSummary> partSummaries) {
        double all = 0;
        for (PartSummary summary : partSummaries) {
            all += summary.getInvoice();
        }
        return all;
    }

    private List<CellDefinitionType> createCellDefinitions(ReportType reportType, CellStyle dateStyle, CellStyle style) {
        List<CellDefinitionType> cellDefinitionTypes = new ArrayList<>();
        int j = 0;
        List<WorkCellType> cells = WorkCellType.getCellsForReport(reportType);
        for (WorkCellType cell : cells) {
            Class<?> fieldType = cell.getType();
            CellStyle cellStyle = LocalDate.class.equals(fieldType) ? dateStyle : style;
            cellDefinitionTypes.add(new CellDefinitionType(cell.getDisplayName(), j, cell.getType(), cellStyle, cell.getGetMethod()));
            j++;
        }
        return cellDefinitionTypes;
    }

    private void createCustomerReportHeader(ReportFilterDto filterDto, XSSFWorkbook xssfWorkbook, String sheetName, int lastCellIndex) {
        XSSFFont font = xssfWorkbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        XSSFCellStyle customerStyle = xssfWorkbook.createCellStyle();
        customerStyle.setFont(font);
        customerStyle.setAlignment(HorizontalAlignment.CENTER);
        customerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFSheet sheet = getSheet(xssfWorkbook, sheetName);
        XSSFRow customer = sheet.createRow(0);

        XSSFCell customerCell = customer.createCell(0, CellType.STRING);
        customerCell.setCellValue("Work Report for " + filterDto.getCustomerProjectPartDtos().get(0).getCustomerName());
        customerCell.setCellStyle(customerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, lastCellIndex));



        XSSFRow from = sheet.createRow(1);
        XSSFCell fromCell = from.createCell(0, CellType.STRING);
        fromCell.setCellValue("From " + filterDto.getDateFrom());
        fromCell.setCellStyle(customerStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, lastCellIndex));

        XSSFRow to = sheet.createRow(2);
        XSSFCell toCell = to.createCell(0, CellType.STRING);
        toCell.setCellValue("To " + filterDto.getDateTo());
        toCell.setCellStyle(customerStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, lastCellIndex));
    }

    private void generateCustomerReport(ReportFilterDto filterDto, XSSFWorkbook workbook) {
        boolean isCustomerReport = downloadModel.getObject().isCustomerReport();
        String sheetName = "Customer report";

        if (isCustomerReport) {
            createCustomerReportHeader(filterDto, workbook, sheetName, WorkCellType.getCellsForReport(ReportType.CUSTOMER).size() -1);
        }

        generateSummaryReport(workbook, sheetName, filterDto);
        generateUsersReport(workbook, sheetName, filterDto, ReportType.CUSTOMER);
    }

    private XSSFSheet getSheet(XSSFWorkbook workbook, String sheetName) {
        XSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = createSheet(sheetName, workbook);
        }
        return sheet;
    }

    private XSSFSheet createSheet(String sheetName, XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.createSheet(sheetName);
        sheet.setDefaultColumnWidth(20);
        sheet.getPrintSetup().setLandscape(true);
        sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
        return sheet;
    }

    private <T> void generateExcel(XSSFWorkbook workbook, String sheetName, List<T> tasks, ReportType reportType) {
        //style, dateStyle, 0,


        List<CellDefinitionType> cellDefinitionTypes =
                createCellDefinitions(reportType,
                        createDateStyle(workbook),
                        createDefaultCellStyle(workbook));

        int startRowNumber = 0;
        XSSFSheet sheet = getSheet(workbook, sheetName);
        if (sheet.getLastRowNum() != 0) {
            startRowNumber = sheet.getLastRowNum() + 2;
        }

        XSSFRow header = sheet.createRow(startRowNumber);
        int hi=0;
        CellStyle headerStyle = createHeaderDefaultStyle(workbook);
        for (CellDefinitionType s : cellDefinitionTypes) {
            XSSFCell headerCell = header.createCell(hi, CellType.STRING);
            headerCell.setCellValue(s.getDisplayName());
            headerCell.setCellStyle(headerStyle);
            hi++;
        }

        for (int i=0; i < tasks.size(); i++) {
            T workTask = tasks.get(i);
            XSSFRow row = sheet.createRow(startRowNumber + i + 1);

            for (CellDefinitionType cell : cellDefinitionTypes) {
                XSSFCell date = row.createCell(cell.getPosition(), cell.getCellType());
                date.setCellStyle(cell.getStyle());
                try {
                    String[] getMethod = cell.getGetMethod().split("\\.");
                    Object value = workTask;
                    for (String method : getMethod) {
                        if (value == null) {
                            break;
                        }
                        value = value.getClass().getMethod(method).invoke(value);
                    }
                    date.setCellValue(value == null ? "" : value.toString());
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    System.out.println("exception " + e.getMessage());
                    e.printStackTrace();
                }

            }

        }
    }

    private static <T> Predicate createListPredicate(List<T> list, EntityPathBase<T> base) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        if (list.size() == 1) {
            return base.eq(list.get(0));
        }

        BooleanExpression expr = base.eq(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            expr = expr.or(base.eq(list.get(i)));
        }

        return expr;
    }


}
