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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import sk.lazyman.gizmo.data.Work;
import sk.lazyman.gizmo.dto.SummaryPanelDto;
import sk.lazyman.gizmo.dto.TaskLength;
import sk.lazyman.gizmo.security.SecurityUtils;
import sk.lazyman.gizmo.web.app.PageWork;

import java.time.LocalDate;

/**
 * @author lazyman
 */
public class SummaryPanel extends SimplePanel<SummaryPanelDto> {

    private static final String ID_MONTH_REPEATER = "monthRepeater";
    private static final String ID_WEEK_REPEATER = "weekRepeater";
    private static final String ID_DAY_REPEATER = "dayRepeater";
    private static final String ID_LABEL = "label";
    private static final String ID_DAY_NUMBER = "dayNumber";

    private static final int TABLE_DAY_SIZE = 32;

    private IModel<SummaryPanelDto> model;

    public SummaryPanel(String id, final IModel<SummaryPanelDto> model) {
        super(id, model);

        setOutputMarkupId(true);
        initPanelLayout();
    }

    private void initPanelLayout() {

        Loop monthRepeater = new Loop(ID_WEEK_REPEATER, 5) {

            @Override
            protected void populateItem(final LoopItem item) {

                Loop dayRepeater = new Loop(ID_DAY_REPEATER, 7) {

                    @Override
                    protected void populateItem(final LoopItem dayItem) {
                        final int dayIndex = dayItem.getIndex();
                        int weekIndex = item.getIndex();
//                        List<LocalDate> week = list.get(item.getIndex());



                        Label dayNumber = new Label(ID_DAY_NUMBER, new IModel<Integer>() {

                            @Override
                            public Integer getObject() {
                                LocalDate day = getModelObject().getDayModel(weekIndex, dayIndex);
                                return day.getDayOfMonth();
                            }
                        });
//                        dayNumber.setRenderBodyOnly(true);
                        dayItem.add(dayNumber);

                        AjaxLink<Void> link = new AjaxLink<>(ID_LABEL) {

                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                Work work = new Work();
                                work.setDate(SummaryPanel.this.getModelObject().getDayModel(weekIndex, dayIndex));
                                work.setRealizator(SecurityUtils.getPrincipalUser().getUser());
                                setResponsePage(new PageWork(new Model(work)));
                            }
                        };
                        link.setBody(createDayModel(weekIndex, dayIndex));
                        dayItem.add(link);

                        dayItem.add(AttributeAppender.append("class", new IModel<String>() {

                            @Override
                            public String getObject() {
                                SummaryPanelDto dto = getModelObject();
                                // month year column
                                if (dayIndex == -1) {
                                    return null;
                                }

                                if (dto.isToday(weekIndex, dayIndex)) {
                                    return "info";
                                }

                                if (!dto.isWithinFilter(weekIndex, dayIndex)) {
                                    return "table-active";
                                }

                                if (dto.isWeekend(weekIndex, dayIndex)) {
                                    return "table-success";
                                }

                                if (!dto.isFullDayDone(weekIndex, dayIndex)) {
                                    return "table-danger";
                                }

                                return null;
                            }
                        }));
                    }
                };
                item.add(dayRepeater);
            }
        };
        add(monthRepeater);
    }

    private IModel<String> createDayModel(final int monthIndex, final int dayIndex) {
        return new IModel<String>() {

            @Override
            public String getObject() {
                SummaryPanelDto dto = getModelObject();

//                if (dayIndex == -1) {
//                    return dto.getMonthYear(monthIndex);
//                }

                if (dto.getDayModel(monthIndex, dayIndex) == null) {
                    //not "existing" date like 31. september
                    return null;
                }

                if (!dto.isWithinFilter(monthIndex, dayIndex)) {
                    return null;
                }

                TaskLength length = dto.getTaskLength(monthIndex, dayIndex);
                if (length == null) {
                    length = new TaskLength(0.0, 0.0);
                }
                return StringUtils.join(new Object[]{length.getLength(), length.getInvoice()}, '/');
            }
        };
    }
}
