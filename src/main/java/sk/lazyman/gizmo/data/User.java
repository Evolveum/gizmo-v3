package sk.lazyman.gizmo.data;

import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author lazyman
 */
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "ldapDn", name = "u_ldapdn"),
        @UniqueConstraint(columnNames = "name", name = "u_name")
})
public class User implements Serializable {

    public static final String F_ID = "id";
    public static final String F_NAME = "name";
    public static final String F_GIVEN_NAME = "givenName";
    public static final String F_FAMILY_NAME = "familyName";
    public static final String F_LDAP_DN = "ldapDn";
    public static final String F_ENABLED = "enabled";
    public static final String F_PASSWORD = "password";

    private Integer id;
    private String name;
    private String givenName;
    private String familyName;
    private String ldapDn;
    private String password;
    private boolean enabled;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id")
    @SequenceGenerator(name = "user_id", sequenceName = "g_user_id_seq")
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getLdapDn() {
        return ldapDn;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setLdapDn(String ldapDn) {
        this.ldapDn = ldapDn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Transient
    public String getFullName() {
        return StringUtils.join(new Object[]{givenName, familyName}, ' ');
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", givenName='").append(givenName).append('\'');
        sb.append(", familyName='").append(familyName).append('\'');
        sb.append(", enabled=").append(enabled);
        sb.append(", ldapDn='").append(ldapDn).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
