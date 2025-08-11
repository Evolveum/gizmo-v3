package com.evolveum.gizmo.component;

import com.evolveum.gizmo.component.behavior.DateRangePickerBehavior;
import com.evolveum.gizmo.component.form.CustomerProjectPartSearchPanel;
import com.evolveum.gizmo.component.form.EmptyOnChangeAjaxBehavior;
import com.evolveum.gizmo.component.form.MultiselectDropDownInput;
import com.evolveum.gizmo.component.modal.DownloadReportConfigPanel;
import com.evolveum.gizmo.component.modal.MainPopupDialog;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.data.provider.SummaryPartsDataProvider;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import com.evolveum.gizmo.security.GizmoPrincipal;
import com.evolveum.gizmo.security.SecurityUtils;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.web.app.PageAppTemplate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalDialog;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class ReportSalesTab extends SimplePanel{
    private static final String ID_FORM = "form";
    private static final String ID_FROM = "from";
    private static final String ID_TO = "to";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_CUSTOMER = "customer";

    private static final String ID_BTN_PREVIOUS = "previous";
    private static final String ID_BTN_NEXT = "next";
    private static final String ID_MONTH = "month";

    private static final String ID_SUMMARY = "summary"; // miesto pôvodnej tabuľky

    private static final String ID_PREVIEW = "preview";
    private static final String ID_EXPORT = "export";

    public static final String ID_CONFIRM_DOWNLOAD = "confirmDownload";

    private IModel<ReportFilterDto> model;
    private Form<?> form;
    private SummaryPartsPanel summaryPanel;

    public ReportSalesTab(String id) {
        super(id);
        setOutputMarkupId(true);

        ReportFilterDto filter = GizmoAuthWebSession.getSession().getReportFilterDto();
        if (filter == null) {
            filter = new ReportFilterDto();
            filter.setDateFrom(GizmoUtils.createWorkDefaultFrom());
            filter.setDateTo(GizmoUtils.createWorkDefaultTo());

            GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
            User defaultRealizator = principal.getUser();
            List<User> realizators = new ArrayList<>();
            realizators.add(defaultRealizator);
            filter.setRealizators(realizators);
        }
        this.model = Model.of(filter);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        initPanelLayout();
    }

    private IModel<ReportFilterDto> getFilterModel() {
        return model;
    }

    private void setDateTo(AjaxRequestTarget target) {
        ReportFilterDto filterDto = getFilterModel().getObject();
        filterDto.setDateTo(filterDto.getDateFrom().with(TemporalAdjusters.lastDayOfMonth()));
        LocalDateTextField dateTo = (LocalDateTextField) get(ID_FORM + ":" + ID_TO);
        target.add(dateTo);
    }

    private void initPanelLayout() {
        form = new Form<>(ID_FORM, model);
        form.setOutputMarkupId(true);
        addOrReplace(form);

        Label month = new Label(ID_MONTH, new PropertyModel<>(model, ReportFilterDto.F_MONTH_YEAR));
        month.setOutputMarkupId(true);
        form.add(month);

        AjaxLink<String> prev = new AjaxLink<>(ID_BTN_PREVIOUS, createStringResource("fa-chevron")) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                previousClicked(target);
            }
        };
        prev.setOutputMarkupId(true);
        form.add(prev);

        AjaxLink<String> next = new AjaxLink<>(ID_BTN_NEXT, createStringResource("fa-chevron")) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                nextClicked(target);
            }
        };
        next.setOutputMarkupId(true);
        form.add(next);

        LocalDateTextField from = new LocalDateTextField(ID_FROM,
                new PropertyModel<>(getFilterModel(), ReportFilterDto.F_DATE_FROM),
                "dd/MM/yyyy");
        from.setOutputMarkupId(true);
        from.add(new EmptyOnChangeAjaxBehavior());
        from.add(new DateRangePickerBehavior() {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                setDateTo(target);
            }
        });
        form.add(from);

        LocalDateTextField to = new LocalDateTextField(ID_TO,
                new PropertyModel<>(getFilterModel(), ReportFilterDto.F_DATE_TO),
                "dd/MM/yyyy");
        to.setOutputMarkupId(true);
        to.add(new DateRangePickerBehavior());
        form.add(to);

        MultiselectDropDownInput<User> realizators = new MultiselectDropDownInput<>(ID_REALIZATOR,
                new PropertyModel<>(model, ReportFilterDto.F_REALIZATORS),
                GizmoUtils.createUsersModel(getPageTemplate()),
                GizmoUtils.createUserChoiceRenderer());
        realizators.setOutputMarkupId(true);
        form.add(realizators);

        CustomerProjectPartSearchPanel customerProjectSearchPanel = new CustomerProjectPartSearchPanel(
                ID_CUSTOMER, new PropertyModel<>(getFilterModel(), ReportFilterDto.F_PROJECT_SEARCH_SETTINGS));
        customerProjectSearchPanel.setOutputMarkupId(true);
        form.add(customerProjectSearchPanel);

        AjaxSubmitButton preview = new AjaxSubmitButton(ID_PREVIEW,
                createStringResource("PageReports.button.preview")) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                refreshSummary(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(getPageTemplate().getFeedbackPanel());
                super.onError(target);
            }
        };
        form.add(preview);

        SummaryPartsDataProvider provider = new SummaryPartsDataProvider(getPageTemplate());
        summaryPanel = new SummaryPartsPanel(ID_SUMMARY, provider, getFilterModel());
        summaryPanel.setOutputMarkupId(true);
        addOrReplace(summaryPanel);

        showDownloadModal(form);
    }

    private void showDownloadModal(Form form) {
        MainPopupDialog confirmDownload = new MainPopupDialog(ID_CONFIRM_DOWNLOAD);
        confirmDownload.setOutputMarkupId(true);
        add(confirmDownload);

        DownloadReportConfigPanel content = new DownloadReportConfigPanel(ModalDialog.CONTENT_ID, getFilterModel());
        content.add(AttributeModifier.append("class", "modal-content"));
        confirmDownload.setContent(content);

        AjaxSubmitButton download = new AjaxSubmitButton(ID_EXPORT, createStringResource("PageReports.download")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                confirmDownload.open(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                WebPage page = (WebPage) getPage();
                if (page instanceof PageAppTemplate template) {
                    target.add(template.getFeedbackPanel());
                }
                super.onError(target);
            }
        };
        form.add(download);

    }

    private void refreshSummary(AjaxRequestTarget target) {
        WebPage page = (WebPage) getPage();
        if (page instanceof PageAppTemplate template) {
            target.add(template.getFeedbackPanel());
        }
        ReportFilterDto reportFilter = model.getObject();
        GizmoAuthWebSession.getSession().setReportFilterDto(reportFilter);
        target.add(summaryPanel);
    }

    private void previousClicked(AjaxRequestTarget target) {
        ReportFilterDto filter = model.getObject();
        LocalDate from = filter.getDateFrom();
        filter.setDateFrom(from.minusMonths(1));
        filter.setDateTo(filter.getDateFrom().with(TemporalAdjusters.lastDayOfMonth()));
        handleCalendarNavigation(target, filter);
    }

    private void nextClicked(AjaxRequestTarget target) {
        ReportFilterDto filter = model.getObject();
        LocalDate from = filter.getDateFrom();
        filter.setDateFrom(from.plusMonths(1));
        filter.setDateTo(filter.getDateFrom().with(TemporalAdjusters.lastDayOfMonth()));
        handleCalendarNavigation(target, filter);
    }

    private void handleCalendarNavigation(AjaxRequestTarget target, ReportFilterDto filter) {
        GizmoAuthWebSession.getSession().setDashboardFilter(filter);
        target.add(form);
        target.add(summaryPanel);
    }
}