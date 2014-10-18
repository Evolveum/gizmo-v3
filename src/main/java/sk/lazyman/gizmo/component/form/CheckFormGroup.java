package sk.lazyman.gizmo.component.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameRemover;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * @author lazyman
 */
public class CheckFormGroup extends FormGroup<CheckInput, Boolean> {

    public CheckFormGroup(String id, IModel<Boolean> value, IModel<String> label, boolean required) {
        super(id, value, label, required);
    }

    @Override
    protected String getFormGroupClass() {
        return "checkbox";
    }

    @Override
    protected FormInput createInput(String componentId, IModel<Boolean> model, IModel<String> placeholder) {
        CheckInput formInput = new CheckInput(componentId, model) {

            @Override
            protected String getInputCssClass() {
                return null;
            }
        };
        formInput.getFormComponent().add(new CssClassNameRemover("form-group", "input-sm"));
        formInput.setRenderBodyOnly(true);

        return formInput;
    }

    protected Component createLabel(String labelId, IModel<String> labelModel) {
        Label label = new Label(labelId, labelModel);
        label.setRenderBodyOnly(true);

        return label;
    }
}
