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
import com.evolveum.gizmo.util.LoadableModel;
import com.evolveum.wicket.chartjs.*;
import org.apache.wicket.model.IModel;

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
    protected void initLayout() {
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

        partSummaries.stream()
                .map(s -> s.getLength() / 8)
                .forEach(dataset::addData);

        partSummaries.stream()
                .map(s -> hexToRgba(s.getColor(), 1))
                .forEach(dataset::addBackgroudColor);

        return dataset;
    }

    private String hexToRgba(String hex, double alpha) {
        if (hex == null || !hex.matches("#?[0-9a-fA-F]{6}")) {
            return "rgba(200, 200, 200, " + alpha + ")";
        }

        hex = hex.replace("#", "");
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);

        return "rgba(" + r + ", " + g + ", " + b + ", " + alpha + ")";
    }


    private ChartOptions createChartOptions() {
        ChartOptions options = new ChartOptions();
        options.setAnimation(createAnimation());
        ChartPluginsOption plugins = new ChartPluginsOption();
        plugins.setLegend(createChartLegend());
        options.setPlugins(plugins);
        options.setResponsive(true);
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
        legend.setLabels(label);
        return legend;
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        ((LoadableModel) getModel()).reset();
    }
}
