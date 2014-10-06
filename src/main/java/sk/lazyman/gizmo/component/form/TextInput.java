package sk.lazyman.gizmo.component.form;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

public class TextInput<T> extends FormInput {

    public TextInput(String id, IModel<T> model) {
        this(id, model, String.class);
    }

    public TextInput(String id, IModel<T> model, Class clazz) {
        super(id, model);

        final TextField<T> text = new TextField<>(ID_INPUT, model);
        text.setType(clazz);
        add(text);
    }
}
