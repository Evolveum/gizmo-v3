package com.evolveum.gizmo.component.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;

import java.util.function.Consumer;

public  class OnChangeUpdateBehavior extends AjaxFormComponentUpdatingBehavior {
    public OnChangeUpdateBehavior() {
        super("change");
    }
    @Override protected void onUpdate(AjaxRequestTarget target) {
    }
}
