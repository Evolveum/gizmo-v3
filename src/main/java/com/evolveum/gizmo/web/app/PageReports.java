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
import com.evolveum.gizmo.component.VisibleEnableBehaviour;
import com.evolveum.gizmo.component.behavior.DateRangePickerBehavior;
import com.evolveum.gizmo.component.data.TablePanel;
import com.evolveum.gizmo.component.form.CustomerProjectPartSearchPanel;
import com.evolveum.gizmo.component.form.IconButton;
import com.evolveum.gizmo.component.form.MultiselectDropDownInput;
import com.evolveum.gizmo.component.modal.DownloadReportConfigPanel;
import com.evolveum.gizmo.component.modal.MainPopupDialog;
import com.evolveum.gizmo.data.*;
import com.evolveum.gizmo.data.provider.AbstractTaskDataProvider;
import com.evolveum.gizmo.data.provider.ReportDataProvider;
import com.evolveum.gizmo.data.provider.SummaryPartsDataProvider;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.PartSummary;
import com.evolveum.gizmo.dto.ProjectSearchSettings;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import com.evolveum.gizmo.security.GizmoPrincipal;
import com.evolveum.gizmo.security.SecurityUtils;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;
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
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalDialog;
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
    private static final String ID_CUSTOMER = "customer";

    private static final String ID_DETAILS = "details";

    private static final String ID_PREVIEW = "preview";
    private static final String ID_EXPORT = "export";

    private IModel<ReportFilterDto> model;

    private IModel<ProjectSearchSettings> projectSearchModel;

    private LoadableModel<List<CustomerProjectPartDto>> availabelProjects;

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

        projectSearchModel = new LoadableModel<>(false) {
            @Override
            protected ProjectSearchSettings load() {
                return new ProjectSearchSettings();
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
        MainPopupDialog confirmDownload = new MainPopupDialog("confirmDownload");
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

//            @Override
//            public boolean isVisible() {
//                return getFilterModel().getObject().isSummary();
//            }
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
