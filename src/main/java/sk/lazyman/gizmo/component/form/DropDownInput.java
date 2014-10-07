package sk.lazyman.gizmo.component.form;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
public class DropDownInput<T> extends FormInput<T> {

    private static final String ID_INPUT = "input";

    private String defaultChoice = "DropDownInput.notDefined";

    public DropDownInput(String id, IModel<T> model) {
        super(id, model);

        DropDownChoice input = new DropDownChoice(ID_INPUT, new Model<>(new ArrayList<T>())) {

            @Override
            protected CharSequence getDefaultChoice(String selectedValue) {
                return getString(defaultChoice);
            }
        };
        input.setModel(model);
        add(input);
    }

    public void setChoiceRenderer(IChoiceRenderer<T> renderer) {
        DropDownChoice choice = (DropDownChoice) getFormComponent();
        choice.setChoiceRenderer(renderer);
    }

    public void setChoices(IModel<? extends List<T>> choices) {
        DropDownChoice choice = (DropDownChoice) getFormComponent();
        choice.setChoices(choices);
    }

    public void setNullValid(boolean nullValid) {
        DropDownChoice choice = (DropDownChoice) getFormComponent();
        choice.setNullValid(nullValid);
    }

    public void setDefaultChoice(String defaultChoice) {
        this.defaultChoice = defaultChoice;
    }
}
