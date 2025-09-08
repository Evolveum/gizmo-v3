package com.evolveum.gizmo.component.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;

import java.util.function.Consumer;

public  class OnChangeUpdateBehavior extends AjaxFormComponentUpdatingBehavior {
    private final Consumer<AjaxRequestTarget> onChange;

    public OnChangeUpdateBehavior(Consumer<AjaxRequestTarget> onChange) {
        super("change");
        this.onChange = onChange;
    }
    public static OnChangeUpdateBehavior of(Consumer<AjaxRequestTarget> action) {
        return new OnChangeUpdateBehavior(action);
    }
    @Override protected void onUpdate(AjaxRequestTarget target) {
        if (onChange != null) {
            onChange.accept(target);
        }
    }
}
