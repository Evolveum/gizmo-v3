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

package sk.lazyman.gizmo.dto;

import sk.lazyman.gizmo.data.User;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lazyman
 */
public class BulkDto implements Serializable {

    public static final String F_REALIZATOR = "realizator";
    public static final String F_FROM = "from";
    public static final String F_TO = "to";
    public static final String F_PART = "part";
    public static final String F_DESCRIPTION = "description";

    private User realizator;
    private Date from;
    private Date to;
    private String description;
    private CustomerProjectPartDto part;

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public CustomerProjectPartDto getPart() {
        return part;
    }

    public void setPart(CustomerProjectPartDto part) {
        this.part = part;
    }

    public User getRealizator() {
        return realizator;
    }

    public void setRealizator(User realizator) {
        this.realizator = realizator;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
