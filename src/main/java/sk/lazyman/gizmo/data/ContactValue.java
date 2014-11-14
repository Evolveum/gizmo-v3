package sk.lazyman.gizmo.data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author lazyman
 */
@Entity
public class ContactValue implements Serializable {

    public static final String F_ID = "id";
    public static final String F_TYPE = "type";
    public static final String F_VALUE = "value";
    public static final String F_CONTACT = "contact";

    private Integer id;
    private ContactType type;
    private String value;
    private Contact contact;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_value_id")
    @SequenceGenerator(name = "contact_value_id", sequenceName = "g_contact_value_id_seq")
    public Integer getId() {
        return id;
    }

    @Enumerated
    public ContactType getType() {
        return type;
    }

    @Column(nullable = false)
    public String getValue() {
        return value;
    }

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_contactValue_contact"))
    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setType(ContactType type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactValue that = (ContactValue) o;

        if (type != that.type) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ContactValue{");
        sb.append("type=").append(type);
        sb.append(", value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
