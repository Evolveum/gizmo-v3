package sk.lazyman.gizmo.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * @author lazyman
 */
public class LinkPanel extends Panel {

    private static final String ID_LINK = "link";
    private static final String ID_LABEL = "label";

    public LinkPanel(String id, IModel<String> label) {
        super(id);

        AjaxLink link = new AjaxLink(ID_LINK) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                LinkPanel.this.onClick(target);
            }
        };
        link.add(new Label(ID_LABEL, label));
        link.add(new VisibleEnableBehaviour() {

            @Override
            public boolean isEnabled() {
                return LinkPanel.this.isEnabled();
            }
        });
        add(link);
    }

    public boolean isEnabled() {
        return true;
    }

    public void onClick(AjaxRequestTarget target) {
    }
}
