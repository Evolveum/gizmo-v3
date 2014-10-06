package sk.lazyman.gizmo.component.form;

import org.apache.wicket.model.IModel;
import sk.lazyman.gizmo.component.CheckBox;

/**
 * @author lazyman
 */
public class CheckInput extends FormInput<Boolean> {

    public CheckInput(String id, IModel<Boolean> model) {
        super(id, model);

        add(new CheckBox(ID_INPUT, model));
    }
}
