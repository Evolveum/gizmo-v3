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

package com.evolveum.gizmo.component.form;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * @author lazyman
 */
public class PasswordInput extends FormInput<String> {

    private static final String ID_INPUT_2 = "input2";

    public PasswordInput(String id, IModel<String> model) {
        super(id, model);
    }

    @Override
    protected void initLayout() {
        PasswordTextField text = new PasswordTextField(ID_INPUT, createCustomInputModel(getModel()));
        text.setRequired(false);
        add(text);

        PasswordTextField text2 = new PasswordTextField(ID_INPUT_2, new Model<>());
        text2.setRequired(false);
        text2.setLabel(createStringResource("PasswordInput.confirmPassword"));
        add(text2);
    }

    private IModel<String> createCustomInputModel(final IModel<String> model) {
        return new IModel<>() {

            @Override
            public String getObject() {
                return model.getObject();
            }

            @Override
            public void setObject(String object) {
                if (StringUtils.isEmpty(object)) {
                    return;
                }
                model.setObject(object);
            }

            @Override
            public void detach() {
            }
        };
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        FormComponent fc1 = (FormComponent) get(ID_INPUT);
        FormComponent fc2 = (FormComponent) get(ID_INPUT_2);

        Form form = findParent(Form.class);
        form.add(new EqualPasswordInputValidator(fc2, fc1));
    }
}
