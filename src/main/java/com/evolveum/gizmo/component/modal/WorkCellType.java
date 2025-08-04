/*
 *  Copyright (C) 2024 Evolveum
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

package com.evolveum.gizmo.component.modal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

enum WorkCellType {
    DATE("Date", LocalDate.class, "getDate", new ReportType[]{ReportType.GENERIC, ReportType.CUSTOMER}),
    FROM("From", LocalTime.class, "getFrom", new ReportType[]{ReportType.GENERIC, ReportType.CUSTOMER}),
    TO("To", LocalTime.class, "getTo", new ReportType[]{ReportType.GENERIC, ReportType.CUSTOMER}),
    REALIZATOR("Realizator", String.class, "getRealizator.getFullName", new ReportType[]{ReportType.GENERIC, ReportType.CUSTOMER}),
    CUSTOMER("Customer", String.class, "getPart.getProject.getCustomer.getName", new ReportType[]{ReportType.GENERIC}),
    PROJECT("Project", String.class, "getPart.getProject.getName", new ReportType[]{ReportType.CUSTOMER, ReportType.GENERIC}),
    PART("Part", String.class, "getPart.getName", new ReportType[]{ReportType.CUSTOMER, ReportType.GENERIC}),
    TRACK_ID("Track ID", String.class, "getTrackId", new ReportType[]{ReportType.GENERIC}),
    DESCRIPTION("Description", String.class, "getDescription", new ReportType[]{ReportType.CUSTOMER, ReportType.GENERIC}),
    WORK_LENGTH("Length", double.class, "getWorkLength", new ReportType[]{ReportType.CUSTOMER, ReportType.GENERIC, ReportType.INTERNAL}),
//    INVOICE_LENGTH("Invoice", double.class, "getInvoiceLength", new ReportType[]{ReportType.CUSTOMER}),

    USER("Realizator", String.class, "getFullName", new ReportType[]{ReportType.SUMMARY}),
    PART_NAME("Project", String.class, "getName", new ReportType[]{ReportType.SUMMARY}),
    SUMMARY_WORK_LENGTH("Work", String.class, "getLength", new ReportType[]{ReportType.SUMMARY}),
    SUMMARY_INVOICE_LENGTH("Invoice", String.class, "getInvoice", new ReportType[]{ReportType.SUMMARY})
    ;

    private String displayName;
    private Class<?> type;
    private String getMethod;
    private ReportType[] reportType;

    WorkCellType(String displayName, Class<?> type, String getMethod, ReportType[] reportType) {
        this.displayName = displayName;
        this.type = type;
        this.getMethod = getMethod;
        this.reportType = reportType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Class<?> getType() {
        return type;
    }

    public String getGetMethod() {
        return getMethod;
    }

    public ReportType[] getReportType() {
        return reportType;
    }

    public static List<WorkCellType> getCellsForReport(ReportType reportType) {
        return Arrays.stream(values()).filter(r -> Arrays.stream(r.reportType).anyMatch(type -> ReportType.ALL == type || type == reportType)).toList();
    }
}
