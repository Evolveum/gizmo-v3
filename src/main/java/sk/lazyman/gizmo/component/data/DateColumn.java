package sk.lazyman.gizmo.component.data;

import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.AbstractReadOnlyModel;
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
        return new AbstractReadOnlyModel<Object>() {

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
