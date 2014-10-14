package sk.lazyman.gizmo.component;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonList;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * @author lazyman
 */
public abstract class SubMenu extends LabeledLink {

    public SubMenu(String id, IModel<String> model) {
        super(id, model);
    }

//    /**
//     * Construct.
//     */
//    public MenuDivider() {
//        super(ButtonList.getButtonMarkupId());
//
//        setBody(Model.of("&nbsp;"));
//        setEscapeModelStrings(false);
//    }
//
//    @Override
//    protected void onInitialize() {
//        super.onInitialize();
//
//        // add the divider if the parent is not a ListView
//        // or a ListView that reuses its items
//        ListView listView = findParent(ListView.class);
//        if (listView != null) {
//            if (listView.getReuseItems()) {
//                getParent().add(new CssClassNameAppender("divider"));
//            }
//        } else {
//            getParent().add(new CssClassNameAppender("divider"));
//        }
//    }
//
//    @Override
//    protected void onConfigure() {
//        super.onConfigure();
//
//        // re-add the divider if the parent is a ListView
//        // that doesn't reuse its items
//        ListView listView = findParent(ListView.class);
//        if (listView != null && !listView.getReuseItems()) {
//            getParent().add(new CssClassNameAppender("divider"));
//        }
//    }
}
