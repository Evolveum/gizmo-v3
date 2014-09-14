package sk.lazyman.gizmo.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sk.lazyman.gizmo.data.User;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author lazyman
 */
public class GizmoPrincipal implements UserDetails {

    private User user;

    public GizmoPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public String getFullName() {
        return user.getFullName();
    }

    public Integer getUserId() {
        return user.getId();
    }

    public User getUser() {
        return user;
    }
}
