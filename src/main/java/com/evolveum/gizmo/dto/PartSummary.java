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

package com.evolveum.gizmo.dto;

import com.evolveum.gizmo.data.User;

/**
 * @author lazyman
 */
public class PartSummary extends TaskLength implements Comparable<PartSummary> {

    public static final String F_NAME = "name";
    public static final String F_REALIZTOR = "fullName";
    private String name;
    private User realizator;
    private String color;

    public PartSummary(User realizator, String name, Double length, Double invoice) {
        super(length, invoice);
        this.name = name;
        this.realizator = realizator;
    }

    public String getFullName() {
        return realizator.getFullName();
    }

    public double getUserAllocation() {
        return realizator.getAllocation();
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    @Override
    public int compareTo(PartSummary o) {
        if (o == null) {
            return 0;
        }

        return String.CASE_INSENSITIVE_ORDER.compare(name, o.getName());
    }
}
