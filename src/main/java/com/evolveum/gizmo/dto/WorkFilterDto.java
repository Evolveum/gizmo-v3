/*
 * Copyright 2015 Viliam Repan (lazyman)
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

package com.evolveum.gizmo.dto;

import com.evolveum.gizmo.data.User;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * @author lazyman
 */
public class WorkFilterDto implements Serializable {

    public static final String F_FROM = "from";
    public static final String F_TO = "to";
    public static final String F_PROJECTS = "projects";
    public static final String F_PROJECT = "project";
    public static final String F_REALIZATOR = "realizator";
    public static final String F_REALIZATORS = "realizators";
    public static final String F_TYPE = "type";
    public static final String F_MULTIPLE = "multiple";

    private LocalDate from;
    private LocalDate to;

    private User realizator;
//    private boolean multiple;
//    private List<CustomerProjectPartDto> projects;
//    private List<User> realizators;
//    private WorkType type = WorkType.ALL;

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public User getRealizator() {
        return realizator;
    }

    public void setRealizator(User realizator) {
        this.realizator = realizator;
    }

    public String getMonth() {
        return from.getMonth().getDisplayName(TextStyle.FULL, Locale.US);
    }

    //    public List<CustomerProjectPartDto> getProjects() {
//        if (projects == null) {
//            projects = new ArrayList<>();
//        }
//        return projects;
//    }
//
//    public void setProjects(List<CustomerProjectPartDto> projects) {
//        this.projects = projects;
//    }
//
//    public CustomerProjectPartDto getProject() {
//        List<CustomerProjectPartDto> projects = getProjects();
//        if (projects.isEmpty()) {
//            return null;
//        }
//
//        return projects.get(0);
//    }
//
//    public void setProject(CustomerProjectPartDto project) {
//        List<CustomerProjectPartDto> projects = getProjects();
//        projects.clear();
//
//        if (project != null) {
//            projects.add(project);
//        }
//    }
//
//    public List<User> getRealizators() {
//        if (realizators == null) {
//            realizators = new ArrayList<>();
//        }
//        return realizators;
//    }
//
//    public void setRealizators(List<User> realizators) {
//        this.realizators = realizators;
//    }
//
//    public User getRealizator() {
//        List<User> realizators = getRealizators();
//        if (realizators.isEmpty()) {
//            return null;
//        }
//
//        return realizators.get(0);
//    }
//
//    public void setRealizator(User realizator) {
//        List<User> realizators = getRealizators();
//        realizators.clear();
//
//        if (realizator != null) {
//            realizators.add(realizator);
//        }
//    }
//
//    public WorkType getType() {
//        return type;
//    }
//
//    public void setType(WorkType type) {
//        if (type == null) {
//            type = WorkType.ALL;
//        }
//        this.type = type;
//    }
//
//    public boolean isMultiple() {
//        return multiple;
//    }
//
//    public void setMultiple(boolean multiple) {
//        this.multiple = multiple;
//    }
}
