package sk.lazyman.gizmo.component.form;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

public class TextAreaInput<T> extends FormInput<T> {

    private static final String ID_INPUT = "input";
    private int rows = 2;

    public TextAreaInput(String id, IModel<T> model) {
        super(id, model);

        TextArea<T> text = new TextArea<T>(ID_INPUT, model);
        text.add(AttributeAppender.replace("rows", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return Integer.toString(rows);
            }
        }));
        add(text);
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    @Override
    public FormComponent getFormComponent() {
        return (FormComponent) get(ID_INPUT);
    }
}
