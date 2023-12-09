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

package com.evolveum.gizmo.component;

import com.evolveum.gizmo.data.*;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;
import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.Date;
import java.util.List;

/**
 * @author lazyman
 */
public class DataPrintPanel extends SimplePanel<ReportFilterDto> {

    private static final String ID_REPORT_SUMMARY = "reportSummary";
    private static final String ID_DATA = "data";
    private static final String ID_DATE = "date";
    private static final String ID_LENGTH = "length";
    private static final String ID_PROJECT_PART = "projectPart";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_CUSTOMER = "customer";
    private static final String ID_CUSTOMER_HEADER = "customerHeader";
    private static final String ID_PROJECT_PART_HEADER = "projectPartHeader";

    private IModel<List<AbstractTask>> dataModel;

    public DataPrintPanel(String id, IModel<ReportFilterDto> filter, IModel<List<AbstractTask>> dataModel) {
        super(id, filter);
        setRenderBodyOnly(true);

        this.dataModel = dataModel;
        initPanelLayout();
    }

    public DataPrintPanel(String id, IModel<ReportFilterDto> filter, EntityManager entityManager) {
        super(id, filter != null ? filter : new Model<>(new ReportFilterDto()));
        setRenderBodyOnly(true);

        dataModel = createDataModel(entityManager);

        initPanelLayout();
    }

    private void initPanelLayout() {
        ReportSearchSummary reportSummary = new ReportSearchSummary(ID_REPORT_SUMMARY, getModel(), dataModel);
        add(reportSummary);

        ListView<AbstractTask> data = new ListView<AbstractTask>(ID_DATA, dataModel) {

            @Override
            protected void populateItem(ListItem<AbstractTask> item) {
                initItem(item);
            }
        };
        add(data);

        WebMarkupContainer customerHeader = new WebMarkupContainer(ID_CUSTOMER_HEADER);
        customerHeader.add(createCustomerColumnBehaviour(dataModel));
        add(customerHeader);

        WebMarkupContainer projectPartHeader = new WebMarkupContainer(ID_PROJECT_PART_HEADER);
        projectPartHeader.add(createProjectPartColumnBehaviour(dataModel));
        add(projectPartHeader);
    }

    private void initItem(ListItem<AbstractTask> item) {
        IModel<AbstractTask> model = item.getModel();

        Label date = new Label(ID_DATE, createStringDateModel(new PropertyModel<Date>(model, AbstractTask.F_DATE)));
        item.add(date);

        Label length = new Label(ID_LENGTH, createLengthModel(model));
        item.add(length);

        Label customer = new Label(ID_CUSTOMER, createCustomerModel(model));
        customer.add(createCustomerColumnBehaviour(dataModel));
        item.add(customer);

        Label projectPart = new Label(ID_PROJECT_PART, createProjectPartModel(model));
        projectPart.add(createProjectPartColumnBehaviour(dataModel));
        item.add(projectPart);

        Label realizator = new Label(ID_REALIZATOR, createRealizatorModel(model));
        item.add(realizator);

        Label description = new Label(ID_DESCRIPTION, new PropertyModel<>(model, AbstractTask.F_DESCRIPTION));
        item.add(description);
    }

    private VisibleEnableBehaviour createCustomerColumnBehaviour(final IModel<List<AbstractTask>> data) {
        return new VisibleEnableBehaviour() {

            @Override
            public boolean isVisible() {
                List<AbstractTask> tasks = data.getObject();
                for (AbstractTask task : tasks) {
                    if (task instanceof Log) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    private VisibleEnableBehaviour createProjectPartColumnBehaviour(final IModel<List<AbstractTask>> data) {
        return new VisibleEnableBehaviour() {

            @Override
            public boolean isVisible() {
                List<AbstractTask> tasks = data.getObject();
                for (AbstractTask task : tasks) {
                    if (task instanceof Work) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    private IModel<String> createCustomerModel(final IModel<AbstractTask> model) {
        return new IModel<String>() {

            @Override
            public String getObject() {
                AbstractTask task = model.getObject();
                if (!(task instanceof Log)) {
                    return null;
                }

                Log log = (Log) task;
                Customer customer = log.getCustomer();
                return customer != null ? customer.getName() : null;
            }
        };
    }

    private IModel<String> createProjectPartModel(final IModel<AbstractTask> model) {
        return new IModel<String>() {

            @Override
            public String getObject() {
                AbstractTask task = model.getObject();
                if (!(task instanceof Work)) {
                    return null;
                }

                Work work = (Work) task;
                Part part = work.getPart();

                return GizmoUtils.describeProjectPart(part, " - ");
            }
        };
    }

    private IModel<String> createLengthModel(final IModel<AbstractTask> model) {
        return new IModel<String>() {

            @Override
            public String getObject() {
                AbstractTask task = model.getObject();
                double work = task.getWorkLength();
                double invoice = 0;
                if (task instanceof Work) {
                    invoice = ((Work) task).getInvoiceLength();
                }

                return StringUtils.join(new Object[]{work, "/", invoice});
            }
        };
    }

    private IModel<String> createRealizatorModel(final IModel<AbstractTask> model) {
        return new IModel<String>() {

            @Override
            public String getObject() {
                User user = model.getObject().getRealizator();
                if (user == null) {
                    return null;
                }

                return user.getFullName();
            }
        };
    }

    private IModel<List<AbstractTask>> createDataModel(final EntityManager entityManager) {
        return new LoadableModel(false) {

            @Override
            protected List<AbstractTask> load() {
                return loadData(entityManager);
            }
        };
    }

    private IModel<String> createStringDateModel(final IModel<Date> dateModel) {
        return new IModel<String>() {

            @Override
            public String getObject() {
                Date date = dateModel.getObject();
                return GizmoUtils.formatDate(date);
            }
        };
    }

    private List<AbstractTask> loadData(EntityManager entityManager) {
        ReportFilterDto filter = getModel().getObject();
        return GizmoUtils.loadData(filter, entityManager);
    }
}
