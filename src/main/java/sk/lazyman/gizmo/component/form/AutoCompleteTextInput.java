package sk.lazyman.gizmo.component.form;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompleteRenderer;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author lazyman
 */
public class AutoCompleteTextInput<T> extends FormInput<T> {

    public AutoCompleteTextInput(String id, IModel<T> model) {
        super(id, model);

        AutoCompleteTextField input = new AutoCompleteTextField(ID_INPUT, model) {

            @Override
            protected Iterator<T> getChoices(String input) {
                return AutoCompleteTextInput.this.getChoices(input);
            }

            @Override
            protected AutoCompleteBehavior newAutoCompleteBehavior(IAutoCompleteRenderer renderer, AutoCompleteSettings settings) {
                AutoCompleteBehavior behavior = AutoCompleteTextInput.this.newAutoCompleteBehavior(renderer, settings);
                if (behavior != null) {
                    return behavior;
                }

                return super.newAutoCompleteBehavior(renderer, settings);
            }
        };
        add(input);
    }

    protected Iterator<T> getChoices(String input) {
        return new ArrayList<T>().iterator();
    }

    protected AutoCompleteBehavior newAutoCompleteBehavior(IAutoCompleteRenderer renderer,
                                                           AutoCompleteSettings settings) {
        return null;
    }
}
