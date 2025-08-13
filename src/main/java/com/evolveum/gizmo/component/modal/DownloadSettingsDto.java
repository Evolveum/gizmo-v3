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
    public static final String F_SUMMARY = "summary";
    public static final String F_REPORT_NAME = "reportName";
    public static final String F_CUSTOMER_REPORT = "customerReport";

    private boolean perUser = true;
    private boolean summary;
    private boolean customerReport = true;
    private String reportName;


    public DownloadSettingsDto() {
        this.reportName = "Export_" + new Date(System.currentTimeMillis()) + ".xlsx";
    }

    public boolean isSummary() {
        return summary;
    }

    public boolean isPerUser() {
        return perUser;
    }

    public void setReportName(String reportName) { this.reportName = reportName; }

}
