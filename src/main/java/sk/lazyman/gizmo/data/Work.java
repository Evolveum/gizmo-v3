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

import com.querydsl.core.annotations.QueryInit;

import javax.persistence.*;

/**
 * @author lazyman
 */
@Entity
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "fk_work_abstractTask"))
public class Work extends AbstractTask {

    public static final String F_PART = "part";
    public static final String F_INVOICE_LENGTH = "invoiceLength";

    private Part part;
    private double invoiceLength;

    public Work() {
        setType(TaskType.WORK);
    }

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_work_part"))
    @QueryInit("project.*")
    public Part getPart() {
        return part;
    }

    public double getInvoiceLength() {
        return invoiceLength;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public void setInvoiceLength(double invoiceLength) {
        this.invoiceLength = invoiceLength;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Work{");
        sb.append("part=").append(part);
        sb.append(", invoiceLength=").append(invoiceLength);
        sb.append('}');
        return sb.toString();
    }
}
