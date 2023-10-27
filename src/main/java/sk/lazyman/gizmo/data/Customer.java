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

package sk.lazyman.gizmo.data;

import sk.lazyman.gizmo.util.GizmoUtils;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author lazyman
 */
@Entity
public class Customer implements Serializable {

    public static final String F_ID = "id";
    public static final String F_NAME = "name";
    public static final String F_DESCRIPTION = "description";
    public static final String F_TYPE = "type";
    public static final String F_PARTNER = "partner";
    public static final String F_PROJECTS = "projects";

    private Integer id;
    private String name;
    private String description;
    private CustomerType type;
    private Customer partner;
    private Set<Notification> notifications;
    private Set<Project> projects;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id")
    @SequenceGenerator(name = "customer_id", sequenceName = "g_customer_id_seq", allocationSize = 1, initialValue = 40000)
    public Integer getId() {
        return id;
    }

    @Column(nullable = false)
    public String getName() {
        return name;
    }

    @Column(length = GizmoUtils.DESCRIPTION_SIZE)
    public String getDescription() {
        return description;
    }

    @Enumerated
    public CustomerType getType() {
        return type;
    }

    @ManyToOne()
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_customer_customer"))
    public Customer getPartner() {
        return partner;
    }

    @OneToMany(mappedBy = Notification.F_CUSTOMER)
    public Set<Notification> getNotifications() {
        return notifications;
    }

    @OneToMany(mappedBy = Project.F_CUSTOMER)
    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public void setNotifications(Set<Notification> notifications) {
        this.notifications = notifications;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(CustomerType type) {
        this.type = type;
    }

    public void setPartner(Customer partner) {
        this.partner = partner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (id != null ? !id.equals(customer.id) : customer.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Customer{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", type=").append(type);
        if (partner != null) {
            sb.append(", partner=(").append(partner.getId()).append(',').append(partner.getName());
        }
        sb.append('}');
        return sb.toString();
    }
}
