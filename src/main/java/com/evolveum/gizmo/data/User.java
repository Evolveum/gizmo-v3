/*
 * Copyright 2015 Viliam Repan (lazyman)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evolveum.gizmo.data;

import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * @author lazyman
 */
@Entity//(name = "g_user")
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

    public static final String M_FULL_NAME = "fullName";

    private Integer id;
    private String name;
    private String givenName;
    private String familyName;
    private String ldapDn;
    private String password;
    private boolean enabled;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id")
    @SequenceGenerator(name = "user_id", sequenceName = "g_user_id_seq", allocationSize = 1, initialValue = 40000)
    public Integer getId() {
        return id;
    }

    @Column(nullable = false)
    public String getName() {
        return name;
    }

//    @Column(name = "givenname")
    public String getGivenName() {
        return givenName;
    }

//    @Column(name = "familyname")
    public String getFamilyName() {
        return familyName;
    }

//    @Column(name = "ldapdn")
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
