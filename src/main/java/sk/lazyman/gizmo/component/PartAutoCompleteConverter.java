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

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import sk.lazyman.gizmo.dto.CustomerProjectPartDto;

import java.util.List;
import java.util.Locale;

/**
 * @author lazyman
 */
public class PartAutoCompleteConverter extends AbstractAutoCompleteTextRenderer<CustomerProjectPartDto>
        implements IConverter<CustomerProjectPartDto> {

    private IModel<List<CustomerProjectPartDto>> projects;

    public PartAutoCompleteConverter(IModel<List<CustomerProjectPartDto>> projects) {
        this.projects = projects;
    }

    @Override
    protected String getTextValue(CustomerProjectPartDto object) {
        return convertToString(object);
    }

    @Override
    public CustomerProjectPartDto convertToObject(String value, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        }

        List<CustomerProjectPartDto> list = projects.getObject();
        for (CustomerProjectPartDto dto : list) {
            if (value.equals(convertToString(dto, locale))) {
                return dto;
            }
        }

        throw new ConversionException("DashboardProjectConverter.cantConvert").setSourceValue(value)
                .setTargetType(CustomerProjectPartDto.class)
                .setConverter(this)
                .setLocale(locale);
    }

    @Override
    public String convertToString(CustomerProjectPartDto value, Locale locale) {
        return convertToString(value);
    }

    public static String convertToString(CustomerProjectPartDto object) {
        if (object == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(object.getCustomerName())) {
            sb.append(object.getCustomerName());
        }

        if (StringUtils.isNotEmpty(object.getCustomerName())
                && StringUtils.isNotEmpty(object.getProjectName())) {
            sb.append(" / ");
        }

        if (StringUtils.isNotEmpty(object.getProjectName())) {
            sb.append(object.getProjectName());
        }

        if (StringUtils.isNotEmpty(object.getProjectName())
                && StringUtils.isNotEmpty(object.getPartName())) {
            sb.append(" / ");
        }

        if (StringUtils.isNotEmpty(object.getPartName())) {
            sb.append(object.getPartName());
        }

        return sb.toString();
    }
}
