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

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.model.*;
import org.apache.wicket.model.util.ListModel;
import sk.lazyman.gizmo.data.QAbstractTask;
import sk.lazyman.gizmo.data.QWork;
import sk.lazyman.gizmo.data.Work;
import sk.lazyman.gizmo.data.provider.AbstractTaskDataProvider;
import sk.lazyman.gizmo.data.provider.SummaryDataProvider;
import sk.lazyman.gizmo.dto.SummaryPanelDto;
import sk.lazyman.gizmo.dto.TaskLength;
import sk.lazyman.gizmo.dto.WorkFilterDto;
import sk.lazyman.gizmo.security.SecurityUtils;
import sk.lazyman.gizmo.util.GizmoUtils;
import sk.lazyman.gizmo.web.app.PageWork;

import javax.swing.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

                        dayItem.add(AttributeAppender.append("class", new AbstractReadOnlyModel<String>() {

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
        return new AbstractReadOnlyModel<String>() {

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
