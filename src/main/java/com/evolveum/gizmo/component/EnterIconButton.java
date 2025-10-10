package com.evolveum.gizmo.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public abstract class EnterIconButton extends SimplePanel<String> {

    private static final String ID_BUTTON = "button";
    private static final String ID_ICON = "icon";
    private static final String ID_LABEL = "label";

    private final IModel<String> buttonClass;
    private final IModel<String> iconModel;

    public EnterIconButton(String id, IModel<String> label, IModel<String> iconModel, IModel<String> buttonClass) {
        super(id, label);
        this.buttonClass = buttonClass;
        this.iconModel = iconModel;
    }

    @Override
    protected void initLayout() {
        AjaxButton button = new AjaxButton(ID_BUTTON) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                submitPerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(getPage().get("feedback"));
            }
        };
        button.add(AttributeAppender.append("class", "btn " + buttonClass.getObject()));
        add(button);

        WebMarkupContainer icon = new WebMarkupContainer(ID_ICON);
        icon.add(AttributeAppender.append("class", iconModel));
        button.add(icon);

        Label label = new Label(ID_LABEL, getModel());
        label.setRenderBodyOnly(true);
        button.add(label);
    }

    protected abstract void submitPerformed(AjaxRequestTarget target);
}

