package sk.lazyman.gizmo.data;

import javax.persistence.*;

/**
 * @author mamut
 */
@Entity
@Table(name = "users")
public class User {

    private Integer id;
    private String userName;
    private String ldapDN;
    private String password;
    private String lastName;
    private String firstName;
    private String email;
    private int role;
    private String certHash;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id")
    @SequenceGenerator(name = "users_id", sequenceName = "users_id_seq")
    @Column(name = "ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "username")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "ldapDN")
    public String getLdapDN() {
        return ldapDN;
    }

    public void setLdapDN(String ldapDN) {
        this.ldapDN = ldapDN;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "lastname")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "firstname")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    @Column(name = "cert_hash")
    public String getCertHash() {
        return certHash;
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
        hash = 53 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Transient
    public String getFullName() {
        StringBuilder builder = new StringBuilder();

        if (firstName != null && !firstName.isEmpty()) {
            builder.append(firstName + " ");
        }

        if (lastName != null && !lastName.isEmpty()) {
            builder.append(lastName);
        }

        return builder.toString().trim();
    }

    @Transient
    public boolean getCommon() {
        if (role == 1) {
            return true;
        }

        return false;
    }
}
