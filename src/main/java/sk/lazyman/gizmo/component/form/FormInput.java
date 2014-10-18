package sk.lazyman.gizmo.component.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameModifier;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import sk.lazyman.gizmo.component.SimplePanel;

/**
 * @author lazyman
 */
public abstract class FormInput<T> extends SimplePanel<T> {

    protected static final String ID_INPUT = "input";

    public FormInput(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        FormComponent c = getFormComponent();
        if (StringUtils.isNotEmpty(getInputCssClass())) {
            c.add(new CssClassNameModifier(getInputCssClass()));
        }
    }

    public FormComponent getFormComponent() {
        return (FormComponent) get(ID_INPUT);
    }

    protected String getInputCssClass() {
        return "form-control input-sm";
    }
}
