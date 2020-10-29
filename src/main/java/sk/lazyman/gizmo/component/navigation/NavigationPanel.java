package sk.lazyman.gizmo.component.navigation;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import sk.lazyman.gizmo.component.SimplePanel;
import sk.lazyman.gizmo.web.app.PageDashboard;

import java.util.ArrayList;
import java.util.List;

public class NavigationPanel extends SimplePanel<List<NavigationMenuItem>> {

    private static final String ID_HOME = "home";
    private static final String ID_MENU_ITEMS = "menuItems";
    private static final String ID_MENU_ITEM = "menuItem";

    public NavigationPanel(String id, IModel<List<NavigationMenuItem>> model) {
        super(id, model);
    }

    @Override
    public void initLayout() {

        AjaxLink<Void> home = new AjaxLink<>(ID_HOME) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(PageDashboard.class);
            }
        };
        add(home);

        ListView<NavigationMenuItem> listView = new ListView<NavigationMenuItem>(ID_MENU_ITEMS, getModel()) {

            @Override
            protected void populateItem(ListItem<NavigationMenuItem> item) {
                item.add(new NavigationLink(ID_MENU_ITEM, item.getModel()));
            }
        };
        add(listView);


    }


}
