package com.evolveum.gizmo.component;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;

import java.util.List;

public class GizmoTabbedPanel<T extends ITab> extends TabbedPanel<T> {


    public GizmoTabbedPanel(String id, List<T> tabs) {
        super(id, tabs);
    }

    @Override
    protected String getSelectedTabCssClass() {
        return " active";
    }
}
