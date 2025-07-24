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
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;

public class EditablePropertyColumn<T extends Editable> extends PropertyColumn<T, String> {

    public EditablePropertyColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    public EditablePropertyColumn(IModel<String> displayModel, String sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId,
                             final IModel<T> rowModel) {



        Component component = createInputPanel(componentId, rowModel);
//        component.add(new VisibleEnableBehaviour() {
//            @Override
//            public boolean isVisible() {
//                return isEditing(rowModel);
//            }
//        });
        cellItem.add(component);

//        Label label = new Label(componentId, getDataModel(rowModel));
//        cellItem.add(label);
//        label.add(new VisibleEnableBehaviour() {
//            @Override
//            public boolean isVisible() {
//                return !isEditing(rowModel);
//            }
//        });

//        if (!isEditing(rowModel)) {
//            super.populateItem(cellItem, componentId, rowModel);
//        } else {
//            cellItem.add(createInputPanel(componentId, rowModel));
//        }
    }

//    protected boolean isEditing(IModel<T> rowModel) {
//        Editable editable = rowModel.getObject();
//        return editable.isEditable();
//    }

    protected TextPanel<T> createInputPanel(String componentId, IModel<T> model) {
        return new TextPanel<>(componentId, model, getPropertyExpression());
    }
}