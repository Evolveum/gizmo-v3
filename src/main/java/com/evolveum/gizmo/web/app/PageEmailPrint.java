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

import com.evolveum.gizmo.data.AbstractTask;
import com.evolveum.gizmo.dto.ReportFilterDto;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import com.evolveum.gizmo.component.DataPrintPanel;

import java.util.List;

/**
 * @author lazyman
 */
public class PageEmailPrint extends WebPage {

    private static final String ID_DATA_PRINT = "dataPrint";

    public PageEmailPrint(IModel<ReportFilterDto> filter, IModel<List<AbstractTask>> dataModel) {
        initLayout(filter != null ? filter : new Model<>(new ReportFilterDto()), dataModel);
    }

    private void initLayout(IModel<ReportFilterDto> filter, IModel<List<AbstractTask>> dataModel) {
        DataPrintPanel dataPrint = new DataPrintPanel(ID_DATA_PRINT, filter, dataModel);
        add(dataPrint);
    }
}
