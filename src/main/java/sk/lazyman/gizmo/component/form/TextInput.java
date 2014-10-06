package sk.lazyman.gizmo.component.form;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

public class TextInput<T> extends FormInput {

    private static final String ID_INPUT = "input";

    public TextInput(String id, IModel<T> model) {
        this(id, model, String.class);
    }

    public TextInput(String id, IModel<T> model, Class clazz) {
        super(id, model);

        final TextField<T> text = new TextField<>(ID_INPUT, model);
        text.setType(clazz);
        add(text);
    }

    @Override
    public FormComponent getFormComponent() {
        return (FormComponent) get(ID_INPUT);
    }
}
