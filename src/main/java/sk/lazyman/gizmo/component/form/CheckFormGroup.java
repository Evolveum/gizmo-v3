package sk.lazyman.gizmo.component.form;

import org.apache.wicket.model.IModel;

/**
 * @author lazyman
 */
public class CheckFormGroup extends FormGroup<CheckInput, Boolean> {

    public CheckFormGroup(String id, IModel<Boolean> value, IModel<String> label, String labelSize, String inputSize,
                          String feedbackSize, boolean required) {
        super(id, value, label, labelSize, inputSize, feedbackSize, required);
    }

    @Override
    protected FormInput createInput(String componentId, IModel<Boolean> model, IModel<String> placeholder) {
        CheckInput formInput = new CheckInput(componentId, model);
        return formInput;
    }
}
