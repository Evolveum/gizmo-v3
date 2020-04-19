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

import com.querydsl.core.types.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.Sort;
import sk.lazyman.gizmo.component.data.LinkColumn;
import sk.lazyman.gizmo.component.data.TablePanel;
import sk.lazyman.gizmo.data.Part;
import sk.lazyman.gizmo.data.Project;
import sk.lazyman.gizmo.data.QPart;
import sk.lazyman.gizmo.data.provider.BasicDataProvider;
import sk.lazyman.gizmo.data.provider.CustomTabDataProvider;
import sk.lazyman.gizmo.web.app.PageAppTemplate;
import sk.lazyman.gizmo.web.app.PageProject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
public class ProjectPartsTab extends SimplePanel {

    private static final String ID_TABLE = "table";
    private static final String ID_NEW_PART = "newPart";

    private boolean initialized;

    public ProjectPartsTab(String id) {
        super(id);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        if (!initialized) {
            initPanelLayout();
            initialized = true;
        }
    }

    private void initPanelLayout() {
        AjaxButton newPart = new AjaxButton(ID_NEW_PART, createStringResource("ProjectPartsTab.newPart")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                newPartPerformed(target);
            }
        };
        newPart.add(new VisibleEnableBehaviour() {

            @Override
            public boolean isVisible() {
                PageAppTemplate page = (PageAppTemplate) getPage();
                Integer customerId = page.getIntegerParam(PageProject.PROJECT_ID);
                return customerId != null;
            }
        });
        add(newPart);

        final PageProject page = (PageProject) getPage();
        BasicDataProvider provider = new CustomTabDataProvider(page.getProjectPartRepository()) {

            @Override
            public Predicate getPredicate() {
                Integer projectId = page.getIntegerParam(PageProject.PROJECT_ID);
                if (projectId == null) {
                    return null;
                }

                return QPart.part.project.id.eq(projectId);
            }
        };
        provider.setSort(Sort.by(Sort.Order.asc(Project.F_NAME)));

        List<IColumn> columns = new ArrayList<>();
        columns.add(new LinkColumn<Part>(createStringResource("Part.name"), Part.F_NAME) {

            @Override
            public void onClick(AjaxRequestTarget target, IModel<Part> rowModel) {
                editPartPerformed(target, rowModel.getObject());
            }
        });
        columns.add(new PropertyColumn(createStringResource("Part.description"), Part.F_DESCRIPTION));

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 15);
        add(table);
    }

    protected void newPartPerformed(AjaxRequestTarget target) {

    }

    protected void editPartPerformed(AjaxRequestTarget target, Part part) {

    }
}
