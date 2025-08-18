package com.evolveum.gizmo.component.modal;

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
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import org.apache.wicket.model.IModel;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DownloadSalesReportConfigPanel extends AbstractExcelDownloadPanel {

    private static final Set<String> EXCLUDED_METHODS = Set.of("getFrom","getTo","getTrackId");

    public DownloadSalesReportConfigPanel(String id, IModel<ReportFilterDto> model) {
        super(id, model);
    }

    @Override protected String filePrefix() { return "sales"; }

    @Override protected String contextSuffix(ReportFilterDto f) {
        return f.getRealizators().size() == 1 ? slug(f.getRealizators().getFirst().getFamilyName()) : "";
    }

    @Override protected boolean supportsPerUser() { return true; }

    @Override
    protected void generateWorkbook(XSSFWorkbook wb, ReportFilterDto f, boolean perUser) throws Exception {
        if (perUser) {
            generateReportPerUser(wb, f);
        } else {
            generateUsersReport(wb, "Sales report", f, ReportType.GENERIC);
        }
    }

    private void generateReportPerUser(XSSFWorkbook workbook, ReportFilterDto filterDto) {
        List<User> realizators = filterDto.getRealizators().isEmpty()
                ? getPageTemplate().getUserRepository().findAllEnabledUsers()
                : filterDto.getRealizators();

        for (User realizator : realizators) {
            List<AbstractTask> tasks = loadWork(realizator, filterDto);
            if (tasks.isEmpty()) continue;

            String sheetName = realizator.getFullName() + "(" + realizator.getId() + ")";
            XSSFSheet sheet = getSheet(workbook, sheetName);

            ReportFilterDto perUserFilter = filterForSingleRealizator(filterDto, realizator);
            writeSummaryTable(workbook, sheet, perUserFilter);

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

    private void generateUsersReport(XSSFWorkbook workbook, String sheetName,
                                     ReportFilterDto filterDto, ReportType reportType) {
        List<AbstractTask> tasks = listLoggedWork(filterDto);

        XSSFSheet sheet = getSheet(workbook, sheetName);

        writeSummaryTable(workbook, sheet, filterDto);

        generateExcel(workbook, sheetName, tasks, reportType);
    }

    private void writeSummaryTable(XSSFWorkbook workbook, XSSFSheet sheet, ReportFilterDto filterDto) {
        CellStyle header = headerStyle(workbook);
        CellStyle text = textStyle(workbook);

        int rowIdx = 1;

        XSSFRow head = sheet.createRow(rowIdx++);
        XSSFCell h0 = head.createCell(0, CellType.STRING);
        h0.setCellValue("User"); h0.setCellStyle(header);
        XSSFCell h1 = head.createCell(1, CellType.STRING);
        h1.setCellValue("Project"); h1.setCellStyle(header);
        XSSFCell h2 = head.createCell(2, CellType.STRING);
        h2.setCellValue("Time (h)"); h2.setCellStyle(header);

        SummaryPartsDataProvider provider = new SummaryPartsDataProvider(getPageTemplate());
        List<PartSummary> rows = provider.createSummary(filterDto);

        double totalHours = 0d;
        for (PartSummary s : rows) {
            XSSFRow r = sheet.createRow(rowIdx++);
            XSSFCell c0 = r.createCell(0, CellType.STRING);
            c0.setCellValue(s.getFullName()); c0.setCellStyle(text);
            XSSFCell c1 = r.createCell(1, CellType.STRING);
            c1.setCellValue(s.getName()); c1.setCellStyle(text);
            XSSFCell c2 = r.createCell(2, CellType.NUMERIC);
            c2.setCellValue(s.getLength()); c2.setCellStyle(text);
            totalHours += s.getLength();
        }

        CellStyle sumStyle = headerStyle(workbook);
        XSSFRow sumRow = sheet.createRow(rowIdx++);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx - 1, rowIdx - 1, 0, 1));
        XSSFCell sumLabel = sumRow.createCell(0, CellType.STRING);
        sumLabel.setCellValue("Summary"); sumLabel.setCellStyle(sumStyle);
        XSSFCell totalCell = sumRow.createCell(2, CellType.NUMERIC);
        totalCell.setCellValue(totalHours); totalCell.setCellStyle(sumStyle);

        for (int c = 0; c <= 2; c++) sheet.autoSizeColumn(c);

        sheet.createRow(rowIdx++);
    }

    private ReportFilterDto filterForSingleRealizator(ReportFilterDto base, User realizator) {
        ReportFilterDto f = new ReportFilterDto();
        f.setDateFrom(base.getDateFrom());
        f.setDateTo(base.getDateTo());
        f.setCustomerProjectPartDtos(base.getCustomerProjectPartDtos());
        f.setRealizators(List.of(realizator));
        return f;
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
        var cells = WorkCellType.getCellsForReport(reportType)
                .stream().filter(c -> !EXCLUDED_METHODS.contains(c.getGetMethod()))
                .collect(Collectors.toList());
        var defs = new ArrayList<CellDefinitionType>();

        int j = 0;
        for (WorkCellType cell : cells) {
            Class<?> fieldType = cell.getType();
            CellStyle style = LocalDate.class.equals(fieldType)
                    ? dateStyle(workbook)
                    : textStyle(workbook);

            defs.add(new CellDefinitionType(cell.getDisplayName(), j, cell.getType(), style, cell.getGetMethod()));
            j++;
        }

        int startRowNumber = 0;
        XSSFSheet sheet = getSheet(workbook, sheetName);
        if (sheet.getLastRowNum() != 0) startRowNumber = sheet.getLastRowNum() + 2;

        XSSFRow header = sheet.createRow(startRowNumber);
        CellStyle headerStyle = headerStyle(workbook);
        for (CellDefinitionType def : defs) {
            XSSFCell headerCell = header.createCell(def.getPosition(), CellType.STRING);
            headerCell.setCellValue(def.getDisplayName());
            headerCell.setCellStyle(headerStyle);
        }

        for (int i = 0; i < tasks.size(); i++) {
            T workTask = tasks.get(i);
            XSSFRow row = sheet.createRow(startRowNumber + i + 1);
            for (CellDefinitionType def : defs) {
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
