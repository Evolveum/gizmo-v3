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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import sk.lazyman.gizmo.dto.ProjectListItem;

import java.io.Serializable;
import java.util.List;

/**
 * @author lazyman
 */
public class ProjectItemList<T extends Serializable> extends SimplePanel<List<ProjectListItem<T>>> {

    private static final String ID_REPEATER = "repeater";
    private static final String ID_LINK = "link";
    private static final String ID_EDIT = "edit";
    private static final String ID_NAME = "name";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_PROPERTIES = "properties";

    public ProjectItemList(String id, IModel<List<ProjectListItem<T>>> model) {
        super(id, model);

        add(AttributeModifier.replace("class", "list-group"));
        setOutputMarkupId(true);
    }

    @Override
    protected void initLayout() {
        ListView<ProjectListItem<T>> repeater = new ListView<ProjectListItem<T>>(ID_REPEATER, getModel()) {

            @Override
            protected void populateItem(ListItem<ProjectListItem<T>> item) {
                createItemBody(item);
            }
        };
        add(repeater);
    }

    private void createItemBody(final ListItem<ProjectListItem<T>> item) {
        item.add(AttributeAppender.append("class", new IModel<String>() {

            @Override
            public String getObject() {
                ProjectListItem i = item.getModelObject();
                return i.isSelected() ? "list-group-item-info" : null;
            }
        }));

        AjaxLink<Void> link = new AjaxLink<>(ID_LINK) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                itemSelected(target, item.getModelObject());
            }
        };
        item.add(link);

        Label name = new Label(ID_NAME,
                new PropertyModel<>(item.getModel(), ProjectListItem.F_NAME));
        name.setRenderBodyOnly(true);
        link.add(name);

        Label description = new Label(ID_DESCRIPTION,
                new PropertyModel<>(item.getModel(), ProjectListItem.F_DESCRIPTION));
        description.setRenderBodyOnly(true);
        link.add(description);

        AjaxLink<Void> edit = new AjaxLink<>(ID_EDIT) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                editPerformed(target, item.getModelObject());
            }
        };
        item.add(edit);

        item.add(new Label(ID_PROPERTIES, createPropertiesModel()));
    }

    protected IModel<String> createPropertiesModel() {
        return new Model<>();
    }

    protected void itemSelected(AjaxRequestTarget target, ProjectListItem<T> selected) {
        target.add(this);

        List<ProjectListItem<T>> items = getModelObject();
        for (ProjectListItem item : items) {
            item.setSelected(false);
        }

        selected.setSelected(true);
    }

    protected void editPerformed(AjaxRequestTarget target, ProjectListItem<T> selected) {
    }
}
