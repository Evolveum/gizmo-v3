/*
 * Copyright (C) 2023 Evolveum
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
package sk.lazyman.gizmo.component.calendar;


//plugins: [ interactionPlugin, dayGridPlugin, timeGridPlugin, listPlugin ],
//        headerToolbar: {
//            left: 'prev,next today',
//            center: 'title',
//            right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek'
//        },
//        initialDate: '2018-01-12',
//        navLinks: true, // can click day/week names to navigate views
//        editable: true,
//        dayMaxEvents: true, // allow "more" link when too many events
//        events: [
//            {
//                title: 'All Day Event',
//                start: '2018-01-01',
//            },
//            {
//                title: 'Long Event',
//                start: '2018-01-07',
//                end: '2018-01-10'
//            },
//            {
//                groupId: 999,
//                title: 'Repeating Event',
//                start: '2018-01-09T16:00:00'
//            },
//            {
//                groupId: 999,
//                title: 'Repeating Event',
//                start: '2018-01-16T16:00:00'
//            },
//            {
//                title: 'Conference',
//                start: '2018-01-11',
//                end: '2018-01-13'
//            },
//            {
//                title: 'Meeting',
//                start: '2018-01-12T10:30:00',
//                end: '2018-01-12T12:30:00'
//            },
//            {
//                title: 'Lunch',
//                start: '2018-01-12T12:00:00'
//            },
//            {
//                title: 'Meeting',
//                start: '2018-01-12T14:30:00'
//            },
//            {
//                title: 'Happy Hour',
//                start: '2018-01-12T17:30:00'
//            },
//            {
//                title: 'Dinner',
//                start: '2018-01-12T20:00:00'
//            },
//            {
//                title: 'Birthday Party',
//                start: '2018-01-13T07:00:00'
//            },
//            {
//                title: 'Click for Google',
//                url: 'http://google.com/',
//                start: '2018-01-28'
//            }
//        ]

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class FullCalendar implements Serializable {

    private List<Plugins> plugins;
    private HeaderToolbar headerToolbar;
    private Date initialDate;
    private List<Event> events;
    private String initialView = "dayGridMonth";
    private Integer firstDay = 1;

    public FullCalendar(List<Plugins> plugins, HeaderToolbar headerToolbar, Date initialDate, List<Event> events) {
        this.plugins = plugins;
        this.headerToolbar = headerToolbar;
        this.initialDate = initialDate;
        this.events = events;
    }

    public List<Plugins> getPlugins() {
        return plugins;
    }

    public HeaderToolbar getHeaderToolbar() {
        return headerToolbar;
    }

    public Date getInitialDate() {
        return initialDate;
    }

    public List<Event> getEvents() {
        return events;
    }


    public String getInitialView() {
        return initialView;
    }

    public Integer getFirstDay() {
        return firstDay;
    }
}
