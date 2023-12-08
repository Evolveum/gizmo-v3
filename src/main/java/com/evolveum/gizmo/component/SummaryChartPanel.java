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

import com.evolveum.gizmo.data.provider.SummaryPartsDataProvider;
import com.evolveum.gizmo.dto.PartSummary;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.wicket.chartjs.*;
import org.apache.wicket.model.IModel;
import com.evolveum.gizmo.util.LoadableModel;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @author lazyman
 */
public class SummaryChartPanel extends SimplePanel<ReportFilterDto> {

    private SummaryPartsDataProvider provider;

    public SummaryChartPanel(String id, final SummaryPartsDataProvider provider, final IModel<ReportFilterDto> model) {
        super(id, model);
        this.provider = provider;
        setOutputMarkupId(true);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<DoughnutChartConfiguration> chartModel = () -> {
            DoughnutChartConfiguration config = new DoughnutChartConfiguration();

            List<PartSummary> partSummaries = provider.createSummary(getModelObject());
            ChartData chartData = new ChartData();
            chartData.addDataset(createDataset(partSummaries));

            partSummaries.stream().map(s -> s.getName()).forEach(s -> {
                chartData.addLabel(s);
            });

            config.setData(chartData);

            config.setOptions(createChartOptions());
            return config;
        };


        ChartJsPanel chart = new ChartJsPanel("chart", chartModel);
        add(chart);

    }

    private ChartDataset createDataset(List<PartSummary> partSummaries) {

        ChartDataset dataset = new ChartDataset();
        partSummaries.stream().map(s -> s.getLength() / 8).
        forEach(s -> {
                    dataset.addData(s);
                });


        partSummaries.stream().map(s -> {
                    Color color = new Color((int)(Math.random() * 0x1000000));
                    String rgba = "rgba( " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ", 0.2)";
                    return rgba;
                }).forEach(s -> {
                    dataset.addBackgroudColor(s);
                });
        return dataset;
    }

    private ChartOptions createChartOptions() {
        ChartOptions options = new ChartOptions();
        options.setAnimation(createAnimation());
        options.setLegend(createChartLegend());
        return options;
    }

    private ChartAnimationOption createAnimation() {
        ChartAnimationOption animationOption = new ChartAnimationOption();
        animationOption.setDuration(0);
        return animationOption;
    }

    private ChartLegendOption createChartLegend() {
        ChartLegendOption legend = new ChartLegendOption();
        legend.setPosition("right");
        ChartLegendLabel label = new ChartLegendLabel();
        label.setBoxWidth(15);
        legend.setLabels(label);
        return legend;
    }

//    @Override
//    public void renderHead(IHeaderResponse response) {
//        super.renderHead(response);
//        List<PartSummary> partSummaries = getModelObject();
//
//        JSONArray names = new JSONArray(partSummaries.stream().map(s -> s.getName()).collect(Collectors.toList()));
//        JSONArray length = new JSONArray(partSummaries.stream().map(s -> s.getLength() / 8).collect(Collectors.toList()));
//
//
//        JSONArray colors = new JSONArray(partSummaries.stream().map(s -> {
//            Color color = new Color((int)(Math.random() * 0x1000000));
//            String rgba = "rgba( " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ", 0.2)";
//            return rgba;
//        }).collect(Collectors.toList()));
//
//
//        JSONObject object = new JSONObject();
//        object.put("names", names);
//        object.put("length", length);
//
//
//        String script = "var ctx = document.getElementById('" + getMarkupId() + "');";
//        script += "var myDoughnutChart = new Chart(ctx, { "
//                + "type: 'doughnut', "
//                + " data : {"
//                    + "datasets: [{"
//                    +      "data: " + length + ","
//
//                    +      "backgroundColor: "//[ "
//                    +                 colors
//                    //+              "'rgba(255, 99, 132, 0.2)','rgba(54, 162, 235, 0.2)', 'rgba(255, 206, 86, 0.2)', 'rgba(75, 192, 192, 0.2)', 'rgba(153, 102, 255, 0.2)', 'rgba(255, 159, 64, 0.2)'],"
//                    + "}],"
//                    + "labels: " + names
//                    + "},"
//                    + "options: {"
//                    +   "legend: {"
//                    +       "display: false"
//                    +       "}"
//                    +   "},"
//                    + "});";
//
//
//        response.render(OnDomReadyHeaderItem.forScript(script));
//    }

//    private IModel<String> createSumWorkModel() {
//        return new IModel<String>() {
//
//            @Override
//            public String getObject() {
//                double sum = 0d;
//                List<PartSummary> list = getModelObject();
//                for (PartSummary part : list) {
//                    sum += part.getLength();
//                }
//
//                return createLength(sum);
//            }
//        };
//    }
//
//    private IModel<String> createSumInvoiceModel() {
//        return new IModel<String>() {
//
//            @Override
//            public String getObject() {
//                double sum = 0d;
//                List<PartSummary> list = getModelObject();
//                for (PartSummary part : list) {
//                    sum += part.getInvoice();
//                }
//
//                return createLength(sum);
//            }
//        };
//    }

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
