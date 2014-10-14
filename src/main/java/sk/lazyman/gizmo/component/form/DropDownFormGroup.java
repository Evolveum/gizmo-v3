package sk.lazyman.gizmo.component.form;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.List;

/**
 * @author lazyman
 */
public class DropDownFormGroup<T extends Serializable> extends FormGroup<DropDownInput<T>, T> {

    public DropDownFormGroup(String id, IModel<T> value, IModel<String> label, boolean required) {
        super(id, value, label, required);
    }

    protected FormInput createInput(String componentId, IModel<T> model, IModel<String> placeholder) {
        return new DropDownInput<>(componentId, model);
    }

    public void setRenderer(IChoiceRenderer<T> renderer) {
        getFormInput().setChoiceRenderer(renderer);
    }

    public void setChoices(IModel<? extends List<T>> choices) {
        getFormInput().setChoices(choices);
    }

    public void setNullValid(boolean nullValid) {
        getFormInput().setNullValid(nullValid);
    }

    public void setDefaultChoice(String defaultChoice) {
        getFormInput().setDefaultChoice(defaultChoice);
    }
}
