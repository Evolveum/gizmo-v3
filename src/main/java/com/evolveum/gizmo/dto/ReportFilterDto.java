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

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportFilterDto implements Serializable {

    public static final String F_DATE_FROM = "dateFrom";
    public static final String F_DATE_TO = "dateTo";
    public static final String F_PROJECT_SEARCH_SETTINGS = "projectSearchSettings";
    public static final String F_CUSTOMER = "customer";
    public static final String F_REALIZATORS = "realizators";
    public static final String F_MONTH_YEAR = "monthYear";

    private LocalDate dateFrom;
    private LocalDate dateTo;

    private ProjectSearchSettings projectSearchSettings = new ProjectSearchSettings();
    private List<User> realizators;
    private WorkType workType = WorkType.ALL;

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public List<CustomerProjectPartDto> getCustomerProjectPartDtos() {
       return projectSearchSettings.getCustomerProjectPartDtoList();
    }

    public void setCustomerProjectPartDtos(List<CustomerProjectPartDto> customerProjectPartDtos) {
        if (customerProjectPartDtos == null) {
            projectSearchSettings.setCustomerProjectPartDtoList(new ArrayList<>());
        } else {
            projectSearchSettings.setCustomerProjectPartDtoList(new ArrayList<>(customerProjectPartDtos));
        }
    }

    public List<User> getRealizators() {
        if (realizators == null) {
            realizators = new ArrayList<>();
        }
        return realizators;
    }

    public void setRealizators(List<User> realizators) {
        this.realizators = realizators;
    }

    public WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }

    public String getMonthYear() {
        return dateFrom.getMonth().getDisplayName(TextStyle.FULL, Locale.US) + " " + dateFrom.getYear();
    }
}
