package sk.lazyman.gizmo.web;

import java.io.Serializable;

/**
 * @author lazyman
 */
public class LoginDto implements Serializable {

    public static final String F_USERNAME = "username";
    public static final String F_PASSWORD = "password";

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
