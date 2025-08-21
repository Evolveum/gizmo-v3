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

import com.evolveum.gizmo.component.SimplePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * @author lazyman
 */
public class FormGroup<F extends FormInput, T extends Serializable> extends SimplePanel<T> {

    private static final String ID_LABEL = "label";
    private static final String ID_INPUT_WRAPPER = "inputWrapper";
    private static final String ID_FEEDBACK = "feedback";

    private final IModel<String> labelModel;
    private final boolean required;

    public FormGroup(String id, IModel<T> value, IModel<String> label, boolean required) {
        super(id, value);
        this.labelModel = label;
        this.required = required;

        add(AttributeModifier.append("class", (IModel<String>) () -> {
            String hasError = "";
            if (getFormComponent().hasErrorMessage()) {
                hasError = " has-error";
            }

            return getFormGroupClass() + hasError;
        }));
    }

    @Override
    protected void initLayout() {
        Component label = createLabel(ID_LABEL, labelModel);
        add(label);

        FormInput inputWrapper = createInput(ID_INPUT_WRAPPER, getModel());
        add(inputWrapper);

        FormComponent input = inputWrapper.getFormComponent();
        input.add(AttributeModifier.replace("placeholder", labelModel));
        input.setRequired(required);
        input.setLabel(labelModel);


        FormGroupFeedback feedback = new FormGroupFeedback(ID_FEEDBACK, new ComponentFeedbackMessageFilter(input));
        feedback.setOutputMarkupId(true);
        add(feedback);
    }

    protected String getFormGroupClass() {
        return "form-group";
    }


    protected Component createLabel(String labelId, IModel<String> labelModel) {
        return new Label(labelId, labelModel);
    }

    protected FormInput createInput(String componentId, IModel<T> model) {
        return new TextInput(componentId, model);
    }

    public F getFormInput() {
        return (F) get(ID_INPUT_WRAPPER);
    }

    public FormComponent getFormComponent() {
        F f = getFormInput();
        return f.getFormComponent();
    }
}
