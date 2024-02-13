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

import java.time.LocalDate;

/**
 * @author lazyman
 */
public class UserSummary extends TaskLength implements Comparable<UserSummary> {

    public static final String F_REALIZTOR = "fullName";
    public static final String F_MAX_DATE = "maxDate";

    public static final String F_TIME_OFF = "timeOff";
    public static final String F_WORK = "work";

    private User realizator;
    private LocalDate maxDate;
    private Double timeOff;
    private Double work;

    public UserSummary(User realizator, LocalDate maxDate, Double timeOff, Double all, Double work) {
        super(all, all);
        this.timeOff = timeOff;
        this.work = work;
        this.maxDate = maxDate;
        this.realizator = realizator;
    }

    public String getFullName() {
        return realizator.getFullName();
    }

    public double getUserAllocation() {
        return realizator.getAllocation();
    }

    @Override
    public int compareTo(UserSummary o) {
        if (o == null) {
            return 0;
        }

        return String.CASE_INSENSITIVE_ORDER.compare(realizator.getName(), o.realizator.getName());
    }

    public Double getTimeOff() {
        return timeOff;
    }

    public Double getWork() {
        return work;
    }
}
