package sk.lazyman.gizmo.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * @author lazyman
 */
public class FormUtils {

    public static WebMarkupContainer createFormGroup(String id, final FormComponent formComponent) {
        WebMarkupContainer formGroup = new WebMarkupContainer(id);
        formGroup.add(formComponent);
        formGroup.add(AttributeModifier.append("class", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if (formComponent.hasErrorMessage()) {
                    return "has-error";
                }
                return null;
            }
        }));

        return formGroup;
    }

    public static void addPlaceholderAndLabel(FormComponent comp, IModel<String> model) {
        comp.add(AttributeModifier.replace("placeholder", model));
        comp.setLabel(model);
    }
}
