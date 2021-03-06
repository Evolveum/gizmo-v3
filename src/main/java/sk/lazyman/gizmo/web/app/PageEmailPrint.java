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

package sk.lazyman.gizmo.web.app;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import sk.lazyman.gizmo.component.DataPrintPanel;
import sk.lazyman.gizmo.data.AbstractTask;
import sk.lazyman.gizmo.dto.WorkFilterDto;

import java.util.List;

/**
 * @author lazyman
 */
public class PageEmailPrint extends WebPage {

    private static final String ID_DATA_PRINT = "dataPrint";

    public PageEmailPrint(IModel<WorkFilterDto> filter, IModel<List<AbstractTask>> dataModel) {
        initLayout(filter != null ? filter : new Model<>(new WorkFilterDto()), dataModel);
    }

    private void initLayout(IModel<WorkFilterDto> filter, IModel<List<AbstractTask>> dataModel) {
        DataPrintPanel dataPrint = new DataPrintPanel(ID_DATA_PRINT, filter, dataModel);
        add(dataPrint);
    }
}
