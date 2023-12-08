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

package com.evolveum.gizmo.component.form;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

/**
 * @author lazyman
 */
public class HPasswordGroup extends HFormGroup<PasswordInput, String> {

    public HPasswordGroup(String id, IModel<String> value, IModel<String> label,
                          String labelSize, String inputSize, String feedbackSize, boolean required) {
        super(id, value, label, labelSize, inputSize, feedbackSize, required);
    }

    @Override
    protected FormInput createInput(String componentId, IModel<String> model, IModel<String> placeholder) {
        PasswordInput passwordInput = new PasswordInput(componentId, model);
        FormComponent input = passwordInput.getFormComponent();
        input.add(AttributeAppender.replace("placeholder", placeholder));

        return passwordInput;
    }
}
