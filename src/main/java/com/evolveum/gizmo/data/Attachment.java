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

/**
 * @author lazyman
 */
@Entity
public class Attachment implements Serializable {

    public static final String F_ID = "id";
    public static final String F_NAME = "name";
    public static final String F_VALUE = "value";
    public static final String F_LOG = "log";
    public static final String F_DESCRIPTION = "description";

    private Integer id;
    private String name;
    private String description;
    private byte[] value;
    private Log log;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_id")
    @SequenceGenerator(name = "attachment_id", sequenceName = "g_attachment_id_seq", allocationSize = 1, initialValue = 40000)
    public Integer getId() {
        return id;
    }

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_attachment_log"))
    public Log getLog() {
        return log;
    }

    @Column(nullable = false)
    public String getName() {
        return name;
    }

    @Column(length = GizmoUtils.DESCRIPTION_SIZE)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getValue() {
        return value;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attachment that = (Attachment) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Attachment{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", log='").append(log).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
