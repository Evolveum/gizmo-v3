package com.evolveum.gizmo.component.modal;

import com.evolveum.gizmo.data.AbstractTask;
import com.evolveum.gizmo.data.QAbstractTask;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.data.Work;
import com.evolveum.gizmo.data.provider.ReportDataProvider;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.util.GizmoUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import org.apache.wicket.model.IModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DownloadOverviewReportConfigPanel extends AbstractExcelDownloadPanel {

    public DownloadOverviewReportConfigPanel(String id, IModel<ReportFilterDto> model) {
        super(id, model);
    }

    @Override protected String filePrefix() { return "overview"; }

    @Override protected String contextSuffix(ReportFilterDto f) {
        return f.getRealizators().size() == 1 ? slug(f.getRealizators().getFirst().getFamilyName()) : "";
    }

    @Override protected boolean supportsPerUser() { return true; }

    @Override
    protected void generateWorkbook(XSSFWorkbook wb, ReportFilterDto f, boolean perUser) throws Exception {
        if (perUser) {
            generateReportPerUser(wb, f);
        } else {
            generateUsersReport(wb, "Overview report", f);
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

        if (filterDto.getDateFrom() != null) predicates.and(task.date.goe(filterDto.getDateFrom()));
        if (filterDto.getDateTo() != null) predicates.and(task.date.loe(filterDto.getDateTo()));
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

    private void generateUsersReport(XSSFWorkbook workbook, String sheetName, ReportFilterDto filterDto) {
        List<AbstractTask> tasks = listLoggedWork(filterDto);
        generateExcel(workbook, sheetName, tasks, ReportType.GENERIC);
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
                    ? dateStyle(workbook)
                    : textStyle(workbook);

            cellDefinitionTypes.add(new CellDefinitionType(
                    cell.getDisplayName(), j, cell.getType(), style, cell.getGetMethod()
            ));
            j++;
        }

        int startRowNumber = 0;
        XSSFSheet sheet = getSheet(workbook, sheetName);
        if (sheet.getLastRowNum() != 0) {
            startRowNumber = sheet.getLastRowNum() + 2;
        }

        XSSFRow header = sheet.createRow(startRowNumber);
        CellStyle headerStyle = headerStyle(workbook);
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
                    handleGuiExceptionFromPanel("Message.couldntGenerateReport", e, null);
                }
            }
        }
    }
}
