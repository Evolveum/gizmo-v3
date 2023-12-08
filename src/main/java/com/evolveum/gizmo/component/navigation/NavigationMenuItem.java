package com.evolveum.gizmo.component.navigation;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public class NavigationMenuItem implements Serializable {

    private IModel<String> name;
    private Class<? extends WebPage> page;

    public NavigationMenuItem(IModel<String> name, Class<? extends WebPage> page) {
        this.name = name;
        this.page = page;
    }

    public IModel<String> getName() {
        return name;
    }

    public void setName(IModel<String> name) {
        this.name = name;
    }

    public Class<? extends WebPage> getPage() {
        return page;
    }

    public void setPage(Class<? extends WebPage> page) {
        this.page = page;
    }
}
