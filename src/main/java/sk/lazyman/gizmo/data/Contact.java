package sk.lazyman.gizmo.data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author lazyman
 */
@Entity
public class Contact implements Serializable {

    public static final String F_ID = "id";
    public static final String F_NAME = "name";
    public static final String F_DESCRIPTION = "description";
    public static final String F_STREET = "street";
    public static final String F_CITY = "city";
    public static final String F_ZIP = "zip";
    public static final String F_COUNTRY = "country";
    public static final String F_CONTACTS = "contacts";

    private Integer id;

    private String name;
    private String description;

    private String street;
    private String city;
    private String zip;
    private String country;

    private Set<ContactValue> contacts;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_id")
    @SequenceGenerator(name = "contact_id", sequenceName = "contact_id_seq")
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getZip() {
        return zip;
    }

    public String getCountry() {
        return country;
    }

    @OneToMany
    public Set<ContactValue> getContacts() {
        return contacts;
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

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setContacts(Set<ContactValue> contacts) {
        this.contacts = contacts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (id != null ? !id.equals(contact.id) : contact.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Contact{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", street='").append(street).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", zip='").append(zip).append('\'');
        sb.append(", country='").append(country).append('\'');
        sb.append(", contacts=").append(contacts);
        sb.append('}');
        return sb.toString();
    }
}
