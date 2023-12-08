package com.evolveum.gizmo.web.app;

import com.evolveum.gizmo.component.form.MultiselectDropDownInput;
import com.evolveum.gizmo.data.*;
import com.evolveum.gizmo.data.provider.AbstractTaskDataProvider;
import com.evolveum.gizmo.data.provider.SummaryPartsDataProvider;
import com.evolveum.gizmo.dto.PartSummary;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;
import com.evolveum.gizmo.component.AjaxSubmitButton;
import com.evolveum.gizmo.component.GizmoTabbedPanel;
import com.evolveum.gizmo.component.SummaryPartsPanel;
import com.evolveum.gizmo.component.behavior.DateRangePickerBehavior;
import com.evolveum.gizmo.component.data.TablePanel;
import sk.lazyman.gizmo.data.*;
import com.evolveum.gizmo.data.provider.ReportDataProvider;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@MountPath("/app/reports")
public class PageReports extends PageAppTemplate {

    private static final String ID_FORM = "form";
    private static final String ID_FROM = "from";
    private static final String ID_TO = "to";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_PROJECT = "project";
    private static final String ID_PER_USER = "perUser";
    private static final String ID_SHOW_SUMMARY = "showSummary";

    private static final String ID_DETAILS = "details";

    private static final String ID_PREVIEW = "preview";

    private IModel<ReportFilterDto> model;

    public PageReports(){
        model = new LoadableModel<>(false) {

            @Override
            protected ReportFilterDto load() {
                GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
                ReportFilterDto filter = session.getReportFilterDto();
                if (filter == null) {
                    filter = new ReportFilterDto();
                    filter.setDateFrom(GizmoUtils.createWorkDefaultFrom());
                    filter.setDateTo(GizmoUtils.createWorkDefaultTo());
                }
                return filter;
            }
        };
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        initLayout();
    }

    private IModel<ReportFilterDto> getFilterModel() {
        return model;
    }

    private void initLayout() {
        Form<ReportFilterDto> form = new Form<>(ID_FORM);
        form.setOutputMarkupId(true);
        add(form);

        LocalDateTextField from = new LocalDateTextField(ID_FROM, new PropertyModel<>(getFilterModel(), ReportFilterDto.F_DATE_FROM), "dd/MM/yyyy");
        from.setOutputMarkupId(true);
        from.add(new DateRangePickerBehavior());
        form.add(from);

        LocalDateTextField to = new LocalDateTextField(ID_TO, new PropertyModel<>(getFilterModel(), ReportFilterDto.F_DATE_TO), "dd/MM/yyyy");
        to.setOutputMarkupId(true);
        to.add(new DateRangePickerBehavior());
        form.add(to);


        MultiselectDropDownInput<User> realizators = new
                MultiselectDropDownInput<>(ID_REALIZATOR,
                new PropertyModel<>(model, ReportFilterDto.F_REALIZATORS),
                GizmoUtils.createUsersModel(this),
                GizmoUtils.createUserChoiceRenderer());
        realizators.setOutputMarkupId(true);
        form.add(realizators);

        MultiselectDropDownInput<CustomerProjectPartDto> projectCombo = new MultiselectDropDownInput<>(ID_PROJECT,
                new PropertyModel<>(model, ReportFilterDto.F_CUSTOM_PROJECT_PART),
                GizmoUtils.createCustomerProjectPartList(this, true, true, true),
                GizmoUtils.createCustomerProjectPartRenderer());
        form.add(projectCombo);

        AjaxCheckBox perUser = new AjaxCheckBox(ID_PER_USER, new PropertyModel<>(model, ReportFilterDto.F_PER_USER)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                model.getObject().setPerUser(!model.getObject().isPerUser());
            }
        };
        perUser.setOutputMarkupId(true);
        form.add(perUser);

        AjaxCheckBox showSummary = new AjaxCheckBox(ID_SHOW_SUMMARY, new PropertyModel<>(model, ReportFilterDto.F_SUMMARY)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                model.getObject().setSummary(!model.getObject().isSummary());
            }
        };
        form.add(showSummary);

        AjaxSubmitButton preview = new AjaxSubmitButton(ID_PREVIEW, createStringResource("PageReports.button.preview")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                refreshTable(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(getFeedbackPanel());
                super.onError(target);
            }
        };
        form.add(preview);

        DownloadLink exportExcel = new DownloadLink("export", new IModel<>()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public File getObject()
            {
                File tempFile = new File("export.xlsx");
                try (XSSFWorkbook workbook = new XSSFWorkbook()) {

//                    style.setShrinkToFit(true);
                    ReportFilterDto filterDto = model.getObject();
                    if (filterDto.isPerUser()) {
                        generateReportPerUser(workbook, filterDto);
                    } else {
                        generateUsersReport(workbook, filterDto);
                    }

                    if (filterDto.isSummary()) {
                        generateSummaryReport(workbook, filterDto);
                    }

                    FileOutputStream os = new FileOutputStream(tempFile);
                    workbook.write(os);

                } catch (Exception e) {
                    System.out.println("exceltion " + e.getMessage());
                    e.printStackTrace();
                }

                return tempFile;
            }
        }, "Export_" + new Date(System.currentTimeMillis()).toString() + ".xlsx").setCacheDuration(Duration.ofMillis(0)).setDeleteAfterDownload(true);
        form.add(exportExcel);





        List<ITab> tabs = createTabs();
        GizmoTabbedPanel<ITab> tabbedPanel = new GizmoTabbedPanel<>(ID_DETAILS, tabs);
        tabbedPanel.setOutputMarkupId(true);
        add(tabbedPanel);
    }

    private List<ITab> createTabs() {
        List<ITab> tabs = new ArrayList<>();
        tabs.add(new AbstractTab(createStringResource("PageReports.table")) {
            @Override
            public WebMarkupContainer getPanel(String id) {
                ReportDataProvider provider = new ReportDataProvider(PageReports.this);
                provider.setFilter(model.getObject());

                List<IColumn<AbstractTask, String>> columns = createColumns();
                TablePanel<AbstractTask> table = new TablePanel<>(id, provider, columns, 50);
                table.setOutputMarkupId(true);
                return table;
            }
        });
        tabs.add(new AbstractTab(createStringResource("PageReports.summary")) {
            @Override
            public WebMarkupContainer getPanel(String id) {
                SummaryPartsDataProvider partsProvider = new SummaryPartsDataProvider(PageReports.this);
                return new SummaryPartsPanel(id, partsProvider, getFilterModel());
            }

            @Override
            public boolean isVisible() {
                return getFilterModel().getObject().isSummary();
            }
        });
        return tabs;
    }

    private void generateReportPerUser(XSSFWorkbook workbook, ReportFilterDto filterDto) {
        CellStyle style = workbook.createCellStyle();

        CellStyle dateStype = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStype.setDataFormat(
                createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

        List<User> realizators = filterDto.getRealizators().isEmpty() ? getUserRepository().findAllEnabledUsers() : filterDto.getRealizators();
        for (User realizator : realizators) {
            XSSFSheet sheet = workbook.createSheet(realizator.getFullName() + "(" + realizator.getId() +")");
//                        sheet.setDefaultColumnWidth(30);
            QAbstractTask task = QAbstractTask.abstractTask;
            JPAQuery query = GizmoUtils.createWorkQuery(PageReports.this.getEntityManager());
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
            List<Work> tasks = query.select(task).fetch();
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
            for (int i=0; i < tasks.size(); i++) {
                Work workTask = tasks.get(i);
                XSSFRow row = sheet.createRow(i+1);

                XSSFCell date = row.createCell(0, CellType.NUMERIC);
                date.setCellValue(workTask.getDate());
                date.setCellStyle(dateStype);

                XSSFCell trackId = row.createCell(1, CellType.STRING);
                trackId.setCellValue(workTask.getTrackId());
                trackId.setCellStyle(style);

                Part part = workTask.getPart();
                XSSFCell partCell = row.createCell(4, CellType.STRING);
                partCell.setCellValue(part.getName());
                partCell.setCellStyle(style);

                Project project = part.getProject();
                XSSFCell projectCell = row.createCell(3, CellType.STRING);
                projectCell.setCellValue(project.getName());
                projectCell.setCellStyle(style);

                Customer customer = project.getCustomer();
                XSSFCell customerCell = row.createCell(2, CellType.STRING);
                customerCell.setCellValue(customer.getName());
                customerCell.setCellStyle(style);

                XSSFCell workLengthCell = row.createCell(5, CellType.NUMERIC);
                workLengthCell.setCellValue(workTask.getWorkLength());
                workLengthCell.setCellStyle(style);

                XSSFCell invoiceLengthCell = row.createCell(6, CellType.NUMERIC);
                invoiceLengthCell.setCellValue(workTask.getInvoiceLength());
                invoiceLengthCell.setCellStyle(style);


            }
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);

        }
    }

    private void generateUsersReport(XSSFWorkbook workbook, ReportFilterDto filterDto) {
        CellStyle style = workbook.createCellStyle();

        CellStyle dateStype = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStype.setDataFormat(
                createHelper.createDataFormat().getFormat("dd/mm/yyyy"));


        XSSFSheet sheet = workbook.createSheet("Work log");
        sheet.setDefaultColumnWidth(30);

            QAbstractTask task = QAbstractTask.abstractTask;
            JPAQuery query = GizmoUtils.createWorkQuery(PageReports.this.getEntityManager());

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



            List<Work> tasks = query.select(task).fetch();
            generateExcel(sheet, tasks, style, dateStype);


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

        SummaryPartsDataProvider partsProvider = new SummaryPartsDataProvider(PageReports.this);
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

    private void generateExcel(XSSFSheet sheet, List<Work> tasks, CellStyle style, CellStyle dateStype) {

        XSSFRow header = sheet.createRow(0);

        XSSFCell dateHeaderCell = header.createCell(0, CellType.STRING);
        dateHeaderCell.setCellValue("Date");
        XSSFCell realizatorHeader = header.createCell(1, CellType.STRING);
        realizatorHeader.setCellValue("Realizator");
        XSSFCell trackIdHeaderCell = header.createCell(2, CellType.STRING);
        trackIdHeaderCell.setCellValue("TrackId");

        header.createCell(3, CellType.STRING).setCellValue("Customer");
        header.createCell(4, CellType.STRING).setCellValue("Project");
        header.createCell(5, CellType.STRING).setCellValue("Part");
        header.createCell(6, CellType.STRING).setCellValue("Work length");
        header.createCell(7, CellType.STRING).setCellValue("Invoice length");
        for (int i=0; i < tasks.size(); i++) {
            Work workTask = tasks.get(i);
            XSSFRow row = sheet.createRow(i+1);

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

            Customer customer = project.getCustomer();
            XSSFCell customerCell = row.createCell(3, CellType.STRING);
            customerCell.setCellValue(customer.getName());
            customerCell.setCellStyle(style);

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

    private void refreshTable(AjaxRequestTarget target) {
        target.add(getFeedbackPanel());
        ReportFilterDto reportFilter = model.getObject();

        GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
        session.setReportFilterDto(reportFilter);


        target.add(get(ID_DETAILS));
    }

    private List<IColumn<AbstractTask, String>> createColumns() {
        List<IColumn<AbstractTask, String>> columns = new ArrayList<>();

        columns.add(new PropertyColumn<>(createStringResource("AbstractTask.date"), AbstractTask.F_DATE));
        columns.add(GizmoUtils.createWorkInvoiceColumn(this));
        columns.add(GizmoUtils.createAbstractTaskRealizatorColumn(this));
        columns.add(GizmoUtils.createWorkProjectColumn(this));
        columns.add(new PropertyColumn<>(createStringResource("AbstractTask.description"), AbstractTask.F_DESCRIPTION));
        columns.add(new PropertyColumn<>(createStringResource("PageReports.trackId"), AbstractTask.F_TRACK_ID));

        return columns;
    }


}
