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

package sk.lazyman.gizmo.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lazyman
 */
public class ReportSearchSummaryDto implements Serializable {

    public static final String F_PROJECT = "project";
    public static final String F_FROM = "from";
    public static final String F_TO = "to";
    public static final String F_WORK = "work";
    public static final String F_INVOICE = "invoice";

    private CustomerProjectPartDto project;
    private Date from;
    private Date to;
    private double work;
    private double invoice;

    public CustomerProjectPartDto getProject() {
        return project;
    }

    public void setProject(CustomerProjectPartDto project) {
        this.project = project;
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
