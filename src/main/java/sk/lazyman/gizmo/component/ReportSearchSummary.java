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

package sk.lazyman.gizmo.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.lazyman.gizmo.data.AbstractTask;
import sk.lazyman.gizmo.data.User;
import sk.lazyman.gizmo.dto.CustomerProjectPartDto;
import sk.lazyman.gizmo.dto.ReportFilterDto;
import sk.lazyman.gizmo.dto.ReportSearchSummaryDto;
import sk.lazyman.gizmo.util.GizmoUtils;
import sk.lazyman.gizmo.util.LoadableModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author lazyman
 */
public class ReportSearchSummary extends SimplePanel<ReportSearchSummaryDto> {

    private static final Logger LOG = LoggerFactory.getLogger(ReportSearchSummary.class);

    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_PROJECT = "project";
    private static final String ID_FROM = "from";
    private static final String ID_TO = "to";
    private static final String ID_INVOICE = "invoice";
    private static final String ID_WORK = "work";

    public ReportSearchSummary(String id, IModel<ReportSearchSummaryDto> model) {
        super(id, model);
        setRenderBodyOnly(true);

        initPanelLayout();
    }

    public ReportSearchSummary(String id, final IModel<ReportFilterDto> filterModel,
                               final IModel<List<AbstractTask>> dataModel) {
        this(id, new LoadableModel<ReportSearchSummaryDto>(false) {

            @Override
            protected ReportSearchSummaryDto load() {
                ReportSearchSummaryDto dto = new ReportSearchSummaryDto();

                ReportFilterDto filter = filterModel.getObject();
//                dto.setRealizators(filter.getRealizators());
//                dto.setProjects(filter.getProjects());
//                dto.setFrom(filter.getFrom());
//                dto.setTo(filter.getTo());

                dto.setInvoice(GizmoUtils.sumInvoiceLength(dataModel));
                dto.setWork(GizmoUtils.sumWorkLength(dataModel));

                return dto;
            }
        });
    }

    private void initPanelLayout() {
        MultiLineLabel realizator = new MultiLineLabel(ID_REALIZATOR, createRealizatorModel());
        add(realizator);

        MultiLineLabel project = new MultiLineLabel(ID_PROJECT, createProjectModel());
        add(project);

        Label from = new Label(ID_FROM,
                createStringDateModel(new PropertyModel<Date>(getModel(), ReportSearchSummaryDto.F_FROM)));
        add(from);

        Label to = new Label(ID_TO,
                createStringDateModel(new PropertyModel<Date>(getModel(), ReportSearchSummaryDto.F_TO)));
        add(to);

        Label invoice = new Label(ID_INVOICE,
                createHourMDModel(new PropertyModel<Double>(getModelObject(), ReportSearchSummaryDto.F_INVOICE)));
        invoice.setRenderBodyOnly(true);
        add(invoice);

        Label work = new Label(ID_WORK,
                createHourMDModel(new PropertyModel<Double>(getModelObject(), ReportSearchSummaryDto.F_WORK)));
        work.setRenderBodyOnly(true);
        add(work);
    }

    private IModel<String> createStringDateModel(final IModel<Date> dateModel) {
        return new IModel<String>() {

            @Override
            public String getObject() {
                Date date = dateModel.getObject();
                if (date == null) {
                    return null;
                }

                DateFormat df = new SimpleDateFormat("EEE dd. MMM. yyyy");
                return df.format(date);
            }
        };
    }

    private IModel<String> createRealizatorModel() {
        return new IModel<String>() {

            @Override
            public String getObject() {
                ReportSearchSummaryDto dto = getModelObject();
                StringBuilder sb = new StringBuilder();

                List<User> list = dto.getRealizators();
                for (User user : list) {
                    sb.append(user.getFullName());
                    sb.append("\n");
                }

                if (sb.length() != 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }

                return sb.toString();
            }
        };
    }

    private IModel<String> createProjectModel() {
        return new IModel<String>() {

            @Override
            public String getObject() {
                ReportSearchSummaryDto dto = getModelObject();
                StringBuilder sb = new StringBuilder();

                List<CustomerProjectPartDto> list = dto.getProjects();
                for (CustomerProjectPartDto cpp : list) {
                    sb.append(PartAutoCompleteConverter.convertToString(cpp));
                    sb.append("\n");
                }

                if (sb.length() != 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }

                return sb.toString();
            }
        };
    }

    private IModel<String> createHourMDModel(final IModel<Double> model) {
        return new IModel<String>() {
            @Override
            public String getObject() {
                return createHourMd(model.getObject());
            }
        };
    }

    private String createHourMd(double hours) {
        return StringUtils.join(new Object[]{hours, "/", hours / 8});
    }
}
