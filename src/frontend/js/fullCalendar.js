import { Calendar } from '@fullcalendar/core';

export default class MidPointFullCalendar {

    initCalendar(elementId, config) {
        var calendarEl = document.getElementById(elementId);
        var calendar = new Calendar(calendarEl, config);
        calendar.render();
    }
}