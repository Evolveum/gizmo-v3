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

package sk.lazyman.gizmo.dto;

import sk.lazyman.gizmo.util.GizmoUtils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lazyman
 */
public class SummaryPanelDto implements Serializable {

    public static final String F_MONTH_COUNT = "monthCount";

    private ReportFilterDto filter;
    private Map<LocalDate, TaskLength> dates = new HashMap<>();

    public SummaryPanelDto(ReportFilterDto filter) {
        this.filter = filter;
    }

    private LocalDate getEffectiveStart() {
        LocalDate monthStart;
        if (filter.getDateFrom() == null) {
            monthStart = GizmoUtils.createWorkDefaultFrom();
        } else {
            monthStart = filter.getDateFrom().with(TemporalAdjusters.firstDayOfMonth());
        }
        DayOfWeek dayOfWeek = monthStart.getDayOfWeek();
        int daysToSubstract = dayOfWeek.getValue() - DayOfWeek.MONDAY.getValue();
        return monthStart.minusDays(daysToSubstract);
    }

    private LocalDate getEffectiveEnd() {
        LocalDate monthEnd;
        if (filter.getDateTo() == null) {
            monthEnd = GizmoUtils.createWorkDefaultTo();
        } else {
            monthEnd = filter.getDateTo().with(TemporalAdjusters.lastDayOfMonth());
        }
        int daysToAdd = DayOfWeek.SUNDAY.getValue() - monthEnd.getDayOfWeek().getValue();
        return monthEnd.plusDays(daysToAdd + 1);
    }


    /**
     *
     * @param week starts with 0
     * @param day starts with 0
     */
    public LocalDate getDayModel(int week, int day) {
        List<LocalDate> datesStream = getEffectiveDates();
        List<LocalDate> subList = datesStream.subList(week * 7, week * 7 + 7);
        return subList.get(day);
    }

    public int getNumberOfWeeks() {
        List<LocalDate> datesStream = getEffectiveDates();
        return datesStream.size() / 7;
    }

    private List<LocalDate> getEffectiveDates() {
        return getEffectiveStart().datesUntil(getEffectiveEnd()).collect(Collectors.toList());
    }

//    private void aaa() {
////        LocalDate monthStart  = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
////        DayOfWeek dayOfWeek = monthStart.getDayOfWeek();
////        int daysToSubstract = dayOfWeek.getValue() - DayOfWeek.MONDAY.getValue();
////        LocalDate start = monthStart.minusDays(daysToSubstract);
//
////        LocalDate monthEnd = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
////        int daysToAdd = DayOfWeek.SUNDAY.getValue() - monthEnd.getDayOfWeek().getValue();
////        LocalDate end = monthEnd.plusDays(daysToAdd + 1);
//
//        List<LocalDate> datesStrem = start.datesUntil(end).collect(Collectors.toList());
//        long weeks = datesStrem.size() / 7;
//
//        List<List<LocalDate>> list = new ArrayList<>();
//
//        for (long i=0; i < weeks; i++) {
//            List<LocalDate> weekDays = new ArrayList<>();
//            Long j = Long.valueOf(i * 7);
//            int days = j.intValue()+7;
//
//            for (int weekstart = j.intValue(); weekstart < days; weekstart++) {
//                weekDays.add(datesStrem.get(weekstart));
//            }
//
//            list.add(weekDays);
//        }
//
//    }

//    public WorkFilterDto getFilter() {
//        return filter;
//    }

    public Map<LocalDate, TaskLength> getDates() {
        return dates;
    }

//    public int getMonthCount() {
//        Calendar fromCal = getFromRoundedToFirstMonthDay();
//        Calendar toCal = getToRoundedToLastMonthDay();
//
//        int c = 0;
//        do {
//            c++;
//            fromCal.add(Calendar.MONTH, 1);
//        } while (fromCal.getTime().before(toCal.getTime()));
//        return c;
//    }

//    private Calendar getToRoundedToLastMonthDay() {
//        Date to = GizmoUtils.clearTime(filter.getTo());
//        Calendar toCal = Calendar.getInstance();
//        toCal.setTime(to);
//        toCal.set(Calendar.DAY_OF_MONTH, 1);
//        toCal.add(Calendar.MONTH, 1);
//        toCal.add(Calendar.DAY_OF_MONTH, -1);
//
//        return toCal;
//    }

//    private Calendar getFromRoundedToFirstMonthDay() {
//        Date from = GizmoUtils.clearTime(filter.getFrom());
//        Calendar fromCal = Calendar.getInstance();
//        fromCal.setTime(from);
//        fromCal.set(Calendar.DAY_OF_MONTH, 1);
//
//        return fromCal;
//    }

//    public Date getDayForIndex(int monthIndex, int dayIndex) {
//        Calendar cal = getFromRoundedToFirstMonthDay();
//        cal.add(Calendar.MONTH, monthIndex);
//
//        int month = cal.get(Calendar.MONTH);
//        cal.add(Calendar.DAY_OF_YEAR, dayIndex);
//
//        if (month != cal.get(Calendar.MONTH)) {
//            //in case of looking for 31. day of month which only have 30 days (or 28/29 days)
//            return null;
//        }
//        return cal.getTime();
//    }

    public boolean isWithinFilter(int monthIndex, int dayIndex) {
        LocalDate date = getDayModel(monthIndex, dayIndex);
        if (date == null) {
            return false;
        }
        return date.getMonth() == filter.getDateFrom().getMonth();
    }

    public boolean isWeekend(int monthIndex, int dayIndex) {
        LocalDate date = getDayModel(monthIndex, dayIndex);
        if (date == null) {
            return false;
        }

       return DayOfWeek.SATURDAY == date.getDayOfWeek() || DayOfWeek.SUNDAY == date.getDayOfWeek();
    }

    public TaskLength getTaskLength(int week, int dayIndex) {
        LocalDate date = getDayModel(week, dayIndex);
        if (date == null) {
            return null;
        }

        return dates.get(date);
    }

    public boolean isFuture(int monthIndex, int dayIndex) {
        LocalDate date = getDayModel(monthIndex, dayIndex);
        if (date == null) {
            return false;
        }

        LocalDate now = LocalDate.now();

        return date.isAfter(now);
    }

    public boolean isFullDayDone(int week, int dayIndex) {
        if (isWeekend(week, dayIndex) || isFuture(week, dayIndex)) {
            return true;
        }

        TaskLength task = getTaskLength(week, dayIndex);
        if (task == null) {
            return false;
        }

        return task.getLength() >= 8.0;
    }

    public boolean isToday(int week, int dayIndex) {
        LocalDate date = getDayModel(week, dayIndex);
        if (date == null) {
            return false;
        }

        return date.isEqual(LocalDate.now());
    }

//    public String getMonthYear(int monthIndex) {
//        DateFormat df = new SimpleDateFormat("MMMM yyyy");
//
//        Calendar from = getFromRoundedToFirstMonthDay();
//        from.add(Calendar.MONTH, monthIndex);
//
//        return df.format(from.getTime());
//    }
}
