/*
 *  Copyright (C) 2023 Evolveum
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

package com.evolveum.gizmo.web.app;

import com.evolveum.gizmo.component.AjaxSubmitButton;
import com.evolveum.gizmo.component.GizmoTabbedPanel;
import com.evolveum.gizmo.component.SummaryPartsPanel;
import com.evolveum.gizmo.component.SummaryUsersPanel;
import com.evolveum.gizmo.component.behavior.DateRangePickerBehavior;
import com.evolveum.gizmo.component.data.TablePanel;
import com.evolveum.gizmo.component.form.CustomerProjectPartSearchPanel;
import com.evolveum.gizmo.component.form.EmptyOnChangeAjaxBehavior;
import com.evolveum.gizmo.component.form.MultiselectDropDownInput;
import com.evolveum.gizmo.component.modal.DownloadReportConfigPanel;
import com.evolveum.gizmo.component.modal.MainPopupDialog;
import com.evolveum.gizmo.data.AbstractTask;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.data.provider.ReportDataProvider;
import com.evolveum.gizmo.data.provider.SummaryPartsDataProvider;
import com.evolveum.gizmo.data.provider.SummaryUserDataProvider;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.WorkDto;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import com.evolveum.gizmo.security.GizmoPrincipal;
import com.evolveum.gizmo.security.SecurityUtils;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalDialog;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;

import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@MountPath("/app/reports")
public class PageReports extends PageAppTemplate {

    private static final String ID_FORM = "form";
    private static final String ID_FROM = "from";
    private static final String ID_TO = "to";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_CUSTOMER = "customer";

    private static final String ID_DETAILS = "details";

    private static final String ID_PREVIEW = "preview";
    private static final String ID_EXPORT = "export";

    public static final String ID_CONFIRM_DOWNLOAD = "confirmDownload";

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

                    GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
                    User defaultRealizator = principal.getUser();
                    List<User> realizators = new ArrayList<>();
                    realizators.add(defaultRealizator);
                    filter.setRealizators(realizators);
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

    private void setDateTo(AjaxRequestTarget target) {
        ReportFilterDto filterDto = getFilterModel().getObject();
        filterDto.setDateTo(filterDto.getDateFrom().with(TemporalAdjusters.lastDayOfMonth()));
        LocalDateTextField dateTo = (LocalDateTextField) get(ID_FORM + ":" + ID_TO);
        target.add(dateTo);
    }

    private void initLayout() {

        Form<ReportFilterDto> form = new Form<>(ID_FORM);
        form.setOutputMarkupId(true);
        add(form);

        LocalDateTextField from = new LocalDateTextField(ID_FROM, new PropertyModel<>(getFilterModel(), ReportFilterDto.F_DATE_FROM), "dd/MM/yyyy");
        from.setOutputMarkupId(true);
        from.add(new EmptyOnChangeAjaxBehavior());
        from.add(new DateRangePickerBehavior() {

            @Override
            protected void onEvent(AjaxRequestTarget target) {
                setDateTo(target);
            }
        });
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

        CustomerProjectPartSearchPanel customerProjectSearchPanel = new CustomerProjectPartSearchPanel(ID_CUSTOMER, new PropertyModel<>(getFilterModel(), ReportFilterDto.F_PROJECT_SEARCH_SETTINGS));
        customerProjectSearchPanel.setOutputMarkupId(true);
        form.add(customerProjectSearchPanel);

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

        showDownloadModal(form);

        List<ITab> tabs = createTabs();
        GizmoTabbedPanel<ITab> tabbedPanel = new GizmoTabbedPanel<>(ID_DETAILS, tabs);
        tabbedPanel.setOutputMarkupId(true);
        add(tabbedPanel);
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
                target.add(getFeedbackPanel());
                super.onError(target);
            }
        };
        form.add(download);

    }

    private List<ITab> createTabs() {
        List<ITab> tabs = new ArrayList<>();
        tabs.add(new AbstractTab(createStringResource("PageReports.table")) {
            @Override
            public WebMarkupContainer getPanel(String id) {
                ReportDataProvider provider = new ReportDataProvider(PageReports.this);
                provider.setFilter(model.getObject());

                List<IColumn<WorkDto, String>> columns = createColumns();
                TablePanel<WorkDto> table = new TablePanel<>(id, provider, columns, 50);
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
        });
        tabs.add(new AbstractTab(createStringResource("PageReports.userSummary")) {
            @Override
            public WebMarkupContainer getPanel(String id) {
                SummaryUserDataProvider partsProvider = new SummaryUserDataProvider(PageReports.this);
                return new SummaryUsersPanel(id, partsProvider, getFilterModel());
            }
        });
        return tabs;
    }

    private void refreshTable(AjaxRequestTarget target) {
        target.add(getFeedbackPanel());
        ReportFilterDto reportFilter = model.getObject();

        GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
        session.setReportFilterDto(reportFilter);


        target.add(get(ID_DETAILS));
    }

    private List<IColumn<WorkDto, String>> createColumns() {
        List<IColumn<WorkDto, String>> columns = new ArrayList<>();

        columns.add(new PropertyColumn<>(createStringResource("AbstractTask.date"), AbstractTask.F_DATE));
        columns.add(GizmoUtils.createWorkInvoiceColumn(this));
        columns.add(GizmoUtils.createAbstractTaskRealizatorColumn(this));
        columns.add(GizmoUtils.createWorkProjectColumn(this));
        columns.add(new PropertyColumn<>(createStringResource("AbstractTask.description"), AbstractTask.F_DESCRIPTION));
        columns.add(new PropertyColumn<>(createStringResource("PageReports.trackId"), AbstractTask.F_TRACK_ID));

        return columns;
    }


}
