/*
 *  Copyright (C) 2023 Evolveum
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

/**
 * @author lazyman
 */
public class TaskLength implements Serializable {

    private double length;
    private double invoice;

    public TaskLength(Double length, Double invoice) {
        this.length = length != null ? length : 0;
        this.invoice = invoice != null ? invoice : 0;
    }

    public double getLength() {
        return length;
    }

    public double getInvoice() {
        return invoice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskLength that = (TaskLength) o;

        if (Double.compare(that.invoice, invoice) != 0) return false;
        if (Double.compare(that.length, length) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(length);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(invoice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskLength{");
        sb.append("l=").append(length);
        sb.append(",i=").append(invoice);
        sb.append('}');
        return sb.toString();
    }
}
