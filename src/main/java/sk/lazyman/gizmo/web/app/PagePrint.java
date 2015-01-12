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

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Predicate;
import de.agilecoders.wicket.less.LessResourceReference;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.DataPrintPanel;
import sk.lazyman.gizmo.component.PartAutoCompleteConverter;
import sk.lazyman.gizmo.component.ReportSearchSummary;
import sk.lazyman.gizmo.component.VisibleEnableBehaviour;
import sk.lazyman.gizmo.data.*;
import sk.lazyman.gizmo.data.provider.AbstractTaskDataProvider;
import sk.lazyman.gizmo.dto.CustomerProjectPartDto;
import sk.lazyman.gizmo.dto.WorkFilterDto;
import sk.lazyman.gizmo.util.GizmoUtils;
import sk.lazyman.gizmo.util.LoadableModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lazyman
 */
@MountPath("/app/print")
public class PagePrint extends PageAppTemplate {

    private static final String ID_DATA_PRINT = "dataPrint";

    public PagePrint() {
        this(null);
    }

    public PagePrint(IModel<WorkFilterDto> filter) {
        initLayout(filter != null ? filter : new Model<>(new WorkFilterDto()));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new LessResourceReference(PagePrint.class, "PagePrint.less")));
    }

    private void initLayout(IModel<WorkFilterDto> filter) {
        DataPrintPanel dataPrint = new DataPrintPanel(ID_DATA_PRINT, filter, getEntityManager());
        add(dataPrint);
    }
}
