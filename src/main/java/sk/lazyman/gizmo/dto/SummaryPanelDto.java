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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lazyman
 */
public class SummaryPanelDto implements Serializable {

    public static final String F_MONTH_COUNT = "monthCount";

    private WorkFilterDto filter;
    private Map<Date, TaskLength> dates = new HashMap<>();

    public SummaryPanelDto(WorkFilterDto filter) {
        this.filter = filter;
    }

    public WorkFilterDto getFilter() {
        return filter;
    }

    public Map<Date, TaskLength> getDates() {
        return dates;
    }

    public int getMonthCount() {
        Calendar fromCal = getFromRoundedToFirstMonthDay();
        Calendar toCal = getToRoundedToLastMonthDay();

        int c = 0;
        do {
            c++;
            fromCal.add(Calendar.MONTH, 1);
        } while (fromCal.getTime().before(toCal.getTime()));
        return c;
    }

    private Calendar getToRoundedToLastMonthDay() {
        Date to = GizmoUtils.clearTime(filter.getTo());
        Calendar toCal = Calendar.getInstance();
        toCal.setTime(to);
        toCal.set(Calendar.DAY_OF_MONTH, 1);
        toCal.add(Calendar.MONTH, 1);
        toCal.add(Calendar.DAY_OF_MONTH, -1);

        return toCal;
    }

    private Calendar getFromRoundedToFirstMonthDay() {
        Date from = GizmoUtils.clearTime(filter.getFrom());
        Calendar fromCal = Calendar.getInstance();
        fromCal.setTime(from);
        fromCal.set(Calendar.DAY_OF_MONTH, 1);

        return fromCal;
    }

    public Date getDayForIndex(int monthIndex, int dayIndex) {
        Calendar cal = getFromRoundedToFirstMonthDay();
        cal.add(Calendar.MONTH, monthIndex);

        int month = cal.get(Calendar.MONTH);
        cal.add(Calendar.DAY_OF_YEAR, dayIndex);

        if (month != cal.get(Calendar.MONTH)) {
            //in case of looking for 31. day of month which only have 30 days (or 28/29 days)
            return null;
        }
        return cal.getTime();
    }

    public boolean isWithinFilter(int monthIndex, int dayIndex) {
        Date date = getDayForIndex(monthIndex, dayIndex);
        if (date == null) {
            return false;
        }

        if (filter.getFrom().equals(date) || filter.getTo().equals(date)) {
            return true;
        }

        return filter.getFrom().before(date) && filter.getTo().after(date);
    }

    public boolean isWeekend(int monthIndex, int dayIndex) {
        Date date = getDayForIndex(monthIndex, dayIndex);
        if (date == null) {
            return false;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int day = cal.get(Calendar.DAY_OF_WEEK);
        return Calendar.SATURDAY == day || Calendar.SUNDAY == day;
    }

    public TaskLength getTaskLength(int monthIndex, int dayIndex) {
        Date date = getDayForIndex(monthIndex, dayIndex);
        if (date == null) {
            return null;
        }

        return dates.get(date);
    }

    public boolean isFuture(int monthIndex, int dayIndex) {
        Date date = getDayForIndex(monthIndex, dayIndex);
        if (date == null) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Calendar now = Calendar.getInstance();
        now.setTime(GizmoUtils.clearTime(new Date()));

        return cal.getTime().after(now.getTime());
    }

    public boolean isFullDayDone(int monthIndex, int dayIndex) {
        if (isWeekend(monthIndex, dayIndex) || isFuture(monthIndex, dayIndex)) {
            return true;
        }

        TaskLength task = getTaskLength(monthIndex, dayIndex);
        if (task == null) {
            return false;
        }

        return task.getLength() >= 8.0;
    }

    public boolean isToday(int monthIndex, int dayIndex) {
        Date date = getDayForIndex(monthIndex, dayIndex);
        if (date == null) {
            return false;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        Calendar now = Calendar.getInstance();
        now.setTime(GizmoUtils.clearTime(new Date()));

        return cal.getTime().equals(now.getTime());
    }

    public String getMonthYear(int monthIndex) {
        DateFormat df = new SimpleDateFormat("MMMM yyyy");

        Calendar from = getFromRoundedToFirstMonthDay();
        from.add(Calendar.MONTH, monthIndex);

        return df.format(from.getTime());
    }
}
