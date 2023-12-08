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

package com.evolveum.gizmo.component.data;

import com.evolveum.gizmo.data.provider.BasicDataProvider;
import org.apache.commons.lang3.Validate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import com.evolveum.gizmo.component.VisibleEnableBehaviour;

import java.util.List;

/**
 * @author lazyman
 */
public class TablePanel<T> extends Panel {

    private static final String ID_TABLE = "table";
    private static final String ID_PAGING = "paging";

    private IModel<Boolean> showPaging = new Model<Boolean>(true);
    private IModel<Boolean> showCount = new Model<Boolean>(true);

    public TablePanel(String id, ISortableDataProvider provider, List<IColumn<T, String>> columns, int rowsPerPage) {
        super(id);
        Validate.notNull(provider, "Provider must not be null.");
        Validate.notNull(columns, "Columns must not be null.");

        add(AttributeModifier.prepend("style", "display: table; width: 100%;"));

        initLayout(columns, provider, rowsPerPage);
    }

    private void initLayout(List<IColumn<T, String>> columns, ISortableDataProvider provider, int rowsPerPage) {
        DataTable<T, String> table = new DataTable<>(ID_TABLE, columns, provider, rowsPerPage);

        table.setOutputMarkupId(true);

        TableHeadersToolbar headers = new TableHeadersToolbar(table, provider);
        headers.setOutputMarkupId(true);
        WebMarkupContainer toolbars = table.getTopToolbars();
        toolbars.add(AttributeAppender.append("class", "thead-dark"));
        table.addTopToolbar(headers);

//        CountToolbar count = new CountToolbar(table);
//        addVisibleBehaviour(count, showCount);
//        table.addBottomToolbar(count);


//        NavigationToolbar toolbar = new NavigationToolbar(table);
        table.addBottomToolbar(new NavigationToolbar(table));
        table.addTopToolbar(new NavigationToolbar(table));

//        NavigatorToolbar navigation = new NavigatorToolbar(table, true);
////        addVisibleBehaviour(count, showCount);
//        table.addBottomToolbar(navigation);

        add(table);

//        NavigatorPanel nb2 = new NavigatorPanel(ID_PAGING, table, true);
//        addVisibleBehaviour(nb2, showPaging);
//        add(nb2);

        setItemsPerPage(rowsPerPage);
    }

    private void addVisibleBehaviour(Component comp, final IModel<Boolean> model) {
        comp.add(new VisibleEnableBehaviour() {

            @Override
            public boolean isVisible() {
                return model.getObject();
            }
        });
    }

    public DataTable getDataTable() {
        return (DataTable) get(ID_TABLE);
    }

    public NavigatorPanel getNavigatorPanel() {
        return (NavigatorPanel) get(ID_PAGING);
    }

    public void setItemsPerPage(int size) {
        IDataProvider provider = getDataTable().getDataProvider();
        if (provider instanceof BasicDataProvider) {
            ((BasicDataProvider) provider).setItemsPerPage(size);
        }

        getDataTable().setItemsPerPage(size);
    }

    public void setCurrentPage(Long page) {
        if (page == null) {
            getDataTable().setCurrentPage(0);
            return;
        }

        getDataTable().setCurrentPage(page);
    }

    public void setShowPaging(boolean showPaging) {
        this.showPaging.setObject(showPaging);
        this.showCount.setObject(showPaging);

        if (!showPaging) {
            setItemsPerPage(Integer.MAX_VALUE);
        } else {
            setItemsPerPage(10);
        }
    }

    public void setShowCount(boolean showCount) {
        this.showCount.setObject(showCount);
    }
}
