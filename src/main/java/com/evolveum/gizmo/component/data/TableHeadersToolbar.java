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

package com.evolveum.gizmo.component.data;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * @author lazyman
 */
public class TableHeadersToolbar<T> extends HeadersToolbar {

    public TableHeadersToolbar(DataTable<T, String> table, ISortStateLocator stateLocator) {
        super(table, stateLocator);
        add(AttributeAppender.append("class", "thead-dark"));
    }

    @Override
    protected WebMarkupContainer newSortableHeader(String headerId, Object property, ISortStateLocator locator) {
        return new BasicOrderByBorder(headerId, property, locator) {

            @Override
            protected void onSortChanged() {
                getTable().setCurrentPage(0);
            }
        };
    }
}
