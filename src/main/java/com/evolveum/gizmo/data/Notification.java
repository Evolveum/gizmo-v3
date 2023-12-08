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
import java.util.Date;
import java.util.Set;

/**
 * @author lazyman
 */
@Entity
public class Notification implements Serializable {

    public static final String F_ID = "id";
    public static final String F_DESCRIPTION = "description";
    public static final String F_CREATED = "created";
    public static final String F_OWNER = "owner";
    public static final String F_CUSTOMER = "customer";
    public static final String F_ALARM = "alarm";
    public static final String F_EMAILS = "emails";

    private Integer id;
    private Date created;
    private User owner;

    private Customer customer;
    private Date alarm;
    private Set<String> emails;
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_id")
    @SequenceGenerator(name = "notification_id", sequenceName = "g_notification_id_seq", allocationSize = 1, initialValue = 40000)
    public Integer getId() {
        return id;
    }

    @Column(nullable = false)
    public Date getCreated() {
        return created;
    }

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_notification_owner"))
    public User getOwner() {
        return owner;
    }

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_notification_customer"))
    public Customer getCustomer() {
        return customer;
    }

    @Column(nullable = false)
    public Date getAlarm() {
        return alarm;
    }

    @ElementCollection
    public Set<String> getEmails() {
        return emails;
    }

    @Column(length = GizmoUtils.DESCRIPTION_SIZE)
    public String getDescription() {
        return description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setAlarm(Date alarm) {
        this.alarm = alarm;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notification that = (Notification) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Notification{");
        sb.append("id=").append(id);
        sb.append(", created=").append(created);
        sb.append(", owner=").append(owner);
        sb.append(", customer=").append(customer);
        sb.append(", alarm=").append(alarm);
        sb.append(", emails=").append(emails);
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
