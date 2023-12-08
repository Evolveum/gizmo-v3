/*
 * Copyright (C) 2023 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */

package sk.lazyman.gizmo.component.calendar;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

public class CalendarPanel extends WebMarkupContainer {

    public CalendarPanel(String id, IModel<FullCalendar> model) {
        super(id, model);
    }

    private IModel<FullCalendar> getModel() {
        return (IModel<FullCalendar>) getDefaultModel();
    }

    private FullCalendar getModelObject() {
        return getModel() != null ? getModel().getObject() : null;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        FullCalendar config = getModelObject();
        if (config == null) {
            return;
        }

//        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(ChartJsPanel.class,
//                "../../../../webjars/chartjs/4.1.2/dist/chart.umd.js")));
////        response.render(CssReferenceHeaderItem.forReference(new PackageResourceReference(ChartJsPanel.class,
////                "../../../../webjars/chartjs/4.1.2/Chart.min.css")));


//        String script = "var ctx = document.getElementById('" + getMarkupId() + "');"
//                +       "var calendar = new Calendar(ctx, " + FullCalendarMapper.toJson(config) + ");"
//                + "calendar.render();";
        response.render(OnDomReadyHeaderItem.forScript(
                "window.MidPointFullCalendar.initCalendar('" + getMarkupId() + "', " + FullCalendarMapper.toJson(config) + ");"));
    }
}
