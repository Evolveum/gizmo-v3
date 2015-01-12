/*
 * Copyright 2015 Viliam Repan (lazyman)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
