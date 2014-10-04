package sk.lazyman.gizmo.data;

import sk.lazyman.gizmo.util.GizmoUtils;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author lazyman
 */
@Entity
public class Part implements Serializable {

    public static final String F_ID = "id";
    public static final String F_PROJECT = "project";
    public static final String F_NAME = "name";
    public static final String F_DESCRIPTION = "description";

    private Integer id;
    private String name;
    private String description;
    private Project project;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "part_id")
    @SequenceGenerator(name = "part_id", sequenceName = "g_part_id_seq")
    public Integer getId() {
        return id;
    }

    @ManyToOne
    public Project getProject() {
        return project;
    }

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

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setName(String name) {
        this.name = name;
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
        sb.append('}');
        return sb.toString();
    }
}
