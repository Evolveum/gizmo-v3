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

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByLink;

/**
 * @author lazyman
 */
public abstract class BasicOrderByBorder extends OrderByBorder {

    protected BasicOrderByBorder(String id, Object property, ISortStateLocator stateLocator) {
        super(id, property, stateLocator);
    }

    @Override
    protected OrderByLink newOrderByLink(String id, Object property, ISortStateLocator stateLocator) {
        return new OrderByLink(id, property, stateLocator);
    }

    //TODO override messages if neede:
    /*
    OrderByLink.CSS.ascending=wicket_orderUp
OrderByLink.CSS.descending=wicket_orderDown
OrderByLink.CSS.none=wicket_orderNone
     */
//    public static class BasicCssProvider extends OrderByLink.CssProvider {
//
//        private static BasicCssProvider instance = new BasicCssProvider();
//
//        private BasicCssProvider() {
//            super("sortable asc", "sortable desc", "sortable");
//        }
//
//        public static BasicCssProvider getInstance() {
//            return instance;
//        }
//    }
}
