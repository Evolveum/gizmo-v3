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

package sk.lazyman.gizmo.component.form;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

/**
 * @author lazyman
 */
public class FormUtils {

    public static WebMarkupContainer createFormGroup(String id, final FormComponent formComponent) {
        WebMarkupContainer formGroup = new WebMarkupContainer(id);
        formGroup.add(formComponent);
        formGroup.add(AttributeModifier.append("class", new IModel<String>() {

            @Override
            public String getObject() {
                if (formComponent.hasErrorMessage()) {
                    return "has-error";
                }
                return null;
            }
        }));

        return formGroup;
    }

    public static void addPlaceholderAndLabel(FormComponent comp, IModel<String> model) {
        comp.add(AttributeModifier.replace("placeholder", model));
        comp.setLabel(model);
    }
}
