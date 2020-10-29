package sk.lazyman.gizmo.component.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import sk.lazyman.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.SimplePanel;

public class IconButton extends SimplePanel<String> {

    private static final String ID_BUTTON = "button";
    private static final String ID_ICON = "icon";

    public IconButton(String id, IModel<String> label) {
        super(id, label);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        AjaxSubmitLink button = new AjaxSubmitLink(ID_BUTTON) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                submitPerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(getPageTemplate().getFeedbackPanel());
            }
        };
        add(button);
        WebMarkupContainer icon = new WebMarkupContainer(ID_ICON);
        icon.add(AttributeAppender.append("class", getModel()));
        button.add(icon);

    }

    protected void submitPerformed(AjaxRequestTarget target) {

    }
    //<button type="button" class="btn btn-default"><i class="fas fa-align-left"></i></button>
}
