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

package com.evolveum.gizmo.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProjectSearchSettings implements Serializable {

    public static final String F_CUSTOMER_SEARCH = "customerSearch";
    public static final String F_PROJECT_SEARCH = "projectSearch";
    public static final String F_PART_SEARCH = "partSearch";

    public static final String F_CUSTOMER = "customerProjectPartDtoList";

    private boolean customerSearch = true;
    private boolean projectSearch = true;
    private boolean partSearch = false;
    private List<CustomerProjectPartDto> customerProjectPartDtoList = new ArrayList<>();

    public ProjectSearchSettings(boolean customerSearch, boolean projectSearch, boolean partSearch) {
        this.customerSearch = customerSearch;
        this.projectSearch = projectSearch;
        this.partSearch = partSearch;
    }

    public ProjectSearchSettings() {
    }

    public boolean isCustomerSearch() {
        return customerSearch;
    }

    public boolean isPartSearch() {
        return partSearch;
    }

    public boolean isProjectSearch() {
        return projectSearch;
    }


    public List<CustomerProjectPartDto> getCustomerProjectPartDtoList() {
        return customerProjectPartDtoList;
    }

    public void setPartSearch(boolean partSearch) {
        this.partSearch = partSearch;
    }

    public void setCustomerProjectPartDtoList(List<CustomerProjectPartDto> customerProjectPartDtoList) {
        if (customerProjectPartDtoList == null) {
            this.customerProjectPartDtoList = new ArrayList<>();
        } else {
            this.customerProjectPartDtoList = new ArrayList<>(customerProjectPartDtoList);
        }
    }
}
