package sk.lazyman.gizmo.component.navigation;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;

public class NavigationLink extends AjaxLink<NavigationMenuItem> {

    private WebPage webPage;
    private boolean active;

    public NavigationLink(String id, IModel<NavigationMenuItem> model) {
        super(id, model);

    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        setResponsePage(getModelObject().getPage());
    }

    @Override
    public IModel<?> getBody() {
        return getModelObject().getName();
    }
}
