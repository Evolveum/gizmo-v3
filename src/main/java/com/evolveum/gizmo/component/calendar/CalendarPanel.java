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

package com.evolveum.gizmo.component.calendar;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import java.util.List;

public class CalendarPanel extends WebMarkupContainer {

    private IModel<FullCalendar> model;

    public CalendarPanel(String id, CalendarEventsProvider provider) {
        super(id);
        this.model = createCalendarModel(provider);
    }

    private IModel<FullCalendar> createCalendarModel(CalendarEventsProvider provider) {
        return () -> {

            List<Plugins> calendarPlugins = List.of(Plugins.DAY_GRID);
            HeaderToolbar headerToolbar = new HeaderToolbar();


            FullCalendar configNew =
                    new FullCalendar(
                            calendarPlugins,
                            headerToolbar,
                            provider.getStartDateForEvents(),
                            provider.createEvents()
                    );

            return configNew;
        };
    }

    private IModel<FullCalendar> getModel() {
        return model;
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
        response.render(OnDomReadyHeaderItem.forScript(
                "window.MidPointFullCalendar.initCalendar('" + getMarkupId() + "', " + FullCalendarMapper.toJson(config) + ");"));
    }
}
