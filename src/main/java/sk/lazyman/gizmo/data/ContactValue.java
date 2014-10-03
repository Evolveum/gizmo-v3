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

    private Integer id;
    private ContactType type;
    private String value;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_value_id")
    @SequenceGenerator(name = "contact_value_id", sequenceName = "contact_value_id_seq")
    public Integer getId() {
        return id;
    }

    @Enumerated
    public ContactType getType() {
        return type;
    }

    public String getValue() {
        return value;
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
