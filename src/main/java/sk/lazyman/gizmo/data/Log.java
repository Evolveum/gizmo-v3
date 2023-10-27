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

import jakarta.persistence.*;
import java.util.Set;

/**
 * @author lazyman
 */
@Entity
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "fk_log_abstractTask"))
public class Log extends AbstractTask {

    public static final String F_CUSTOMER = "customer";

    private Customer customer;
    private Set<Attachment> attachments;

    public Log() {
        setType(TaskType.LOG);
    }

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_log_customer"))
    public Customer getCustomer() {
        return customer;
    }

    @OneToMany(mappedBy = Attachment.F_LOG)
    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Log{");
        sb.append("customer=").append(customer);
        sb.append(", attachments=").append(attachments);
        sb.append('}');
        return sb.toString();
    }
}
