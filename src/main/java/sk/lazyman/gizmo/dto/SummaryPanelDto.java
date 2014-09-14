package sk.lazyman.gizmo.dto;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lazyman
 */
public class SummaryPanelDto implements Serializable {

    private TaskFilterDto filter;
    private Map<Date, TaskLength> dates = new HashMap<>();

    public SummaryPanelDto(TaskFilterDto filter) {
        this.filter = filter;
    }

    public TaskFilterDto getFilter() {
        return filter;
    }

    public Map<Date, TaskLength> getDates() {
        return dates;
    }

    //todo not working correctly
    public Date getFromRoundedToMonday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(filter.getFrom());

        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    //todo not working correctly
    private Date getToRoundedToSunday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(filter.getTo());

        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return cal.getTime();
    }

    public int getWeekCount() {
        Date from = getFromRoundedToMonday();
        Date to = getToRoundedToSunday();

        Calendar cal = Calendar.getInstance();
        cal.setTime(from);

        int c = 0;
        do {
            c++;
            cal.add(Calendar.WEEK_OF_YEAR, 1);
        } while (cal.getTime().before(to));
        return c;
    }

    public boolean isWithinFilter(int dayIndex) {
        Date date = getDayForIndex(dayIndex);

        if (filter.getFrom().equals(date) || filter.getTo().equals(date)) {
            return true;
        }

        return filter.getFrom().before(date) && filter.getTo().after(date);
    }

    public boolean isWeekend(int dayIndex) {
        Date date = getDayForIndex(dayIndex);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int day = cal.get(Calendar.DAY_OF_WEEK);
        return Calendar.SATURDAY == day || Calendar.SUNDAY == day;
    }

    private Date getDayForIndex(int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(filter.getFrom());

        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.add(Calendar.DAY_OF_YEAR, i);
        return cal.getTime();
    }
}
