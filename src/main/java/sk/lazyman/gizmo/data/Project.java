package sk.lazyman.gizmo.data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author lazyman
 */
@Entity
@Table(name = "project")
public class Project implements Serializable {

    private Integer id;
    private String name;
    private Company customer;
    private String desc;
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
    @JoinColumn(name = "customer_id")
    public Company getCustomer() {
        return customer;
    }

    @Column(name = "description", length = 1024)
    public String getDesc() {
        return desc;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCustomer(Company customer) {
        this.customer = customer;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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
        StringBuilder builder = new StringBuilder();
        builder.append("id=" + id);
        builder.append(", name=" + name);
        builder.append(", customer=[" + customer);
        builder.append("], desc= " + desc);

        return builder.toString();
    }
}
