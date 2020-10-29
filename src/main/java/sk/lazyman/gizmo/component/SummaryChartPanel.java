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

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import sk.lazyman.gizmo.data.provider.SummaryPartsDataProvider;
import sk.lazyman.gizmo.dto.PartSummary;
import sk.lazyman.gizmo.dto.ReportFilterDto;
import sk.lazyman.gizmo.dto.WorkFilterDto;
import sk.lazyman.gizmo.util.LoadableModel;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lazyman
 */
public class SummaryChartPanel extends SimplePanel<List<PartSummary>> {

    public SummaryChartPanel(String id, final SummaryPartsDataProvider provider, final IModel<ReportFilterDto> model) {
        super(id);

        setModel(new LoadableModel<List<PartSummary>>() {

            @Override
            protected List<PartSummary> load() {
                return provider.createSummary(model.getObject());
            }
        });

        setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        List<PartSummary> partSummaries = getModelObject();

        JSONArray names = new JSONArray(partSummaries.stream().map(s -> s.getName()).collect(Collectors.toList()));
        JSONArray length = new JSONArray(partSummaries.stream().map(s -> s.getLength() / 8).collect(Collectors.toList()));


        JSONArray colors = new JSONArray(partSummaries.stream().map(s -> {
            Color color = new Color((int)(Math.random() * 0x1000000));
            String rgba = "rgba( " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ", 0.2)";
            return rgba;
        }).collect(Collectors.toList()));


        JSONObject object = new JSONObject();
        object.put("names", names);
        object.put("length", length);


        String script = "var ctx = document.getElementById('" + getMarkupId() + "');";
        script += "var myDoughnutChart = new Chart(ctx, { "
                + "type: 'doughnut', "
                + " data : {"
                    + "datasets: [{"
                    +      "data: " + length + ","

                    +      "backgroundColor: "//[ "
                    +                 colors
                    //+              "'rgba(255, 99, 132, 0.2)','rgba(54, 162, 235, 0.2)', 'rgba(255, 206, 86, 0.2)', 'rgba(75, 192, 192, 0.2)', 'rgba(153, 102, 255, 0.2)', 'rgba(255, 159, 64, 0.2)'],"
                    + "}],"
                    + "labels: " + names
                    + "},"
                    + "options: {"
                    +   "legend: {"
                    +       "display: false"
                    +       "}"
                    +   "},"
                    + "});";


        response.render(OnDomReadyHeaderItem.forScript(script));
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

    @Override
    protected void onDetach() {
        super.onDetach();
        ((LoadableModel) getModel()).reset();
    }
}
