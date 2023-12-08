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

import com.evolveum.gizmo.data.Customer;
import com.evolveum.gizmo.data.EmailLog;
import com.evolveum.gizmo.data.Project;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.data.provider.BasicDataProvider;
import com.evolveum.gizmo.data.provider.EmailDataProvider;
import com.evolveum.gizmo.dto.EmailFilterDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.springframework.data.domain.Sort;
import org.wicketstuff.annotation.mount.MountPath;
import com.evolveum.gizmo.component.AjaxSubmitButton;
import com.evolveum.gizmo.component.data.DateColumn;
import com.evolveum.gizmo.component.data.IconColumn;
import com.evolveum.gizmo.component.data.TablePanel;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;

import java.util.*;

/**
 * @author lazyman
 */
@MountPath("/app/emails")
public class PageEmails extends PageAppTemplate {

    private static final String ID_TABLE = "table";
    private static final String ID_FORM = "form";
    private static final String ID_FROM = "from";
    private static final String ID_TO = "to";
    private static final String ID_SENDER = "sender";
    private static final String ID_FILTER = "filter";

    private IModel<EmailFilterDto> filter;

    public PageEmails() {
        filter = new LoadableModel<EmailFilterDto>(false) {

            @Override
            protected EmailFilterDto load() {
                EmailFilterDto dto = new EmailFilterDto();
                dto.setFrom(GizmoUtils.createWorkDefaultFrom());
                dto.setTo(GizmoUtils.createWorkDefaultTo());

                return dto;
            }
        };

        initLayout();
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        form.add(new DateTextField(ID_FROM, new PropertyModel<Date>(filter, EmailFilterDto.F_FROM),
                GizmoUtils.DATE_FIELD_FORMAT));
        form.add(new DateTextField(ID_TO, new PropertyModel<Date>(filter, EmailFilterDto.F_TO),
                GizmoUtils.DATE_FIELD_FORMAT));

        form.add(new DropDownChoice<User>(ID_SENDER, new PropertyModel<User>(filter, EmailFilterDto.F_SENDER),
                GizmoUtils.createUsersModel(this), new ChoiceRenderer<>() {

            @Override
            public Object getDisplayValue(User object) {
                return object.getFullName();
            }

            @Override
            public String getIdValue(User object, int index) {
                return Integer.toString(index);
            }
        }) {

            @Override
            protected String getNullValidKey() {
                return "PageEmails.sender";
            }
        });

        form.add(new AjaxSubmitButton(ID_FILTER, createStringResource("PageEmails.filter")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                filterLogs(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(getFeedbackPanel());
            }
        });

        BasicDataProvider provider = new EmailDataProvider(getEmailLogRepository(), 15);
        provider.setSort(Sort.by(Sort.Direction.DESC, EmailLog.F_SENT_DATE));

        List<IColumn> columns = new ArrayList<>();
        columns.add(new DateColumn(createStringResource("EmailLog.sentDate"),
                EmailLog.F_SENT_DATE, "dd. MMM, yyyy HH:mm:ss"));
        columns.add(new AbstractColumn<EmailLog, String>(createStringResource("EmailLog.sender")) {

            @Override
            public void populateItem(Item<ICellPopulator<EmailLog>> item, String componentId,
                                     final IModel<EmailLog> rowModel) {
                item.add(new Label(componentId, new IModel<Object>() {

                    @Override
                    public String getObject() {
                        EmailLog log = rowModel.getObject();
                        return log.getSender().getFullName();
                    }
                }));
            }
        });
        columns.add(new IconColumn<EmailLog>(createStringResource("EmailLog.successful")) {

            @Override
            protected IModel<String> createTitleModel(IModel<EmailLog> rowModel) {
                EmailLog log = rowModel.getObject();
                String key = log.isSuccessful() ? "PageEmails.success" : "PageEmails.failure";
                return createStringResource(key);
            }

            @Override
            protected IModel<String> createIconModel(final IModel<EmailLog> rowModel) {
                return new IModel<String>() {

                    @Override
                    public String getObject() {
                        EmailLog log = rowModel.getObject();
                        return "fa fa-fw fa-lg " +
                                (log.isSuccessful() ? "fa-check-circle text-success" : "fa-times-circle text-danger");
                    }
                };
            }
        });
        columns.add(new PropertyColumn(createStringResource("EmailLog.mailTo"), EmailLog.F_MAIL_TO));
        columns.add(new DateColumn(createStringResource("EmailLog.fromDate"), EmailLog.F_FROM_DATE, "dd. MMM, yyyy"));
        columns.add(new DateColumn(createStringResource("EmailLog.toDate"), EmailLog.F_TO_DATE, "dd. MMM, yyyy"));
        columns.add(new PropertyColumn(createStringResource("EmailLog.summaryWork"), EmailLog.F_SUMMARY_WORK));
        columns.add(new PropertyColumn(createStringResource("EmailLog.summaryInvoice"), EmailLog.F_SUMMARY_INVOICE));
        columns.add(new AbstractColumn<EmailLog, String>(createStringResource("EmailLog.realizators")) {

            @Override
            public void populateItem(Item<ICellPopulator<EmailLog>> cellItem, String componentId,
                                     IModel<EmailLog> rowModel) {

                MultiLineLabel label = new MultiLineLabel(componentId, createRealizators(rowModel));
                cellItem.add(label);
            }
        });
        columns.add(new AbstractColumn<EmailLog, String>(createStringResource("EmailLog.projects")) {

            @Override
            public void populateItem(Item<ICellPopulator<EmailLog>> cellItem, String componentId,
                                     final IModel<EmailLog> rowModel) {
                MultiLineLabel label = new MultiLineLabel(componentId, createProjects(rowModel));
                cellItem.add(label);
            }
        });
        columns.add(new AbstractColumn<EmailLog, String>(createStringResource("EmailLog.customers")) {

            @Override
            public void populateItem(Item<ICellPopulator<EmailLog>> cellItem, String componentId,
                                     final IModel<EmailLog> rowModel) {
                MultiLineLabel label = new MultiLineLabel(componentId, createCustomers(rowModel));
                cellItem.add(label);
            }
        });

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 15);
        table.setOutputMarkupId(true);
        add(table);
    }

    private IModel<String> createRealizators(final IModel<EmailLog> rowModel) {
        return new IModel<String>() {

            @Override
            public String getObject() {
                EmailLog log = rowModel.getObject();
                Set<User> set = log.getRealizatorList();
                if (set == null) {
                    return null;
                }

                List<String> names = new ArrayList<>();
                for (User user : set) {
                    names.add(user.getFullName());
                }
                Collections.sort(names);

                return StringUtils.join(names, '\n');
            }
        };
    }

    private IModel<String> createCustomers(final IModel<EmailLog> rowModel) {
        return new IModel<String>() {

            @Override
            public String getObject() {
                EmailLog log = rowModel.getObject();
                Set<Customer> set = log.getCustomerList();
                if (set == null) {
                    return null;
                }

                List<String> names = new ArrayList<>();
                for (Customer customer : set) {
                    names.add(customer.getName());
                }
                Collections.sort(names);

                return StringUtils.join(names, '\n');
            }
        };
    }

    private IModel<String> createProjects(final IModel<EmailLog> rowModel) {
        return new IModel<String>() {

            @Override
            public String getObject() {
                EmailLog log = rowModel.getObject();
                Set<Project> set = log.getProjectList();
                if (set == null) {
                    return null;
                }

                List<String> names = new ArrayList<>();
                for (Project project : set) {
                    names.add(GizmoUtils.describeProject(project));
                }
                Collections.sort(names);

                return StringUtils.join(names, '\n');
            }
        };
    }

    private void filterLogs(AjaxRequestTarget target) {
        TablePanel table = (TablePanel) get(ID_TABLE);
        EmailDataProvider provider = (EmailDataProvider) table.getDataTable().getDataProvider();
        provider.setFilter(filter.getObject());
        table.setCurrentPage(0L);

        target.add(table);
    }
}
