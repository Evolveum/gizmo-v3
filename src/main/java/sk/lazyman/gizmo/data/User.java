package sk.lazyman.gizmo.data;

import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author lazyman
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    public static final String F_ID = "id";
    public static final String F_USER_NAME = "userName";
    public static final String F_LDAP_DN = "ldapDN";
    public static final String F_PASSWORD = "password";
    public static final String F_LAST_NAME = "lastName";
    public static final String F_FIRST_NAME = "firstName";
    public static final String F_EMAIL = "email";
    public static final String F_ROLE = "role";
    public static final String F_CERT_HASH = "certHash";

    private Integer id;
    private String userName;
    private String ldapDN;
    private String password;
    private String lastName;
    private String firstName;
    private String email;
    private int role;                   //todo change to Role
    private String certHash;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id")
    @SequenceGenerator(name = "users_id", sequenceName = "users_id_seq")
    @Column(name = "ID")
    public Integer getId() {
        return id;
    }

    @Column(name = "username")
    public String getUserName() {
        return userName;
    }

    @Column(name = "ldapDN")
    public String getLdapDN() {
        return ldapDN;
    }

    public String getPassword() {
        return password;
    }

    @Column(name = "lastname")
    public String getLastName() {
        return lastName;
    }

    @Column(name = "firstname")
    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public int getRole() {
        return role;
    }

    @Column(name = "cert_hash")
    public String getCertHash() {
        return certHash;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLdapDN(String ldapDN) {
        this.ldapDN = ldapDN;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void setCertHash(String certHash) {
        this.certHash = certHash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof User)) {
            return false;
        }

        User user = (User) obj;
        if (user.getId() == id) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Transient
    public String getFullName() {
        return StringUtils.join(new Object[]{firstName, lastName}, ' ');
    }
}
