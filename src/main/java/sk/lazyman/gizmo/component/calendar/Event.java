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

import java.time.LocalDate;
import java.util.Date;

public class Event {

    private String id;
    private String title;
    private Date start;
    private Date end;
    private boolean allDay = true;
    private String display = "background";
    private String backgroundColor;

    public Event(String id, String title, Date start, String backgroundColor) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.backgroundColor = backgroundColor;
    }

    public Event display(String display) {
        this.display = display;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public String getDisplay() {
        return display;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getId() {
        return id;
    }
}
