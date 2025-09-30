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

import com.evolveum.gizmo.data.LabelPart;
import com.evolveum.gizmo.data.User;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class ReportFilterDto implements Serializable {

    public static final String F_DATE_FROM = "dateFrom";
    public static final String F_DATE_TO = "dateTo";
    public static final String F_PROJECT_SEARCH_SETTINGS = "projectSearchSettings";
    public static final String F_CUSTOMER = "customer";
    public static final String F_REALIZATORS = "realizators";
    public static final String F_INCLUDE_DISABLED = "includeDisabled";
    public static final String F_MONTH_YEAR = "monthYear";
    public static final String F_LABELS = "labels";

    private LocalDate dateFrom;
    private LocalDate dateTo;

    private ProjectSearchSettings projectSearchSettings = new ProjectSearchSettings();
    private List<User> realizators;
    private boolean includeDisabled;
    private WorkType workType = WorkType.ALL;

    private List<LabelPart> labels = new ArrayList<>();
    private Set<Long> labelIds = new HashSet<>();

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

    public ProjectSearchSettings getProjectSearchSettings() {
        return projectSearchSettings;
    }

    public void setCustomerProjectPartDtos(List<CustomerProjectPartDto> customerProjectPartDtos) {
//        if (customerProjectPartDtos == null) {
            projectSearchSettings.setCustomerProjectPartDtoList(customerProjectPartDtos);
//        } else {
//            projectSearchSettings.setCustomerProjectPartDtoList(new ArrayList<>(customerProjectPartDtos));
//        }
    }

    public void setupProjectSearchSettings(ProjectSearchSettings projectSearchSettings) {
        this.projectSearchSettings = projectSearchSettings;
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

    public boolean isIncludeDisabled() { return includeDisabled; }

    public void setIncludeDisabled(boolean includeDisabled) { this.includeDisabled = includeDisabled; }

    public WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }

    public String getMonthYear() {
        return dateFrom.getMonth().getDisplayName(TextStyle.FULL, Locale.US) + " " + dateFrom.getYear();
    }

    public List<LabelPart> getLabels() {
        if (labels == null) labels = new ArrayList<>();
        return labels;
    }
    public void setLabels(List<LabelPart> labels) {
        this.labels = (labels != null) ? labels : new ArrayList<>();
    }

    public Set<Long> getLabelIds() {
        if (labels != null && !labels.isEmpty()) {
            return labels.stream()
                    .filter(Objects::nonNull)
                    .map(LabelPart::getId)
                    .filter(Objects::nonNull)
                    .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
        }
        return (labelIds != null) ? labelIds : new LinkedHashSet<>();
    }
    public void setLabelIds(Set<Long> ids) {
        this.labelIds = (ids != null) ? ids : new LinkedHashSet<>();
    }
}

