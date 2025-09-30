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

import java.io.Serializable;
import java.util.Date;

public class DownloadSettingsDto implements Serializable {

    public static final String F_PER_USER = "perUser";
    public static final String F_PER_CUSTOMER = "perCustomer";
    public static final String F_REPORT_NAME = "reportName";

    private boolean perUser = true;
    private boolean perCustomer = true;
    private String reportName;
    private ReportType reportType = ReportType.WORK_REPORT;


    public DownloadSettingsDto() {
        this.reportName = "Export_" + new Date(System.currentTimeMillis()) + ".xlsx";
    }

    public boolean isPerUser() {
        return perUser;
    }

    public boolean isPerCustomer() {
        return perCustomer;
    }

    public void setReportName(String reportName) { this.reportName = reportName; }

    public String getReportName() { return reportName; }

    public void setPerUser(boolean perUser) { this.perUser = perUser; }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
}
