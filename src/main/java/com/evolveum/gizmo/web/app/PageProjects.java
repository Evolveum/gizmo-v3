/*
 *  Copyright (C) 2023 Evolveum
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.evolveum.gizmo.web.app;

import com.evolveum.gizmo.data.provider.BasicDataProvider;
import com.querydsl.core.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.data.domain.Sort;
import org.wicketstuff.annotation.mount.MountPath;
import com.evolveum.gizmo.component.AjaxButton;
import com.evolveum.gizmo.component.data.LinkColumn;
import com.evolveum.gizmo.component.data.TablePanel;
import com.evolveum.gizmo.component.form.IconButton;
import com.evolveum.gizmo.data.Customer;
import com.evolveum.gizmo.data.Project;
import com.evolveum.gizmo.data.QProject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
@MountPath("/app/projects")
public class PageProjects extends PageAppProjects {

    private static final String ID_TABLE = "table";
    private static final String ID_FORM = "form";
    private static final String ID_SEARCH_TEXT = "searchText";
    private static final String ID_SEARCH = "search";
    private static final String ID_NEW_PROJECT = "newProject";

    private IModel<String> searchModel = new Model<>();

    public PageProjects() {
        initLayout();
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        TextField searchText = new TextField(ID_SEARCH_TEXT, searchModel);
        searchText.setOutputMarkupId(true);
        form.add(searchText);

        initButtons(form);

        BasicDataProvider provider = new BasicDataProvider(getProjectRepository()) {

            @Override
            public Predicate getPredicate() {
                String text = searchModel.getObject();
                if (StringUtils.isNotEmpty(text)) {
                    return QProject.project.name.lower().contains(text.toLowerCase())
                            .or(QProject.project.description.lower().contains(text.toLowerCase()));
                }

                return null;
            }
        };
        provider.setSort(Sort.by(Sort.Order.asc(Project.F_NAME),
                Sort.Order.desc(Project.F_COMMERCIAL)));

        List<IColumn> columns = new ArrayList<>();

        columns.add(new LinkColumn<Project>(createStringResource("Project.name"), Project.F_NAME) {

            @Override
            public void onClick(AjaxRequestTarget target, IModel<Project> rowModel) {
                projectDetailsPerformed(target, rowModel.getObject());
            }
        });
        columns.add(new PropertyColumn(createStringResource("Project.customer"), Project.F_CUSTOMER + "." + Customer.F_NAME));
        columns.add(new PropertyColumn(createStringResource("Project.description"), Project.F_DESCRIPTION));
        columns.add(new PropertyColumn(createStringResource("Project.commercial"), Project.F_COMMERCIAL));
        columns.add(new PropertyColumn(createStringResource("Project.closed"), Project.F_CLOSED));

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 20);
        table.setOutputMarkupId(true);
        add(table);
    }

    @Override
    public Fragment createHeaderButtonsFragment(String fragmentId) {
        Fragment fragment = new  Fragment(fragmentId, "buttonsFragment", this);

        AjaxLink<String> newProject = new AjaxLink<>(ID_NEW_PROJECT, createStringResource("PageProjects.newProject")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                newProjectPerformed(target);
            }
        };
        newProject.setOutputMarkupId(true);
        fragment.add(newProject);

        return fragment;
    }


    private void initButtons(Form form) {
        IconButton search = new IconButton(ID_SEARCH,
                createStringResource("PageProjects.search"),
                createStringResource("fa-search"),
                createStringResource("btn-primary")) {

            @Override
            protected void submitPerformed(AjaxRequestTarget target) {
                searchPerformed(target);
            }
        };
        search.setRenderBodyOnly(true);
        form.add(search);
    }

    private void newProjectPerformed(AjaxRequestTarget target) {
        setResponsePage(PageProject.class);
    }

    private void searchPerformed(AjaxRequestTarget target) {
        target.add(get(ID_TABLE));
    }

    private void projectDetailsPerformed(AjaxRequestTarget target, Project customer) {
        PageParameters params = new PageParameters();
        params.set(PageProject.PROJECT_ID, customer.getId());

        setResponsePage(PageProject.class, params);
    }
}
