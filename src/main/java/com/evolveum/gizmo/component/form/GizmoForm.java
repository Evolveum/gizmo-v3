/*
 *  Copyright (C) 2025 Evolveum
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

package com.evolveum.gizmo.component.form;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.springframework.security.web.csrf.CsrfToken;

public class GizmoForm<T> extends Form<T> {

    public GizmoForm(String id) {
        super(id);

        HttpServletRequest request = ((ServletWebRequest) getRequest()).getContainerRequest();
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (token != null) {
            HiddenField<String> csrfField = new HiddenField<>(token.getParameterName(), Model.of(token.getToken()));
            csrfField.add(new AttributeModifier("name", token.getParameterName()));
            add(csrfField);
        }
    }
}
