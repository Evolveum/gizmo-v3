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
import com.evolveum.gizmo.data.*;
import com.evolveum.gizmo.data.provider.AbstractTaskDataProvider;
import com.evolveum.gizmo.data.provider.SummaryPartsDataProvider;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.PartSummary;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.util.GizmoUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetViewType;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.TextCell;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
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

        downloadModel = DownloadSettingsDto::new;


        TextField<String> reportName = new TextField<>(ID_REPORT_NAME, new PropertyModel<>(downloadModel, DownloadSettingsDto.F_REPORT_NAME));
        reportName.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        });
        add(reportName);

        AjaxCheckBox perUser = new AjaxCheckBox(ID_PER_USER, new PropertyModel<>(downloadModel, DownloadSettingsDto.F_PER_USER)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        };
        perUser.setOutputMarkupId(true);
        add(perUser);

        AjaxCheckBox showSummary = new AjaxCheckBox(ID_SHOW_SUMMARY, new PropertyModel<>(downloadModel, DownloadSettingsDto.F_SUMMARY)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        };
        add(showSummary);

        AjaxCheckBox customerReport = new AjaxCheckBox(ID_CUSTOMER_REPORT, new PropertyModel<>(downloadModel, DownloadSettingsDto.F_CUSTOMER_REPORT)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                downloadModel.getObject().setCustomerReport(!downloadModel.getObject().isCustomerReport());
            }
        };
        add(customerReport);

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
        add(exportExcel);
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

//                    style.setShrinkToFit(true);

            if (downloadSettings.isPerUser()) {
                generateReportPerUser(workbook, filterDto);
            } else {
                generateUsersReport(workbook, filterDto);
            }

            if (downloadSettings.isSummary()) {
                generateSummaryReport(workbook, filterDto);
            }


        } catch (Exception e) {
            System.out.println("exception " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void generateReportPerUser(XSSFWorkbook workbook, ReportFilterDto filterDto) {
        CellStyle style = workbook.createCellStyle();

        CellStyle dateStype = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStype.setDataFormat(
                createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

        List<User> realizators = filterDto.getRealizators().isEmpty() ? getPageTemplate().getUserRepository().findAllEnabledUsers() : filterDto.getRealizators();
        for (User realizator : realizators) {
//                        sheet.setDefaultColumnWidth(30);
            List<Work> tasks = loadWork(realizator, filterDto);

            XSSFSheet sheet = workbook.createSheet(realizator.getFullName() + "(" + realizator.getId() +")");
            createHeader(sheet);
            for (int i=0; i < tasks.size(); i++) {
                Work workTask = tasks.get(i);
                XSSFRow row = sheet.createRow(i+1);
                XSSFCell date = appendCell(row, 0, CellType.NUMERIC, dateStype);
                date.setCellValue(workTask.getDate());

                XSSFCell trackId = appendCell(row,1, CellType.STRING, style);
                trackId.setCellValue(workTask.getTrackId());

                Part part = workTask.getPart();
                XSSFCell partCell = appendCell(row, 4, CellType.STRING, style);
                partCell.setCellValue(part.getName());

                Project project = part.getProject();
                XSSFCell projectCell = appendCell(row, 3, CellType.STRING, style);
                projectCell.setCellValue(project.getName());

                Customer customer = project.getCustomer();
                XSSFCell customerCell = appendCell(row, 2, CellType.STRING, style);
                customerCell.setCellValue(customer.getName());

                XSSFCell workLengthCell = appendCell(row, 5, CellType.NUMERIC, style);
                workLengthCell.setCellValue(workTask.getWorkLength());

                XSSFCell invoiceLengthCell = appendCell(row, 6, CellType.NUMERIC, style);
                invoiceLengthCell.setCellValue(workTask.getInvoiceLength());


            }
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);

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
                projectPredicate.or(AbstractTaskDataProvider.createPredicate(project));
            }
            predicates.and(projectPredicate);
        }

        query.where(predicates);
        return query.select(task).fetch();
    }

    private void createHeader(XSSFSheet sheet) {
        XSSFRow header = sheet.createRow(0);

        XSSFCell dateHeaderCell = header.createCell(0, CellType.STRING);
        dateHeaderCell.setCellValue("Date");
        XSSFCell trackIdHeaderCell = header.createCell(1, CellType.STRING);
        trackIdHeaderCell.setCellValue("TrackId");

        header.createCell(2, CellType.STRING).setCellValue("Customer");
        header.createCell(3, CellType.STRING).setCellValue("Project");
        header.createCell(4, CellType.STRING).setCellValue("Part");
        header.createCell(5, CellType.STRING).setCellValue("Work length");
        header.createCell(6, CellType.STRING).setCellValue("Invoice length");
    }

    private XSSFCell appendCell(XSSFRow row, int cellId, CellType cellType, CellStyle cellStyle) {
        XSSFCell column = row.createCell(cellId, cellType);
        column.setCellStyle(cellStyle);
        return column;
    }

    private void generateUsersReport(XSSFWorkbook workbook, ReportFilterDto filterDto) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);

        CellStyle dateStype = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStype.setDataFormat(
                createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
        dateStype.setBorderBottom(BorderStyle.THIN);
        dateStype.setBorderLeft(BorderStyle.THIN);
        dateStype.setBorderRight(BorderStyle.THIN);
        dateStype.setBorderTop(BorderStyle.THIN);


        XSSFSheet sheet = workbook.createSheet("Work log");
        sheet.setDefaultColumnWidth(30);


//        sheet.getCTWorksheet().getSheetViews().setView(STSheetViewType.PAGE_LAYOUT);


       List<Work> tasks = listLoggedWork(filterDto);
       generateExcel(filterDto, sheet, tasks, style, dateStype);


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
                projectPredicate.or(AbstractTaskDataProvider.createPredicate(project));
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

        XSSFSheet sheet = workbook.createSheet("Summary");
        sheet.setDefaultColumnWidth(30);

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
            XSSFCell part = row.createCell(0, CellType.STRING);
            part.setCellStyle(style);
            part.setCellValue(partSummary.getName());

            XSSFCell work = row.createCell(1, CellType.NUMERIC);
            work.setCellStyle(style);
            work.setCellValue(partSummary.getLength());

            XSSFCell invoice = row.createCell(2, CellType.NUMERIC);
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



    private void generateExcel(ReportFilterDto filterDto, XSSFSheet sheet, List<Work> tasks, CellStyle style, CellStyle dateStype) {
        boolean isCutomerReport = downloadModel.getObject().isCustomerReport();
        Map<String, CellDefinitionType> cellMap = new HashMap<>();
        cellMap.put("Date", new CellDefinitionType(0, CellType.NUMERIC, dateStype));
        cellMap.put("Realizator", new CellDefinitionType(1, CellType.STRING, style));

        if (!isCutomerReport) {
            cellMap.put("TrackId", new CellDefinitionType(2, CellType.STRING, style));
            cellMap.put("Customer", new CellDefinitionType(3, CellType.STRING, style));
        }
        cellMap.put("Project", new CellDefinitionType(4, CellType.STRING, style));
        cellMap.put("Part", new CellDefinitionType(5, CellType.STRING, style));
        cellMap.put("Work length", new CellDefinitionType(6, CellType.NUMERIC, style));
        cellMap.put("Invoice length", new CellDefinitionType(7, CellType.NUMERIC, style));


        int startRowNumber = isCutomerReport ? 5 : 0;

        if (isCutomerReport) {
            XSSFFont font = sheet.getWorkbook().createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 16);
            XSSFCellStyle customerStyle = sheet.getWorkbook().createCellStyle();
            customerStyle.setFont(font);
            customerStyle.setAlignment(HorizontalAlignment.CENTER);
            customerStyle.setVerticalAlignment(VerticalAlignment.CENTER);


            XSSFRow customer = sheet.createRow(0);

            XSSFCell customerCell = customer.createCell(0, CellType.STRING);
            customerCell.setCellValue("Work Report for " + filterDto.getCustomerProjectPartDtos().get(0).getCustomerName());
            customerCell.setCellStyle(customerStyle);

            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));




            XSSFRow dates = sheet.createRow(1);
            XSSFCell rangeCell = dates.createCell(0, CellType.STRING);
            rangeCell.setCellValue("Dates " + filterDto.getDateFrom() + " - " + filterDto.getDateTo());
            rangeCell.setCellStyle(customerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));
        }

        XSSFRow header = sheet.createRow(startRowNumber);
        int hi=0;
        for (String s : cellMap.keySet()) {
            header.createCell(hi, CellType.STRING).setCellValue(s);
            hi++;
        }

        for (int i=0; i < tasks.size(); i++) {
            Work workTask = tasks.get(i);
            XSSFRow row = sheet.createRow(startRowNumber + i + 1);


            XSSFCell date = row.createCell(0, CellType.NUMERIC);
            date.setCellValue(workTask.getDate());
            date.setCellStyle(dateStype);

            XSSFCell realizator = row.createCell(1, CellType.STRING);
            realizator.setCellValue(workTask.getRealizator().getFullName());
            realizator.setCellStyle(style);

            XSSFCell trackId = row.createCell(2, CellType.STRING);
            trackId.setCellValue(workTask.getTrackId());
            trackId.setCellStyle(style);

            Part part = workTask.getPart();
            XSSFCell partCell = row.createCell(5, CellType.STRING);
            partCell.setCellValue(part.getName());
            partCell.setCellStyle(style);

            Project project = part.getProject();
            XSSFCell projectCell = row.createCell(4, CellType.STRING);
            projectCell.setCellValue(project.getName());
            projectCell.setCellStyle(style);

            if (!isCutomerReport) {
                Customer customer = project.getCustomer();
                XSSFCell customerCell = row.createCell(3, CellType.STRING);
                customerCell.setCellValue(customer.getName());
                customerCell.setCellStyle(style);
            }

            XSSFCell workLengthCell = row.createCell(6, CellType.NUMERIC);
            workLengthCell.setCellValue(workTask.getWorkLength());
            workLengthCell.setCellStyle(style);

            XSSFCell invoiceLengthCell = row.createCell(7, CellType.NUMERIC);
            invoiceLengthCell.setCellValue(workTask.getInvoiceLength());
            invoiceLengthCell.setCellStyle(style);


        }
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
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
