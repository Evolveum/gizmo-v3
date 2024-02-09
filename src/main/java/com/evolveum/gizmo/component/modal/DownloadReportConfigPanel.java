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
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.File;
import java.io.FileOutputStream;
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
        reportName.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                downloadModel.getObject().getReportName();

            }
        });
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

        DownloadLink exportExcel = new DownloadLink("export", new IModel<>()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public File getObject()
            {
                File tempFile = new File("export.xlsx");
                DownloadSettingsDto downloadSettings = downloadModel.getObject();
                generateExcelReport(tempFile, downloadSettings);
                return tempFile;
            }
        }, new PropertyModel<>(downloadModel, DownloadSettingsDto.F_REPORT_NAME))
                .setCacheDuration(Duration.ofMillis(0))
                .setDeleteAfterDownload(true);
        form.add(exportExcel);
    }

    private void generateExcelReport(File tempFile, DownloadSettingsDto downloadSettings) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            ReportFilterDto filterDto = getModelObject();

            if (downloadSettings.isCustomerReport()) {

                generateUsersReport(workbook, filterDto);
                generateSummaryReport(workbook, filterDto);
                FileOutputStream os = new FileOutputStream(tempFile);
                workbook.write(os);

                return;
            }

            if (downloadSettings.isPerUser()) {
                generateReportPerUser(workbook, filterDto);
            } else {
                generateUsersReport(workbook, filterDto);
            }

            if (downloadSettings.isSummary()) {
                generateSummaryReport(workbook, filterDto);
            }

            FileOutputStream os = new FileOutputStream(tempFile);
            workbook.write(os);


        } catch (Exception e) {
            System.out.println("exception " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void generateReportPerUser(XSSFWorkbook workbook, ReportFilterDto filterDto) {
        CellStyle style = workbook.createCellStyle();

        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(
                createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

        List<User> realizators = filterDto.getRealizators().isEmpty() ?
                getPageTemplate().getUserRepository().findAllEnabledUsers() : filterDto.getRealizators();
        for (User realizator : realizators) {
            List<Work> tasks = loadWork(realizator, filterDto);
            if (tasks.isEmpty()) {
                continue;
            }

            XSSFSheet sheet = createSheet(realizator.getFullName() + "(" + realizator.getId() + ")", workbook);
            generateExcel(filterDto, sheet, tasks, style, dateStyle);
        }
    }

    private List<Work> loadWork(User realizator, ReportFilterDto filterDto) {


        QAbstractTask task = QAbstractTask.abstractTask;
        JPAQuery query = GizmoUtils.createWorkQuery(getPageTemplate().getEntityManager());
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

    private CellStyle createDefaultCellStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    private void generateUsersReport(XSSFWorkbook workbook, ReportFilterDto filterDto) {
        CellStyle style = createDefaultCellStyle(workbook);

        CellStyle dateStyle = createDefaultCellStyle(workbook);
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(
                createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

        XSSFSheet sheet = createSheet("Work log", workbook);
        List<Work> tasks = listLoggedWork(filterDto);
       generateExcel(filterDto, sheet, tasks, style, dateStyle);
    }

    private XSSFSheet createSheet(String sheetName, XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.createSheet(sheetName);
        sheet.setDefaultColumnWidth(15);
        sheet.getPrintSetup().setLandscape(true);
        sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
        return sheet;
    }

    private List<Work> listLoggedWork(ReportFilterDto filterDto) {
        QAbstractTask task = QAbstractTask.abstractTask;
        JPAQuery query = GizmoUtils.createWorkQuery(getPageTemplate().getEntityManager());

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

    private void generateSummaryReport(XSSFWorkbook workbook, ReportFilterDto filterDto) {
        CellStyle style = workbook.createCellStyle();

        XSSFSheet sheet = createSheet("Summary", workbook);

        XSSFRow header = sheet.createRow(0);

        XSSFCell partHeaderCell = header.createCell(0, CellType.STRING);
        partHeaderCell.setCellValue("Part");
        XSSFCell workHeaderCell = header.createCell(1, CellType.STRING);
        workHeaderCell.setCellValue("Work length");
        XSSFCell invoiceHeaderCell = header.createCell(2, CellType.STRING);
        invoiceHeaderCell.setCellValue("Invoice");

        SummaryPartsDataProvider partsProvider = new SummaryPartsDataProvider(getPageTemplate());
        List<PartSummary> partSummaries = partsProvider.createSummary(filterDto);

        for (int i = 0; i < partSummaries.size(); i++) {
            PartSummary partSummary = partSummaries.get(i);

            XSSFRow row = sheet.createRow(i+1);

            XSSFCell user = row.createCell(0, CellType.STRING);
            user.setCellStyle(style);
            user.setCellValue(partSummary.getFullName());

            XSSFCell part = row.createCell(1, CellType.STRING);
            part.setCellStyle(style);
            part.setCellValue(partSummary.getName());

            XSSFCell work = row.createCell(2, CellType.NUMERIC);
            work.setCellStyle(style);
            work.setCellValue(partSummary.getLength());

            XSSFCell invoice = row.createCell(3, CellType.NUMERIC);
            invoice.setCellStyle(style);
            invoice.setCellValue(partSummary.getInvoice());
        }

        XSSFRow summaryRow = sheet.createRow(partSummaries.size()+2);
        XSSFCell summary = summaryRow.createCell(0, CellType.STRING);
        summary.setCellStyle(style);
        summary.setCellValue("Summary");

        XSSFCell work = summaryRow.createCell(1, CellType.NUMERIC);
        work.setCellStyle(style);
        work.setCellValue(countLength(partSummaries));

        XSSFCell invoice = summaryRow.createCell(2, CellType.NUMERIC);
        invoice.setCellStyle(style);
        invoice.setCellValue(countInvoice(partSummaries));
    }

    private Double countLength(List<PartSummary> partSummaries) {
        Double all = Double.valueOf(0);
        for (PartSummary summary : partSummaries) {
            all += summary.getLength();
        }
        return all;
    }

    private Double countInvoice(List<PartSummary> partSummaries) {
        Double all = Double.valueOf(0);
        for (PartSummary summary : partSummaries) {
            all += summary.getInvoice();
        }
        return all;
    }

    private List<CellDefinitionType> createCellDefinitions(boolean isCustomerReport, CellStyle dateStyle, CellStyle style) {
        List<CellDefinitionType> cellDefinitionTypes = new ArrayList<>();
        int j = 0;
        for (WorkCellType cell : WorkCellType.values()) {
            String fieldName = cell.getDisplayName();
            if (isCustomerReport && (fieldName.equals("Customer") || fieldName.equals("Track ID"))) {
                continue;
            }
            Class<?> fieldType = cell.getType();
            CellStyle cellStyle = LocalDate.class.equals(fieldType) ? dateStyle : style;
            cellDefinitionTypes.add(new CellDefinitionType(cell.getDisplayName(), j, cell.getType(), cellStyle, cell.getGetMethod()));
            j++;
        }
        return cellDefinitionTypes;
    }

    private void createCustomerReportHeader(ReportFilterDto filterDto, XSSFSheet sheet, List<CellDefinitionType> cellDefinitionTypes) {
        XSSFFont font = sheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        XSSFCellStyle customerStyle = sheet.getWorkbook().createCellStyle();
        customerStyle.setFont(font);
        customerStyle.setAlignment(HorizontalAlignment.CENTER);
        customerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFRow customer = sheet.createRow(0);

        int lastCell = cellDefinitionTypes.size() - 1;
        XSSFCell customerCell = customer.createCell(0, CellType.STRING);
        customerCell.setCellValue("Work Report for " + filterDto.getCustomerProjectPartDtos().get(0).getCustomerName());
        customerCell.setCellStyle(customerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, lastCell));



        XSSFRow from = sheet.createRow(1);
        XSSFCell fromCell = from.createCell(0, CellType.STRING);
        fromCell.setCellValue("From " + filterDto.getDateFrom());
        fromCell.setCellStyle(customerStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, lastCell));

        XSSFRow to = sheet.createRow(2);
        XSSFCell toCell = to.createCell(0, CellType.STRING);
        toCell.setCellValue("To " + filterDto.getDateTo());
        toCell.setCellStyle(customerStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, lastCell));
    }

    private void generateExcel(ReportFilterDto filterDto, XSSFSheet sheet, List<Work> tasks, CellStyle style, CellStyle dateStyle) {
        boolean isCustomerReport = downloadModel.getObject().isCustomerReport();
        List<CellDefinitionType> cellDefinitionTypes = createCellDefinitions(isCustomerReport, dateStyle, style);

        int startRowNumber = 0;

        if (isCustomerReport) {
            startRowNumber = 5;
            createCustomerReportHeader(filterDto, sheet, cellDefinitionTypes);
        }

        XSSFRow header = sheet.createRow(startRowNumber);
        int hi=0;
        for (CellDefinitionType s : cellDefinitionTypes) {
            XSSFCell headerCell = header.createCell(hi, CellType.STRING);
            headerCell.setCellValue(s.getDisplayName());
            headerCell.setCellStyle(style);
            hi++;
        }

        for (int i=0; i < tasks.size(); i++) {
            Work workTask = tasks.get(i);
            XSSFRow row = sheet.createRow(startRowNumber + i + 1);

            for (CellDefinitionType cell : cellDefinitionTypes) {
                XSSFCell date = row.createCell(cell.getPosition(), cell.getCellType());
                date.setCellStyle(cell.getStyle());
                try {
                    String[] getMethod = cell.getGetMethod().split("\\.");
                    Object value = workTask;
                    for (String method : getMethod) {
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
