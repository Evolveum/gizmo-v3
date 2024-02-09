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

enum WorkCellType {
    DATE("Date", LocalDate.class, "getDate"),
    REALIZATOR("Realizator", String.class, "getRealizator.getFullName"),
    CUSTOMER("Customer", String.class, "getPart.getProject.getCustomer.getName"),
    PROJECT("Project", String.class, "getPart.getProject.getName"),
    PART("Part", String.class, "getPart.getName"),
    TRACK_ID("Track ID", String.class, "getTrackId"),
    DESCRIPTION("Description", String.class, "getDescription"),
    WORK_LENGTH("Length", double.class, "getWorkLength"),
    INVOICE_LENGTH("Invoice", double.class, "getInvoiceLength");

    private String displayName;
    private Class<?> type;
    private String getMethod;

    WorkCellType(String displayName, Class<?> type, String getMethod) {
        this.displayName = displayName;
        this.type = type;
        this.getMethod = getMethod;
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
}
