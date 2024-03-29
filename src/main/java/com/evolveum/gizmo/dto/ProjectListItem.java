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

package com.evolveum.gizmo.dto;

import java.io.Serializable;

/**
 * @author lazyman
 */
public class ProjectListItem<T extends Serializable> implements Serializable {

    public static final String F_NAME = "name";
    public static final String F_DESCRIPTION = "description";
    public static final String F_SELECTED = "selected";

    private T data;
    private boolean selected;

    public ProjectListItem(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return null;
    }

    public String getDescription() {
        return null;
    }
}
