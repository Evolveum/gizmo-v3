package com.evolveum.gizmo.web.component;

import java.time.LocalTime;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

public class TimeField extends TextField<LocalTime> {

    public TimeField(String id, IModel<LocalTime> model) {
        super(id, model, LocalTime.class);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        tag.put("type", "time");
        tag.put("step", "300");
    }
}

