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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lazyman
 */
public class ReportSearchSummaryDto implements Serializable {

    public static final String F_PROJECTS = "projectS";
    public static final String F_FROM = "from";
    public static final String F_TO = "to";
    public static final String F_WORK = "work";
    public static final String F_INVOICE = "invoice";

    private List<User> realizators;
    private List<CustomerProjectPartDto> projects;
    private Date from;
    private Date to;
    private double work;
    private double invoice;

    public List<CustomerProjectPartDto> getProjects() {
        if (projects == null) {
            projects = new ArrayList<>();
        }
        return projects;
    }

    public void setProjects(List<CustomerProjectPartDto> projects) {
        this.projects = projects;
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

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public double getWork() {
        return work;
    }

    public void setWork(double work) {
        this.work = work;
    }

    public double getInvoice() {
        return invoice;
    }

    public void setInvoice(double invoice) {
        this.invoice = invoice;
    }
}
