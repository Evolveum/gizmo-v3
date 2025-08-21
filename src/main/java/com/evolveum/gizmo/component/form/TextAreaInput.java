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

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;

public class TextAreaInput<T> extends FormInput<T> {

    private static final String ID_INPUT = "input";
    private int rows = 2;

    public TextAreaInput(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    protected void initLayout() {
        TextArea<T> text = new TextArea<T>(ID_INPUT, getModel());
        text.add(AttributeAppender.replace("rows", new IModel<String>() {

            @Override
            public String getObject() {
                return Integer.toString(rows);
            }
        }));
        add(text);
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    @Override
    public FormComponent getFormComponent() {
        return (FormComponent) get(ID_INPUT);
    }
}
