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

package com.evolveum.gizmo.component;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

/**
 * @author lazyman
 */
public class VisibleEnableBehaviour extends Behavior {

    public boolean isVisible() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onConfigure(Component component) {
        component.setEnabled(isEnabled());

        boolean visible = isVisible();
        component.setVisible(visible);
        component.setVisibilityAllowed(visible);
    }
}
