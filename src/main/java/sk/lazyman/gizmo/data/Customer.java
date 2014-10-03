package sk.lazyman.gizmo.data;

import javax.persistence.*;
import java.io.Serializable;

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

    private Integer id;
    private String name;
    private String description;
    private CustomerType type;
    private Customer partner;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id")
    @SequenceGenerator(name = "customer_id", sequenceName = "customer_id_seq")
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Column(length = 1000)
    public String getDescription() {
        return description;
    }

    @Enumerated
    public CustomerType getType() {
        return type;
    }

    @ManyToOne
    public Customer getPartner() {
        return partner;
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
