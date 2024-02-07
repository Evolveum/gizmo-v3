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

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;
import com.evolveum.gizmo.data.User;

import java.util.List;

public class MultiselectDropDownInput<T> extends ListMultipleChoice<T> {

    private static final String ID_INPUT = "input";

    private IModel<List<User>> choices;
    private boolean multiple = true;

    public MultiselectDropDownInput(String id, IModel<List<T>> model, IModel<List<T>> choices, IChoiceRenderer<T> renderer) {
        super(id, model, choices, renderer);
        setOutputMarkupId(true);
    }

    public MultiselectDropDownInput(String id, IModel<List<T>> model, boolean multiple, IModel<List<T>> choices, IChoiceRenderer<T> renderer) {
        super(id, model, choices, renderer);
        this.multiple = multiple;
        setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        StringBuilder componentSb = new StringBuilder("$('#").append(getMarkupId()).append("')");
        String componentSelector = componentSb.toString();

        StringBuilder sb = new StringBuilder();
        sb.append("if (typeof ");
        sb.append(componentSelector);
        sb.append(".select2 === \"function\") {\n");

        sb.append(componentSelector);
        sb.append(".select2({").append("multiple: " + multiple + ", width: 'element'").append("});");
        sb.append("}");
        response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
    }



}
