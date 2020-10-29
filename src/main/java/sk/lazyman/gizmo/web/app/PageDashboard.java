/*
 * Copyright 2015 Viliam Repan (lazyman)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sk.lazyman.gizmo.web.app;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.SummaryChartPanel;
import sk.lazyman.gizmo.component.SummaryPanel;
import sk.lazyman.gizmo.component.data.LinkColumn;
import sk.lazyman.gizmo.component.data.LinkIconColumn;
import sk.lazyman.gizmo.component.data.TablePanel;
import sk.lazyman.gizmo.data.AbstractTask;
import sk.lazyman.gizmo.data.Log;
import sk.lazyman.gizmo.data.Work;
import sk.lazyman.gizmo.data.provider.AbstractTaskDataProvider;
import sk.lazyman.gizmo.data.provider.SummaryDataProvider;
import sk.lazyman.gizmo.data.provider.SummaryPartsDataProvider;
import sk.lazyman.gizmo.dto.CustomerProjectPartDto;
import sk.lazyman.gizmo.dto.ReportFilterDto;
import sk.lazyman.gizmo.dto.SummaryPanelDto;
import sk.lazyman.gizmo.repository.AbstractTaskRepository;
import sk.lazyman.gizmo.security.GizmoAuthWebSession;
import sk.lazyman.gizmo.security.GizmoPrincipal;
import sk.lazyman.gizmo.security.SecurityUtils;
import sk.lazyman.gizmo.util.GizmoUtils;
import sk.lazyman.gizmo.util.LoadableModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
@MountPath(value = "/app/dashboard", alt = "/app")
public class PageDashboard extends PageAppTemplate {

    private static final String ID_FORM = "form";
    private static final String ID_FROM = "from";
    private static final String ID_TO = "to";
    private static final String ID_TABLE = "table";
    private static final String ID_PROJECT = "project";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_BTN_DISPLAY = "display";
    private static final String ID_BTN_EMAIL = "email";
    private static final String ID_BTN_PRINT = "print";
    private static final String ID_BTN_NEW_TASK = "task";
    private static final String ID_SUMMARY = "summary";
    private static final String ID_SUMMARY_PARTS = "summaryParts";
    private static final String ID_TYPE = "type";
    private static final String ID_BTN_PREVIOUS = "previous";
    private static final String ID_BTN_NEXT = "next";
    private static final String ID_PROJECT_COMBO = "projectCombo";
    private static final String ID_REALIZATOR_COMBO = "realizatorCombo";
    private static final String ID_PROJECT_MULTI = "projectMulti";
    private static final String ID_REALIZATOR_MULTI = "realizatorMulti";
    private static final String ID_ADVANCED = "advanced";

    private static final String ID_MONTH = "month";

    private IModel<ReportFilterDto> filter;
    private IModel<List<CustomerProjectPartDto>> projects =
            GizmoUtils.createCustomerProjectPartList(this, true, true, false);

    public PageDashboard() {
        filter = new LoadableModel<>(false) {

            @Override
            protected ReportFilterDto load() {
                GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
                ReportFilterDto dto = session.getDashboardFilter();
                if (dto == null) {
                    dto = new ReportFilterDto();
                    dto.setDateFrom(GizmoUtils.createWorkDefaultFrom());
                    dto.setDateTo(GizmoUtils.createWorkDefaultTo());

                    GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
                    dto.getRealizators().add(principal.getUser());

                    session.setDashboardFilter(dto);
                }

                return dto;
            }
        };

        initLayout();
    }

    private IModel<ReportFilterDto> getFilterModel() {
        return filter;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
//        String script = "if (typeof $('#bla').fullCalendar === \"function\") { " +
//                "$(function() { " +
//                "$('#bla').fullCalendar({ " +
//                "plugins: [ 'bootstrap' ], themeSystem: 'bootstrap'}) " +
//                "}); }";
        // put your options and callbacks here

//        String script = "$(function() {\n" +
//                "\n" +
//                "  $('#calendar1').fullCalendar({\n" +
//                "    themeSystem: 'jquery-ui',\n" +
//                "    header: {\n" +
//                "      left: 'prev,next today',\n" +
//                "      center: 'title',\n" +
//                "      right: 'month,agendaWeek,agendaDay,listMonth'\n" +
//                "    },\n" +
//                "    weekNumbers: true,\n" +
//                "    eventLimit: true, // allow \"more\" link when too many events\n" +
//                "    events: 'https://fullcalendar.io/demo-events.json'\n" +
//                "  });\n" +
//                "  \n" +
//                "});";
//
//
//        response.render(OnDomReadyHeaderItem.forScript(script));


//        response.render(CssHeaderItem.forReference(
//                new LessResourceReference(PageDashboard.class, "PageDashboard.less")));
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        form.setOutputMarkupId(true);
        add(form);

        Label month = new Label(ID_MONTH, new PropertyModel<>(filter, "month"));
        month.setOutputMarkupId(true);
        add(month);

        AjaxLink<String> prev = new AjaxLink<>(ID_BTN_PREVIOUS, createStringResource("fa-chevron")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                previousClicked(target);
            }
        };
        prev.setOutputMarkupId(true);
        add(prev);

        AjaxLink<String> next = new AjaxLink<>(ID_BTN_NEXT, createStringResource("fa-chevron")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
               nextClicked(target);
            }
        };
        next.setOutputMarkupId(true);
        add(next);


        SummaryPanel summary = new SummaryPanel(ID_SUMMARY, createSummaryModel());
        add(summary);


//        SummaryPartsPanel summaryParts = new SummaryPartsPanel(ID_SUMMARY_PARTS, partsProvider, filter);
//        add(summaryParts);

        AbstractTaskDataProvider provider = new AbstractTaskDataProvider(this);
        provider.setFilter(filter.getObject());

        List<IColumn> columns = createColumns();
        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 50);
        table.setOutputMarkupId(true);
        add(table);

        SummaryPartsDataProvider partsProvider = new SummaryPartsDataProvider(this);
        SummaryChartPanel chart = new SummaryChartPanel(ID_SUMMARY_PARTS, partsProvider, getFilterModel());
        add(chart);
    }

    private IModel<SummaryPanelDto> createSummaryModel() {
        return new LoadableModel<SummaryPanelDto>(false) {

            @Override
            protected SummaryPanelDto load() {
                SummaryDataProvider provider = new SummaryDataProvider(PageDashboard.this);
                return provider.createSummary(getFilterModel().getObject());
            }
        };
    }

    private void previousClicked(AjaxRequestTarget target) {
        ReportFilterDto workFilter = filter.getObject();
        LocalDate defaultFrom = workFilter.getDateFrom();
        workFilter.setDateFrom(defaultFrom.minusMonths(1));
        LocalDate defaultTo = workFilter.getDateTo();
        workFilter.setDateTo(defaultTo.minusMonths(1));
        handleCalendatNavigation(target, workFilter);
    }

    private void nextClicked(AjaxRequestTarget target) {
        ReportFilterDto workFilter = filter.getObject();
        LocalDate defaultFrom = workFilter.getDateFrom();
        workFilter.setDateFrom(defaultFrom.plusMonths(1));

        LocalDate defaultTo = workFilter.getDateTo();
        workFilter.setDateTo(defaultTo.plusMonths(1));
        handleCalendatNavigation(target, workFilter);
    }

    private void handleCalendatNavigation(AjaxRequestTarget target, ReportFilterDto workFilter) {
        GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
        session.setDashboardFilter(workFilter);
        SummaryPanel summaryPanel = (SummaryPanel) get(ID_SUMMARY);
        if (summaryPanel != null) {
            ((LoadableModel) summaryPanel.getModel()).reset();
        }
        target.add(PageDashboard.this);
    }

    private void updateSearchForm(AjaxRequestTarget target) {
        target.add(get(ID_FORM + ":" + ID_PROJECT), get(ID_FORM + ":" + ID_REALIZATOR));
    }

    //date, length (invoice), realizator, project, description (WORK)
    //date, length (0.0), realizator, customer, description, attachments(icon) (LOG)
    private List<IColumn> createColumns() {
        List<IColumn> columns = new ArrayList<>();

        columns.add(new LinkColumn<AbstractTask>(createStringResource("AbstractTask.date"), AbstractTask.F_DATE) {

            @Override
            protected IModel<String> createLinkModel(final IModel<AbstractTask> rowModel) {
                return new IModel<String>() {
                    @Override
                    public String getObject() {
                        PropertyModel<LocalDate> propertyModel = new PropertyModel<>(rowModel, getPropertyExpression());
                        LocalDate date = propertyModel.getObject();
                        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    }
                };
            }

            @Override
            public void onClick(AjaxRequestTarget target, IModel<AbstractTask> rowModel) {
                AbstractTask task = rowModel.getObject();
                switch (task.getType()) {
                    case LOG:
                        logDetailsPerformed(target, (Log) task);
                        break;
                    case WORK:
                        workDetailsPerformed(target, (Work) task);
                        break;
                }
            }
        });
        columns.add(GizmoUtils.createWorkInvoiceColumn(this));
        columns.add(GizmoUtils.createAbstractTaskRealizatorColumn(this));
        columns.add(GizmoUtils.createWorkProjectColumn(this));
        columns.add(GizmoUtils.createLogCustomerColumn(this));
        columns.add(new PropertyColumn(createStringResource("AbstractTask.description"), AbstractTask.F_DESCRIPTION));
        columns.add(new LinkIconColumn<AbstractTask>(new Model<>("")) {

            @Override
            protected IModel<String> createIconModel(IModel rowModel) {
                return new Model<>("fa fa-lg fa-trash-o text-danger");
            }

            @Override
            protected IModel<String> createTitleModel(IModel rowModel) {
                return PageDashboard.this.createStringResource("PageDashboard.delete");
            }

            @Override
            protected void onClickPerformed(AjaxRequestTarget target, IModel<AbstractTask> rowModel, AjaxLink link) {
                deletePerformed(target, rowModel.getObject());
            }
        });

        return columns;
    }

    private void initButtons(Form form) {

//        AjaxSubmitButton display = new AjaxSubmitButton(ID_BTN_DISPLAY,
//                createStringResource("PageDashboard.display")) {
//
//            @Override
//            protected void onSubmit(AjaxRequestTarget target) {
//                displayPerformed(target);
//            }
//
//            @Override
//            protected void onError(AjaxRequestTarget target) {
//                target.add(getFeedbackPanel());
//            }
//        };
//        form.add(display);
//
//        AjaxSubmitButton email = new AjaxSubmitButton(ID_BTN_EMAIL, createStringResource("PageDashboard.email")) {
//
//            @Override
//            protected void onSubmit(AjaxRequestTarget target) {
//                emailPerformed(target);
//            }
//
//            @Override
//            protected void onError(AjaxRequestTarget target) {
//                target.add(getFeedbackPanel());
//            }
//        };
//        form.add(email);
//
//        AjaxSubmitButton print = new AjaxSubmitButton(ID_BTN_PRINT, createStringResource("PageDashboard.print")) {
//
//            @Override
//            protected void onSubmit(AjaxRequestTarget target) {
//                printPerformed(target);
//            }
//
//            @Override
//            protected void onError(AjaxRequestTarget target) {
//                target.add(getFeedbackPanel());
//            }
//        };
//        form.add(print);

//        SplitButton task = new SplitButton(ID_BTN_NEW_TASK, createStringResource("PageDashboard.newWork")) {
//
//            @Override
//            protected AbstractLink newBaseButton(String markupId, IModel<String> labelModel,
//                                                 IModel<IconType> iconTypeModel) {
//
//                return new BootstrapAjaxLink<>(markupId, labelModel, Buttons.Type.Success, labelModel) {
//
//                    @Override
//                    public void onClick(AjaxRequestTarget target) {
//                        newWorkPerformed(target);
//                    }
//                }.setIconType(iconTypeModel.getObject());
//            }
//
//            @Override
//            protected List<AbstractLink> newSubMenuButtons(String buttonMarkupId) {
//                return createDropDownLinks(buttonMarkupId);
//            }
//        };
//        task.setSize(Buttons.Size.Small).setType(Buttons.Type.Success);
//
//
//        form.add(task);
    }

//    private String createFileName() {
//        WorkFilterDto filterDto = filter.getObject();
//        String timestamp = new Date(System.currentTimeMillis()).toString();
//        List<User> realizators = filterDto.getRealizators();
//        if (realizators.isEmpty()) {
//            return "all_" + timestamp;
//        }
//
//        if (realizators.size() == 1) {
//            return realizators.iterator().next() + timestamp;
//        }
//
//        return "Export_" + timestamp;
//    }
//
//    private List<AbstractLink> createDropDownLinks(String id) {
//        List<AbstractLink> list = new ArrayList<>();
//        list.add(new LabeledLink(id, createStringResource("PageDashboard.newLog")) {
//
//            @Override
//            public void onClick(AjaxRequestTarget target) {
//                newLogPerformed(target);
//            }
//        });
//        list.add(new LabeledLink(id, createStringResource("PageDashboard.newBulk")) {
//
//            @Override
//            public void onClick(AjaxRequestTarget target) {
//                newBulkPerformed(target);
//            }
//        });
//
//
//        return list;
//    }
//
//    private void moveMonth(AjaxRequestTarget target, int add) {
//        WorkFilterDto dto = filter.getObject();
//
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(GizmoUtils.clearTime(dto.getFrom()));
//        cal.add(Calendar.MONTH, add);
//        cal.set(Calendar.DAY_OF_MONTH, 1);
//        dto.setFrom(cal.getTime());
//
//        cal = Calendar.getInstance();
//        cal.setTime(dto.getFrom());
//        cal.add(Calendar.MONTH, 1);
//        cal.add(Calendar.MILLISECOND, -1);
//        dto.setTo(cal.getTime());
//
//        displayPerformed(target);
//        target.add(get(ID_FORM));
//    }

    private void displayPerformed(AjaxRequestTarget target) {
        ReportFilterDto dto = filter.getObject();

        TablePanel table = (TablePanel) get(ID_TABLE);
        AbstractTaskDataProvider provider = (AbstractTaskDataProvider) table.getDataTable().getDataProvider();
        provider.setFilter(dto);
        table.setCurrentPage(0L);

        GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
        session.setDashboardFilter(dto);

        target.add(get(ID_SUMMARY), get(ID_SUMMARY_PARTS), table);
    }

    private void emailPerformed(AjaxRequestTarget target) {
//        PageEmail next = new PageEmail(filter);
//        setResponsePage(next);
    }

//    private void printPerformed(AjaxRequestTarget target) {
//        setResponsePage(new PagePrint(filter));
//    }

    private void newWorkPerformed(AjaxRequestTarget target) {
        setResponsePage(PageWork.class);
    }

    private void newLogPerformed(AjaxRequestTarget target) {
        setResponsePage(PageLog.class);
    }

    private void newBulkPerformed(AjaxRequestTarget target) {
        setResponsePage(PageBulk.class);
    }

    private void workDetailsPerformed(AjaxRequestTarget target, Work work) {
        PageParameters params = new PageParameters();
        params.add(PageWork.WORK_ID, work.getId());

        setResponsePage(PageWork.class, params);
    }

    private void logDetailsPerformed(AjaxRequestTarget target, Log log) {
        PageParameters params = new PageParameters();
        params.add(PageLog.LOG_ID, log.getId());

        setResponsePage(PageLog.class, params);
    }

    private void deletePerformed(AjaxRequestTarget target, AbstractTask task) {
        //todo add confirmation
        try {
            AbstractTaskRepository repository = getAbstractTaskRepository();
            repository.deleteById(task.getId());

            success(createStringResource("Message.successfullyDeleted").getString());
            target.add(getFeedbackPanel(), get(ID_TABLE), get(ID_SUMMARY), get(ID_SUMMARY_PARTS));
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveWork", ex, target);
        }
    }
}
