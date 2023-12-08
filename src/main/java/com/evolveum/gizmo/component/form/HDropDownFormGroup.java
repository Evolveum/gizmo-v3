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

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.List;

/**
 * @author lazyman
 */
public class HDropDownFormGroup<T extends Serializable> extends HFormGroup<DropDownInput<T>, T> {

    public HDropDownFormGroup(String id, IModel<T> value, IModel<String> label, String labelSize, String inputSize,
                              String feedbackSize, boolean required) {
        super(id, value, label, labelSize, inputSize, feedbackSize, required);
    }

    protected FormInput createInput(String componentId, IModel<T> model, IModel<String> placeholder) {
        return new DropDownInput<>(componentId, model);
    }

    public void setRenderer(IChoiceRenderer<T> renderer) {
        getFormInput().setChoiceRenderer(renderer);
    }

    public void setChoices(IModel<? extends List<T>> choices) {
        getFormInput().setChoices(choices);
    }

    public void setNullValid(boolean nullValid) {
        getFormInput().setNullValid(nullValid);
    }

    public void setDefaultChoice(String defaultChoice) {
        getFormInput().setDefaultChoice(defaultChoice);
    }
}
