package sk.lazyman.gizmo.component;

import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.model.IModel;

import java.util.Date;

/**
 * @author lazyman
 */
public class DateFormComponent extends DateTimeField {

    public DateFormComponent(String id, IModel<Date> model) {
        super(id, model);
    }
}
