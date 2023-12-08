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

package com.evolveum.gizmo.component.calendar;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Plugins {

//    interactionPlugin,
//    dayGridPlugin,
//    timeGridPlugin,
//    listPlugin;

    INTERACTION("interactionPlugin"),
    DAY_GRID("dayGridPlugin"),
    TIME_GRID("timeGridPlugin"),
    LIST("listPlugin");

    private String name;

    Plugins(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

}
