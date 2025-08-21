package com.evolveum.gizmo.data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;


@Entity
@Table(name = "label",
        uniqueConstraints = @UniqueConstraint(name = "uq_g_label_code", columnNames = "code"))
public class LabelPart implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LabelPart)) return false;
        LabelPart other = (LabelPart) o;
        return id != null && id.equals(other.id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }

    @Override public String toString() { return code + " — " + name; }
}
