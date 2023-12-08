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

package com.evolveum.gizmo.component.calendar;

//  headerToolbar: {
//            left: 'prev,next today',
//            center: 'title',
//            right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek'
//        },
public class HeaderToolbar {

    private String left = "";
    private String center = "";
    private String right = "";

    public HeaderToolbar(){

    }

    public HeaderToolbar(String left, String center, String right) {
        this.left = left;
        this.center = center;
        this.right = right;
    }

    public String getLeft() {
        return left;
    }

    public String getCenter() {
        return center;
    }

    public String getRight() {
        return right;
    }
}
