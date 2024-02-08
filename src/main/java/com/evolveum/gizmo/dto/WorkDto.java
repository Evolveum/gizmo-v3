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

import com.evolveum.gizmo.data.*;
import com.evolveum.gizmo.repository.PartRepository;
import com.evolveum.gizmo.security.GizmoPrincipal;
import com.evolveum.gizmo.security.SecurityUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class WorkDto implements Serializable {

    public static final String F_DATE = "date";
    public static final String F_TRACK_ID = "trackId";
    public static final String F_DESCRIPTION = "description";
    public static final String F_WORK_LENGTH = "workLength";
    public static final String F_INVOICE_LENGTH = "invoiceLength";
    public static final String F_CUSTOMER_PROJECT_PART = "customerProjectPart";


    private User realizator;
    private double invoiceLength;
    private double workLength;
    private LocalDate date;
    private String trackId;
    private String description;
    private List<CustomerProjectPartDto> customerProjectPart = new ArrayList<>();

    public WorkDto() {
        GizmoPrincipal principal = SecurityUtils.getPrincipalUser();

        this.realizator = principal.getUser();
        this.date = LocalDate.now();
    }

    public WorkDto(Work work) {
        this.realizator = work.getRealizator();
        this.invoiceLength = work.getInvoiceLength();
        this.workLength = work.getWorkLength();
        this.date = work.getDate();
        this.trackId = work.getTrackId();
        this.description = work.getDescription();

        Part part = work.getPart();
        if (part != null) {
            Project project = part.getProject();
            Customer customer = project.getCustomer();
            customerProjectPart.clear();
            customerProjectPart.add(new CustomerProjectPartDto(
                    customer.getName(), project.getName(), part.getName(),
                    customer.getId(), project.getId(), part.getId()));
        }
    }

    public double getInvoiceLength() {
        return invoiceLength;
    }

    public double getWorkLength() {
        return workLength;
    }


    public void setInvoiceLength(double invoiceLength) {
        this.invoiceLength = invoiceLength;
    }

    public void setWorkLength(double workLength) {
        this.workLength = workLength;
    }

    public List<CustomerProjectPartDto> getCustomerProjectPart() {
        return customerProjectPart;
    }

    public void setCustomerProjectPart(List<CustomerProjectPartDto> customerProjectPart) {
        this.customerProjectPart = customerProjectPart;
    }

    public User getRealizator() {
        return realizator;
    }

    public void setRealizator(User realizator) {
        this.realizator = realizator;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public List<Work> createWorks(PartRepository repositoryPart) {
        return customerProjectPart.stream()
                .map(project -> prepareWork(repositoryPart, project))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Work prepareWork(PartRepository repositoryPart, CustomerProjectPartDto project) {
        Optional<Part> optionalPart = repositoryPart.findById(project.getPartId());
        if (optionalPart.isEmpty()) {
            return null;
        }
        Work preparedWork = new Work();
        Part part = optionalPart.get();
        preparedWork.setPart(part);
        preparedWork.setInvoiceLength(invoiceLength/ customerProjectPart.size());
        preparedWork.setWorkLength(workLength/ customerProjectPart.size());
        preparedWork.setDate(date);
        preparedWork.setRealizator(realizator);
        preparedWork.setTrackId(trackId);
        preparedWork.setDescription(description);
        return preparedWork;
    }
}
