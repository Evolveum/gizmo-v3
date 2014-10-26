package sk.lazyman.gizmo.component.form;

import org.apache.wicket.model.IModel;
import sk.lazyman.gizmo.component.PartAutoCompleteText;
import sk.lazyman.gizmo.dto.CustomerProjectPartDto;

import java.util.List;

/**
 * @author lazyman
 */
public class AutoCompleteInput extends FormInput<CustomerProjectPartDto> {

    public AutoCompleteInput(String id, IModel<CustomerProjectPartDto> model, IModel<List<CustomerProjectPartDto>> choices) {
        super(id, model);

        add(new PartAutoCompleteText(ID_INPUT, model, choices));
    }
}
