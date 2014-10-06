package sk.lazyman.gizmo.component.form;

import org.apache.wicket.model.IModel;

import java.util.Date;

/**
 * @author lazyman
 */
public class DateFormGroup extends FormGroup<DateInput, Date> {

    public DateFormGroup(String id, IModel<Date> value, IModel<String> label, String labelSize, String inputSize,
                         String feedbackSize, boolean required) {
        super(id, value, label, labelSize, inputSize, feedbackSize, required);
    }

    @Override
    protected FormInput createInput(String componentId, IModel<Date> model, IModel<String> placeholder) {
        FormInput formInput = new DateInput(componentId, model);
        return formInput;
    }
}
