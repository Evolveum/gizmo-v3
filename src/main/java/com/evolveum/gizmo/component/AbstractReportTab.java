package com.evolveum.gizmo.component;

import com.evolveum.gizmo.component.behavior.DateRangePickerBehavior;
import com.evolveum.gizmo.component.data.MonthNavigationPanel;
import com.evolveum.gizmo.component.form.CustomerProjectPartSearchPanel;
import com.evolveum.gizmo.component.form.EmptyOnChangeAjaxBehavior;
import com.evolveum.gizmo.component.form.MultiselectDropDownInput;
import com.evolveum.gizmo.component.modal.MainPopupDialog;
import com.evolveum.gizmo.data.LabelPart;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.security.GizmoPrincipal;
import com.evolveum.gizmo.security.SecurityUtils;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LabelService;
import com.evolveum.gizmo.util.LoadableModel;
import com.evolveum.gizmo.web.app.PageAppTemplate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalDialog;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractReportTab extends SimplePanel<ReportFilterDto> {

    protected static final String ID_FORM = "form";
    protected static final String ID_FROM = "from";
    protected static final String ID_TO = "to";
    protected static final String ID_REALIZATOR = "realizator";
    protected static final String ID_CUSTOMER = "customer";
    protected static final String ID_LABELS = "labels";

    protected static final String ID_PREVIEW = "preview";
    protected static final String ID_EXPORT = "export";
    protected static final String ID_DOWNLOAD_MODAL = "confirmDownload";


    @SpringBean
    private LabelService labelService;
    private final IModel<ReportFilterDto> model;

    @Override
    public IModel<ReportFilterDto> getModel() {
        return model;
    }

    public AbstractReportTab(String id) {
        super(id);
        setOutputMarkupId(true);
        this.model = new LoadableModel<>(false) {
            @Override
            protected ReportFilterDto load() {
                return loadOrCreateFilter();
            }
        };
    }

    protected void initPanelLayout() {
        Form<?> form = new Form<>(ID_FORM, model);
        form.setOutputMarkupId(true);
        add(form);

        MonthNavigationPanel monthNavigation = new MonthNavigationPanel("monthNavigation", model) {

            @Override
            protected void handleCalendarNavigation(AjaxRequestTarget target, ReportFilterDto workFilter) {
                AbstractReportTab.this.handleCalendarNavigation(target, workFilter);
            }
        };
        monthNavigation.setOutputMarkupId(true);
        form.add(monthNavigation);

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
        realizators.add(new EmptyOnChangeAjaxBehavior());
        form.add(realizators);

        CustomerProjectPartSearchPanel customerProjectSearchPanel = new CustomerProjectPartSearchPanel(ID_CUSTOMER, new PropertyModel<>(getFilterModel(), ReportFilterDto.F_PROJECT_SEARCH_SETTINGS));
        customerProjectSearchPanel.add(new VisibleEnableBehaviour() {
            @Override public boolean isVisible() { return includeCustomerSearch(); }
        });
        customerProjectSearchPanel.setOutputMarkupId(true);
        form.add(customerProjectSearchPanel);

        IChoiceRenderer<LabelPart> labelRenderer = new IChoiceRenderer<>() {
            @Override public Object getDisplayValue(LabelPart l) { return l.getCode() + " — " + l.getName(); }
            @Override public String getIdValue(LabelPart l, int index) { return String.valueOf(l.getId()); }
            @Override public LabelPart getObject(String id, IModel<? extends List<? extends LabelPart>> choices) {
                Long lid = Long.valueOf(id);
                for (LabelPart lp : choices.getObject()) if (lp != null && lid.equals(lp.getId())) return lp;
                return null;
            }
        };

        LoadableModel<List<LabelPart>> labelsChoices = new LoadableModel<>(false) {
            @Override protected List<LabelPart> load() { return labelService.findAllOrdered(); }
        };

        ListMultipleChoice<LabelPart> labelsField = new MultiselectDropDownInput<>(
                ID_LABELS,
                new PropertyModel<>(model, ReportFilterDto.F_LABELS),
                labelsChoices,
                labelRenderer
        );
        labelsField.add(new EmptyOnChangeAjaxBehavior());
        labelsField.setOutputMarkupId(true);
        form.add(labelsField);

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

        MainPopupDialog confirmDownload = new MainPopupDialog(ID_DOWNLOAD_MODAL);
        confirmDownload.setOutputMarkupId(true);
        add(confirmDownload);

        addDownloadButton(form);
    }

    @Override
    protected void initLayout() {
        initPanelLayout();
    }

    protected boolean includeCustomerSearch() {
        return true;
    }

    protected abstract void buildResultsUI();

    protected abstract void onPreview(AjaxRequestTarget target);

    protected abstract Component createDownloadContent(String contentId);

    protected void beforeOpenDownload(AjaxRequestTarget target) {
    }

    protected void afterCalendarNavigation(AjaxRequestTarget target) {}


    protected IModel<ReportFilterDto> getFilterModel() { return model; }

    protected void setDateTo(AjaxRequestTarget target) {
        ReportFilterDto f = getFilterModel().getObject();
        f.setDateTo(f.getDateFrom().with(TemporalAdjusters.lastDayOfMonth()));
        LocalDateTextField dateTo = (LocalDateTextField) get(ID_FORM + ":" + ID_TO);
        target.add(dateTo);
    }

    private void handleCalendarNavigation(AjaxRequestTarget target, ReportFilterDto f) {
        setFilterToSession(f);
        target.add(get(ID_FORM));
        afterCalendarNavigation(target);
    }

    protected void addDownloadButton(Form<?> form) {
        form.add(new AjaxSubmitButton(ID_EXPORT, createStringResource("PageReports.download")) {
            @Override protected void onSubmit(AjaxRequestTarget target) {
                beforeOpenDownload(target);
                showDownloadPanel(target);
            }
            @Override protected void onError(AjaxRequestTarget target) {
                targetAddFeedback(target);
                super.onError(target);
            }
        });
    }

    private void showDownloadPanel(AjaxRequestTarget target) {

        Component content = createDownloadContent(ModalDialog.CONTENT_ID);
        content.add(AttributeModifier.append("class", "modal-content"));


        MainPopupDialog partModal = (MainPopupDialog) get(ID_DOWNLOAD_MODAL);
        partModal.setContent(content);
        partModal.open(target);

    }

    protected void targetAddFeedback(AjaxRequestTarget target) {
        WebPage page = (WebPage) getPage();
        if (page instanceof PageAppTemplate template) {
            target.add(template.getFeedbackPanel());
        }
    }

    @Override
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
        ReportFilterDto filter = getFilterFromSession();
        if (filter == null) {
            filter = new ReportFilterDto();
            filter.setDateFrom(GizmoUtils.createWorkDefaultFrom());
            filter.setDateTo(GizmoUtils.createWorkDefaultTo());
            filter.setRealizators(getDefaultRealizators());
        }
        return filter;
    }

    protected abstract ReportFilterDto getFilterFromSession();
    protected abstract void setFilterToSession(ReportFilterDto filter);
    protected List<User> getDefaultRealizators() {
        GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
        User defaultRealizator = principal.getUser();
        List<User> realizators = new ArrayList<>();
        realizators.add(defaultRealizator);
        return realizators;
    }
}
