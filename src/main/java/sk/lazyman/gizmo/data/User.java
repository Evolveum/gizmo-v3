package sk.lazyman.gizmo.data;

import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author lazyman
 */
@Entity
public class User implements Serializable {

    public static final String F_ID = "id";
    public static final String F_NAME = "name";
    public static final String F_GIVEN_NAME = "givenName";
    public static final String F_FAMILY_NAME = "familyName";
    public static final String F_LDAP_DN = "ldapDn";
    public static final String F_ENABLED = "enabled";

    private Integer id;
    private String name;
    private String givenName;
    private String familyName;
    private String ldapDn;
    private boolean enabled;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id")
    @SequenceGenerator(name = "user_id", sequenceName = "g_user_id_seq")
    public Integer getId() {
        return id;
    }

    @Column(unique = true)
    public String getName() {
        return name;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    @Column(unique = true)
    public String getLdapDn() {
        return ldapDn;
    }

    public boolean isEnabled() {
        return enabled;
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
}
