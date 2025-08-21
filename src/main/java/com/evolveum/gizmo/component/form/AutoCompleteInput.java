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

import com.evolveum.gizmo.component.PartAutoCompleteText;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * @author lazyman
 */
public class AutoCompleteInput extends FormInput<CustomerProjectPartDto> {

    private final IModel<List<CustomerProjectPartDto>> choices;

    public AutoCompleteInput(String id, IModel<CustomerProjectPartDto> model, IModel<List<CustomerProjectPartDto>> choices) {
        super(id, model);
        this.choices = choices;
    }

    @Override
    protected void initLayout() {
        add(new PartAutoCompleteText(ID_INPUT, getModel(), choices));
    }
}
