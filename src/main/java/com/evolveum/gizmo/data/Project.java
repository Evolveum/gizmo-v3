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

package com.evolveum.gizmo.data;

import com.evolveum.gizmo.util.GizmoUtils;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author lazyman
 */
@Entity
public class Project implements Serializable {

    public static final String F_ID = "id";
    public static final String F_NAME = "name";
    public static final String F_DESCRIPTION = "description";
    public static final String F_CUSTOMER = "customer";
    public static final String F_CLOSED = "closed";
    public static final String F_COMMERCIAL = "commercial";

    private Integer id;
    private String name;
    private String description;
    private Customer customer;
    private boolean closed;
    private boolean commercial;
    private Set<Part> parts;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_id")
    @SequenceGenerator(name = "project_id", sequenceName = "g_project_id_seq", allocationSize = 1, initialValue = 40000)
    public Integer getId() {
        return id;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean isCommercial() {
        return commercial;
    }

    @Column(nullable = false)
    public String getName() {
        return name;
    }

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_project_customer"))
    public Customer getCustomer() {
        return customer;
    }

    @Column(length = GizmoUtils.DESCRIPTION_SIZE)
    public String getDescription() {
        return description;
    }

    @OneToMany(mappedBy = Part.F_PROJECT)
    public Set<Part> getParts() {
        return parts;
    }

    public void setParts(Set<Part> parts) {
        this.parts = parts;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public void setCommercial(boolean commercial) {
        this.commercial = commercial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (id != null ? !id.equals(project.id) : project.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Project{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", customer=").append(customer);
        sb.append(", closed=").append(closed);
        sb.append(", commercial=").append(commercial);
        sb.append('}');
        return sb.toString();
    }
}
