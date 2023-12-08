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

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * @author lazyman
 */
public class CheckFormGroup extends FormGroup<CheckInput, Boolean> {

    public CheckFormGroup(String id, IModel<Boolean> value, IModel<String> label, boolean required) {
        super(id, value, label, required);
    }

    @Override
    protected String getFormGroupClass() {
        return "checkbox";
    }

    @Override
    protected FormInput createInput(String componentId, IModel<Boolean> model, IModel<String> placeholder) {
        CheckInput formInput = new CheckInput(componentId, model) {

            @Override
            protected String getInputCssClass() {
                return null;
            }
        };
//        formInput.getFormComponent().add(new CssClassNameRemover("form-group", "input-sm"));
        formInput.setRenderBodyOnly(true);

        return formInput;
    }

    protected Component createLabel(String labelId, IModel<String> labelModel) {
        Label label = new Label(labelId, labelModel);
        label.setRenderBodyOnly(true);

        return label;
    }
}
