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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import sk.lazyman.gizmo.data.provider.SummaryPartsDataProvider;
import sk.lazyman.gizmo.dto.PartSummary;
import sk.lazyman.gizmo.dto.WorkFilterDto;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author lazyman
 */
public class SummaryPartsPanel extends SimplePanel<List<PartSummary>> {

    private static final String ID_PART_REPEATER = "partRepeater";
    private static final String ID_PART = "part";
    private static final String ID_WORK = "work";
    private static final String ID_INVOICE = "invoice";
    private static final String ID_SUM_WORK = "sumWork";
    private static final String ID_SUM_INVOICE = "sumInvoice";

    public SummaryPartsPanel(String id, final SummaryPartsDataProvider provider, final IModel<WorkFilterDto> model) {
        super(id);

        setModel(new LoadableDetachableModel<List<PartSummary>>() {

            @Override
            protected List<PartSummary> load() {
                return provider.createSummary(model.getObject());
            }
        });

        setOutputMarkupId(true);
        initPanelLayout();
    }

    private void initPanelLayout() {
        ListView repeater = new ListView<PartSummary>(ID_PART_REPEATER, getModel()) {

            @Override
            protected void populateItem(final ListItem<PartSummary> item) {
                Label part = new Label(ID_PART, new PropertyModel<>(item.getModel(), PartSummary.F_NAME));
                part.setRenderBodyOnly(true);
                item.add(part);

                Label work = new Label(ID_WORK, new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        PartSummary part = item.getModelObject();
                        Double hours = part.getLength();
                        return createLength(hours);
                    }
                });
                work.setRenderBodyOnly(true);
                item.add(work);

                Label invoice = new Label(ID_INVOICE, new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        PartSummary part = item.getModelObject();
                        Double hours = part.getInvoice();
                        return createLength(hours);
                    }
                });
                invoice.setRenderBodyOnly(true);
                item.add(invoice);
            }
        };
        add(repeater);

        Label sumWork = new Label(ID_SUM_WORK, createSumWorkModel());
        sumWork.setRenderBodyOnly(true);
        add(sumWork);

        Label sumInvoice = new Label(ID_SUM_INVOICE, createSumInvoiceModel());
        sumInvoice.setRenderBodyOnly(true);
        add(sumInvoice);
    }

    private IModel<String> createSumWorkModel() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                double sum = 0d;
                List<PartSummary> list = getModelObject();
                for (PartSummary part : list) {
                    sum += part.getLength();
                }

                return createLength(sum);
            }
        };
    }

    private IModel<String> createSumInvoiceModel() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                double sum = 0d;
                List<PartSummary> list = getModelObject();
                for (PartSummary part : list) {
                    sum += part.getInvoice();
                }

                return createLength(sum);
            }
        };
    }

    private String createLength(Double hours) {
        if (hours == null) {
            hours = 0d;
        }
        Double days = hours / 8;

        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return hours + " (" + twoDForm.format(days) + "d)";
    }
}
