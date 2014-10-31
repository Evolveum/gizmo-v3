package sk.lazyman.gizmo.data;

import sk.lazyman.gizmo.util.GizmoUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author lazyman
 */
@Entity
public class Customer implements Serializable {

    public static final String F_ID = "id";
    public static final String F_NAME = "name";
    public static final String F_DESCRIPTION = "description";
    public static final String F_TYPE = "type";
    public static final String F_PARTNER = "partner";
    public static final String F_PROJECTS = "projects";

    private Integer id;
    private String name;
    private String description;
    private CustomerType type;
    private Customer partner;
    private Set<Notification> notifications;
    private Set<Project> projects;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id")
    @SequenceGenerator(name = "customer_id", sequenceName = "g_customer_id_seq")
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Column(length = GizmoUtils.DESCRIPTION_SIZE)
    public String getDescription() {
        return description;
    }

    @Enumerated
    public CustomerType getType() {
        return type;
    }

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_customer_customer"))
    public Customer getPartner() {
        return partner;
    }

    @OneToMany(mappedBy = Notification.F_CUSTOMER)
    public Set<Notification> getNotifications() {
        return notifications;
    }

    @OneToMany(mappedBy = Project.F_CUSTOMER)
    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public void setNotifications(Set<Notification> notifications) {
        this.notifications = notifications;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(CustomerType type) {
        this.type = type;
    }

    public void setPartner(Customer partner) {
        this.partner = partner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (id != null ? !id.equals(customer.id) : customer.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Customer{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", type=").append(type);
        sb.append(", partner=").append(partner);
        sb.append('}');
        return sb.toString();
    }
}
