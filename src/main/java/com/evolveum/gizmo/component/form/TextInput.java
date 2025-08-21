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

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

public class TextInput<T> extends FormInput {

    public TextInput(String id, IModel<T> model) {
        this(id, model, String.class);
    }

    private Class<?> clazz;

    public TextInput(String id, IModel<T> model, Class clazz) {
        super(id, model);
        this.clazz = clazz;
    }

    @Override
    protected void initLayout() {
        final TextField<T> text = new TextField<>(ID_INPUT, getModel());
        text.setType(clazz);
        add(text);
    }
}
