/*
 *  Copyright (C) 2023 Evolveum
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.evolveum.gizmo.web.app;

import com.evolveum.gizmo.component.AjaxButton;
import com.evolveum.gizmo.component.AjaxSubmitButton;
import com.evolveum.gizmo.component.VisibleEnableBehaviour;
import com.evolveum.gizmo.component.form.*;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.data.Work;
import com.evolveum.gizmo.dto.ProjectSearchSettings;
import com.evolveum.gizmo.repository.UserRepository;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;

import java.util.Optional;

/**
 * @author lazyman
 */
@MountPath("/app/user")
public class PageUser extends PageAppUsers {

    public static final String USER_ID = "userId";

    private static final String ID_FORM = "form";
    private static final String ID_NAME = "name";
    private static final String ID_GIVEN_NAME = "givenName";
    private static final String ID_FAMILY_NAME = "familyName";
    private static final String ID_LDAP_DN = "ldapDn";
    private static final String ID_ENABLED = "enabled";
    private static final String ID_PASSWORD = "password";
    private static final String ID_SAVE = "save";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_ALLOCATION = "allocation";

    private IModel<User> model;

    public PageUser() {
        model = new LoadableModel<>(false) {

            @Override
            protected User load() {
                return loadUser();
            }
        };

        initLayout();
    }

    private User loadUser() {
        Integer userId = getIntegerParam(USER_ID);

        if (userId == null) {
            return new User();
        }

        UserRepository repo = getUserRepository();
        Optional<User> user = repo.findById(userId);
        return user.get();
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        TextField<String> username = new TextField<>(ID_NAME, new PropertyModel<>(model, User.F_NAME));
        form.add(username);

        TextField<String> firstName = new TextField<>(ID_GIVEN_NAME, new PropertyModel<>(model, User.F_GIVEN_NAME));
        form.add(firstName);

        TextField<String> lastName = new TextField<>(ID_FAMILY_NAME, new PropertyModel<>(model, User.F_FAMILY_NAME));
        form.add(lastName);

        TextField<String> ldapDn = new TextField<>(ID_LDAP_DN, new PropertyModel<>(model, User.F_LDAP_DN));
        ldapDn.setEnabled(false);
        form.add(ldapDn);

        TextField<String> allocation = new TextField<>(ID_ALLOCATION, new PropertyModel<>(model, User.F_ALLOCATION));
        form.add(allocation);

        AjaxCheckBox enabled = new AjaxCheckBox(ID_ENABLED, new PropertyModel<>(model, User.F_ENABLED)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        };
        enabled.setOutputMarkupId(true);
        form.add(enabled);

        PasswordInput password = new PasswordInput(ID_PASSWORD, new PropertyModel<>(model, User.F_PASSWORD));
        form.add(password);

        IconButton save = new IconButton(ID_SAVE, createStringResource("GizmoApplication.button.save"), createStringResource("fa fa-save"), createStringResource("btn-success")) {

            @Override
            public void submitPerformed(AjaxRequestTarget target) {
                userSavePerformed(target);
            }
        };

        form.add(save);

        IconButton cancel = new IconButton(ID_CANCEL, createStringResource("GizmoApplication.button.cancel"), createStringResource("fa fa-times"), createStringResource("btn-danger")) {

            @Override
            public void submitPerformed(AjaxRequestTarget target) {
                cancelPerformed(target);
            }
        };
        form.add(cancel);
    }

    private void cancelPerformed(AjaxRequestTarget target) {
        setResponsePage(PageUsers.class);
    }

    private void userSavePerformed(AjaxRequestTarget target) {
        try {
            UserRepository repo = getUserRepository();
            User user = model.getObject();
            if (user.getPassword() != null) {
                user.setPassword(GizmoUtils.toSha1(user.getPassword()));
            }

            repo.saveAndFlush(user);

            PageUsers next = new PageUsers();
            next.success(getString("Message.userSavedSuccessfully"));
            setResponsePage(next);
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveUser", ex, target);
        }
    }
}
