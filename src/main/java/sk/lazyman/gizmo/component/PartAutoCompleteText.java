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

package sk.lazyman.gizmo.component;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import sk.lazyman.gizmo.dto.CustomerProjectPartDto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author lazyman
 */
public class PartAutoCompleteText extends AutoCompleteTextField<CustomerProjectPartDto> {

    private IModel<List<CustomerProjectPartDto>> allChoices;

    public PartAutoCompleteText(String id, IModel<CustomerProjectPartDto> model, IModel<List<CustomerProjectPartDto>> allChoices) {
        super(id, model, CustomerProjectPartDto.class, new PartAutoCompleteConverter(allChoices), new AutoCompleteSettings());

        this.allChoices = allChoices;
    }

    @Override
    protected Iterator<CustomerProjectPartDto> getChoices(String input) {
        List<CustomerProjectPartDto> list = allChoices.getObject();
        List<CustomerProjectPartDto> result = new ArrayList<>();
        for (CustomerProjectPartDto dto : list) {
            if (dto.match(input)) {
                result.add(dto);
            }
        }

        return result.iterator();
    }

    @Override
    public <C> IConverter<C> getConverter(Class<C> type) {
        return (IConverter<C>) new PartAutoCompleteConverter(allChoices);
    }
}
