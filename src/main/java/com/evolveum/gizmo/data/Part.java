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

package com.evolveum.gizmo.data;

import com.evolveum.gizmo.util.GizmoUtils;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lazyman
 */
@Entity
public class Part implements Serializable {

    public static final String F_ID = "id";
    public static final String F_PROJECT = "project";
    public static final String F_NAME = "name";
    public static final String F_DESCRIPTION = "description";
    public static final String F_COLOR = "color";
    public static final String F_LABELS = "labels";

    private Integer id;
    private String name;
    private String description;
    private Project project;
    private String color;
    private Set<LabelPart> labels = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "part_id")
    @SequenceGenerator(name = "part_id", sequenceName = "g_part_id_seq", allocationSize = 1, initialValue = 40000)
    public Integer getId() {
        return id;
    }

    @ManyToOne(optional = false)
    public Project getProject() {
        return project;
    }

    @Column(nullable = false)
    public String getName() {
        return name;
    }

    @Column(length = GizmoUtils.DESCRIPTION_SIZE)
    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Part that = (Part) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Part{");
        sb.append("id=").append(id);
        sb.append(", project=").append(project);
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", color='").append(color).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "part_label",
            joinColumns = @JoinColumn(name = "part_id",
                    foreignKey = @ForeignKey(name = "fk_part_label_part")),
            inverseJoinColumns = @JoinColumn(name = "label_id",
                    foreignKey = @ForeignKey(name = "fk_part_label_label"))
    )
    public Set<LabelPart> getLabels() {
        return labels;
    }

    public void setLabels(Set<LabelPart> labels) { this.labels = labels; }
    public void addLabel(LabelPart l) { this.labels.add(l); }
    public void removeLabel(LabelPart l) { this.labels.remove(l); }
}
