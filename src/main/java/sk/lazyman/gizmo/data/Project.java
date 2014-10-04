package sk.lazyman.gizmo.data;

import sk.lazyman.gizmo.util.GizmoUtils;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author lazyman
 */
@Entity
public class Project implements Serializable {

    public static final String F_ID = "id";
    public static final String F_NAME = "name";
    public static final String F_DESCRIPTION = "description";
    public static final String F_CUSTOMER = "customer";
    public static final String F_CLOSED = "closed";
    public static final String F_COMMERCIAL = "commercial";

    private Integer id;
    private String name;
    private String description;
    private Customer customer;
    private boolean closed;
    private boolean commercial;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_id")
    @SequenceGenerator(name = "project_id", sequenceName = "project_id_seq")
    public Integer getId() {
        return id;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean isCommercial() {
        return commercial;
    }

    public String getName() {
        return name;
    }

    @ManyToOne
    public Customer getCustomer() {
        return customer;
    }

    @Column(length = GizmoUtils.DESCRIPTION_SIZE)
    public String getDescription() {
        return description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public void setCommercial(boolean commercial) {
        this.commercial = commercial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (id != null ? !id.equals(project.id) : project.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Project{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", customer=").append(customer);
        sb.append(", closed=").append(closed);
        sb.append(", commercial=").append(commercial);
        sb.append('}');
        return sb.toString();
    }
}
