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

import com.evolveum.gizmo.data.provider.SummaryUserDataProvider;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.UserSummary;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author lazyman
 */
public class SummaryUsersPanel extends SimplePanel<List<UserSummary>> {

    private static final String ID_PART_REPEATER = "partRepeater";
    private static final String ID_MAX_DATE = "maxDate";
    private static final String ID_USER = "user";
    private static final String ID_WORK = "work";
    private static final String ID_TIME_OFF = "timeOff";

    public SummaryUsersPanel(String id, final SummaryUserDataProvider provider, final IModel<ReportFilterDto> model) {
        super(id);

        setModel(new LoadableDetachableModel<List<UserSummary>>() {
            @Override
            protected List<UserSummary> load() {
                try {
                    List<UserSummary> data = provider.createSummary(model.getObject());
                    return data != null ? data : java.util.Collections.emptyList();
                } catch (Exception e) {
                    return java.util.Collections.emptyList();
                }
            }
        });

        setOutputMarkupId(true);
        initPanelLayout();
    }

    private void initPanelLayout() {
        ListView<UserSummary> repeater = new ListView<UserSummary>(ID_PART_REPEATER, getModel()) {

            @Override
            protected void populateItem(final ListItem<UserSummary> item) {
                Label user = new Label(ID_USER, new PropertyModel<>(item.getModel(), UserSummary.F_REALIZTOR));
                user.setRenderBodyOnly(true);
                item.add(user);

                Label part = new Label(ID_MAX_DATE, new PropertyModel<>(item.getModel(), UserSummary.F_MAX_DATE));
                part.setRenderBodyOnly(true);
                item.add(part);

                Label work = new Label(ID_WORK, (IModel<String>) () -> {
                    UserSummary part1 = item.getModelObject();
                    Double hours = part1.getWork();
                    return createLength(hours, part1.getUserAllocation());
                });
                work.setRenderBodyOnly(true);
                item.add(work);

                Label timeOff = new Label(ID_TIME_OFF, (IModel<String>) () -> {
                    UserSummary part1 = item.getModelObject();
                    Double hours = part1.getTimeOff();
                    return createLength(hours, part1.getUserAllocation());
                });
                timeOff.setRenderBodyOnly(true);
                item.add(timeOff);

            }
        };
        add(repeater);
    }

    private String createLength(Double hours, double allocation) {
        if (hours == null) {
            hours = 0d;
        }
        Double days = hours / (8 * allocation);

        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return hours + " (" + twoDForm.format(days) + "d)";
    }
}
