/*
 *  Copyright (C) 2024 Evolveum
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

package com.evolveum.gizmo.component.modal;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;

import java.time.LocalDate;

public class CellDefinitionType {

    private int position;
    private Class<?> cellType;
    private CellStyle style;
    private String cellHeaderName;
    private String getMethod;

    public CellDefinitionType(String cellHeaderName, int i, Class<?> cellType, CellStyle style, String getMethod) {
        this.position = i;
        this.cellType = cellType;
        this.style = style;
        this.cellHeaderName = cellHeaderName;
        this.getMethod = getMethod;
    }

    public String getDisplayName() {
        return cellHeaderName;
    }

    public int getPosition() {
        return position;
    }

    public CellStyle getStyle() {
        return style;
    }

    public CellType getCellType() {
        return LocalDate.class.equals(cellType) || double.class.equals(cellType) ? CellType.NUMERIC : CellType.STRING;
    }

    public String getGetMethod() {
        return getMethod;
    }
}
