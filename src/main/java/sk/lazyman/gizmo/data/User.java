package sk.lazyman.gizmo.data;

/**
 * @author mamut
 */
public class User {

    private long id;

    private String userName;

    private String ldapDN;

    private String password;

    private String lastName;

    private String firstName;

    private String email;

    private int role;

    private String certHash;

    public User() {
        this(0, null, null);
    }

    public User(long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "USER: id=" + id + ", firstName=" + firstName + ", lastName=" + lastName +
                ", userName=" + userName + ", email=" + email + ", ldapDN=" + ldapDN + ", role=" + role;
    }

    public String getStrId() {
        return Long.toString(id);
    }

    public void setId(String id) {
        this.id = Long.parseLong(id);
    }

    public void setId(Long id) {
        this.id = id.longValue();
    }

    public void setId(User u) {
        this.id = u.getId();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

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

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getRoleStr() {
        return Integer.toString(role);
    }

    public void setRoleStr(String role) {
        this.role = Integer.parseInt(role);
    }

    public String getCertHash() {
        return certHash;
    }

    public void setCertHash(String certHash) {
        this.certHash = certHash;
    }

    public boolean getCommon() {
        if (role == 1) {
            return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof User)) {
            return false;
        }

        User user = (User)obj;
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
}
