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

package sk.lazyman.gizmo.component.data;

import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lazyman
 */
public class DateColumn<T, S> extends PropertyColumn<T, S> {

    private String dateFormat;

    public DateColumn(IModel<String> displayModel, String propertyExpression, String dateFormat) {
        super(displayModel, propertyExpression);
        this.dateFormat = dateFormat;
    }

    @Override
    public IModel<Object> getDataModel(final IModel<T> rowModel) {
        return new IModel<Object>() {

            @Override
            public Object getObject() {
                PropertyModel<Date> propertyModel = new PropertyModel<>(rowModel, getPropertyExpression());
                Date date = propertyModel.getObject();
                if (date == null) {
                    return null;
                }

                DateFormat df = new SimpleDateFormat(dateFormat);
                return df.format(date);
            }
        };
    }
}
