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

package com.evolveum.gizmo.component.data;

import com.evolveum.gizmo.component.VisibleEnableBehaviour;
import com.evolveum.gizmo.data.Editable;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class TextPanel<T extends Editable> extends Panel {

    private static final String ID_TEXT = "text";

    private final String propertyExpression;

    public TextPanel(String id, IModel<T> model, String propertyExpression) {
        super(id, model);
        this.propertyExpression = propertyExpression;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        initLayout();
    }

    private void initLayout() {
        TextField textField = new TextField<>(ID_TEXT, new PropertyModel<>(getModel(), propertyExpression));
        textField.add(new VisibleEnableBehaviour() {
            @Override
            public boolean isEnabled(Component component) {
                return isEditing(getModel());
            }
        });
        textField.add(AttributeAppender.replace("class", () -> isEditing(getModel()) ? "form-control" : "form-control-plaintext"));
        add(textField);
    }

    protected boolean isEditing(IModel<Editable> rowModel) {
        Editable editable = rowModel.getObject();
        return editable.isEditable();
    }

    private IModel<Editable> getModel() {
        return (IModel<Editable>) getDefaultModel();
    }
}
