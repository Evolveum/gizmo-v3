package com.evolveum.gizmo.component;

import com.evolveum.gizmo.component.behavior.DateRangePickerBehavior;
import com.evolveum.gizmo.component.form.CustomerProjectPartSearchPanel;
import com.evolveum.gizmo.component.form.EmptyOnChangeAjaxBehavior;
import com.evolveum.gizmo.component.form.MultiselectDropDownInput;
import com.evolveum.gizmo.component.modal.MainPopupDialog;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import com.evolveum.gizmo.security.GizmoPrincipal;
import com.evolveum.gizmo.security.SecurityUtils;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.web.app.PageAppTemplate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
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
import org.apache.wicket.request.cycle.RequestCycle;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractReportTab extends SimplePanel {

    protected static final String ID_FORM = "form";
    protected static final String ID_FROM = "from";
    protected static final String ID_TO = "to";
    protected static final String ID_REALIZATOR = "realizator";
    protected static final String ID_CUSTOMER = "customer";

    protected static final String ID_BTN_PREVIOUS = "previous";
    protected static final String ID_BTN_NEXT = "next";
    protected static final String ID_MONTH = "month";

    protected static final String ID_PREVIEW = "preview";
    protected static final String ID_EXPORT = "export";
    protected static final String ID_CONFIRM_DOWNLOAD = "confirmDownload";

    private IModel<ReportFilterDto> model;
    protected Form<?> form;

    public AbstractReportTab(String id) {
        super(id);
        setOutputMarkupId(true);
        this.model = Model.of(loadOrCreateFilter());
    }

    protected void initPanelLayout() {
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
                new PropertyModel<>(getFilterModel(), ReportFilterDto.F_DATE_FROM), "dd/MM/yyyy");
        from.setOutputMarkupId(true);
        from.add(new EmptyOnChangeAjaxBehavior());
        from.add(new DateRangePickerBehavior() {
            @Override protected void onEvent(AjaxRequestTarget target) { setDateTo(target); }
        });
        form.add(from);

        LocalDateTextField to = new LocalDateTextField(ID_TO,
                new PropertyModel<>(getFilterModel(), ReportFilterDto.F_DATE_TO), "dd/MM/yyyy");
        to.setOutputMarkupId(true);
        to.add(new DateRangePickerBehavior());
        form.add(to);

        MultiselectDropDownInput<User> realizators = new MultiselectDropDownInput<>(ID_REALIZATOR,
                new PropertyModel<>(model, ReportFilterDto.F_REALIZATORS),
                GizmoUtils.createUsersModel(getPageTemplate()),
                GizmoUtils.createUserChoiceRenderer());
        realizators.setOutputMarkupId(true);
        form.add(realizators);

        if (includeCustomerSearch()) {
            CustomerProjectPartSearchPanel customerProjectSearchPanel = new CustomerProjectPartSearchPanel(ID_CUSTOMER, new PropertyModel<>(getFilterModel(), ReportFilterDto.F_PROJECT_SEARCH_SETTINGS));
            customerProjectSearchPanel.setOutputMarkupId(true);
            form.add(customerProjectSearchPanel);
        }

        form.add(new AjaxSubmitButton(ID_PREVIEW, createStringResource("PageReports.button.preview")) {
            @Override protected void onSubmit(AjaxRequestTarget target) {
                onPreview(target);
            }
            @Override protected void onError(AjaxRequestTarget target) {
                targetAddFeedback(target);
                super.onError(target);
            }
        });

        buildResultsUI();

        showDownloadModal();
    }

    @Override
    protected void initLayout() {
        initPanelLayout();
    }

    protected boolean includeCustomerSearch() {
        return false;
    }

    protected abstract void buildResultsUI();

    protected abstract void onPreview(AjaxRequestTarget target);

    protected abstract Component createDownloadContent(String contentId);

    protected void beforeOpenDownload(AjaxRequestTarget target, Component content) {
    }

    protected void afterCalendarNavigation(AjaxRequestTarget target) {}


    protected IModel<ReportFilterDto> getFilterModel() { return model; }

    protected void setDateTo(AjaxRequestTarget target) {
        ReportFilterDto f = getFilterModel().getObject();
        f.setDateTo(f.getDateFrom().with(TemporalAdjusters.lastDayOfMonth()));
        LocalDateTextField dateTo = (LocalDateTextField) get(ID_FORM + ":" + ID_TO);
        target.add(dateTo);
    }

    protected void previousClicked(AjaxRequestTarget target) {
        ReportFilterDto f = model.getObject();
        LocalDate from = f.getDateFrom();
        f.setDateFrom(from.minusMonths(1));
        f.setDateTo(f.getDateFrom().with(TemporalAdjusters.lastDayOfMonth()));
        handleCalendarNavigation(target, f);
    }

    protected void nextClicked(AjaxRequestTarget target) {
        ReportFilterDto f = model.getObject();
        LocalDate from = f.getDateFrom();
        f.setDateFrom(from.plusMonths(1));
        f.setDateTo(f.getDateFrom().with(TemporalAdjusters.lastDayOfMonth()));
        handleCalendarNavigation(target, f);
    }

    private void handleCalendarNavigation(AjaxRequestTarget target, ReportFilterDto f) {
        GizmoAuthWebSession.getSession().setDashboardFilter(f);
        target.add(form);
        afterCalendarNavigation(target);
    }

    protected void showDownloadModal() {
        MainPopupDialog confirm = new MainPopupDialog(ID_CONFIRM_DOWNLOAD);
        confirm.setOutputMarkupId(true);
        addOrReplace(confirm);

        Component content = createDownloadContent(ModalDialog.CONTENT_ID);
        content.add(AttributeModifier.append("class", "modal-content"));
        confirm.setContent(content);

        form.add(new AjaxSubmitButton(ID_EXPORT, createStringResource("PageReports.download")) {
            @Override protected void onSubmit(AjaxRequestTarget target) {
                beforeOpenDownload(target, content);
                confirm.open(target);
            }
            @Override protected void onError(AjaxRequestTarget target) {
                targetAddFeedback(target);
                super.onError(target);
            }
        });
    }

    protected void targetAddFeedback(AjaxRequestTarget target) {
        WebPage page = (WebPage) getPage();
        if (page instanceof PageAppTemplate template) {
            target.add(template.getFeedbackPanel());
        }
    }

    protected void handleGuiExceptionFromPanel(String message, Exception e, AjaxRequestTarget target) {
        if (target == null) {
            target = RequestCycle.get().find(AjaxRequestTarget.class).orElse(null);
        }
        Page page = getPage();
        if (page instanceof PageAppTemplate tem) {
            tem.handleGuiException(tem, message, e, target);
        } else {
            error(message + ": " + (e.getMessage() != null ? e.getMessage() : ""));
            if (target != null) target.add(this);
        }
    }

    private ReportFilterDto loadOrCreateFilter() {
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
        return filter;
    }
}
