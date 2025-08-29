/*
 *  Copyright (C) 2025 Evolveum
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

import com.evolveum.gizmo.component.SimplePanel;
import com.evolveum.gizmo.data.AbstractTask;
import com.evolveum.gizmo.data.provider.ReportDataProvider;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.WorkDto;
import com.evolveum.gizmo.repository.AbstractTaskRepository;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.web.app.PageWork;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

public class WorkDataTable extends SimplePanel<ReportFilterDto> {

    private static final String ID_TABLE = "table";

    private final boolean editable;

    public WorkDataTable(String id, IModel<ReportFilterDto> model, boolean editable) {
        super(id, model);
        this.editable = editable;
    }

    @Override
    protected void initLayout() {

        ReportDataProvider provider = new ReportDataProvider(getPageTemplate());
        provider.setFilter(getModelObject());

        List<IColumn<WorkDto, String>> columns = createColumns();
        TablePanel<WorkDto> table = new TablePanel<>(ID_TABLE, provider, columns, 20);
        table.setOutputMarkupId(true);
        add(table);
    }

    private List<IColumn<WorkDto, String>> createColumns() {
        List<IColumn<WorkDto, String>> columns = new ArrayList<>();

        columns.add(new EditablePropertyColumn<>(createStringResource("AbstractTask.date"), AbstractTask.F_DATE));
        columns.add(GizmoUtils.createWorkInvoiceColumn(getPageTemplate()));
        columns.add(GizmoUtils.createWorkTimeRangeColumn(getPageTemplate()));
        columns.add(GizmoUtils.createWorkProjectColumn(getPageTemplate()));
        columns.add(new EditablePropertyColumn<>(createStringResource("AbstractTask.trackId"), AbstractTask.F_TRACK_ID));
        columns.add(new EditablePropertyColumn<>(createStringResource("AbstractTask.description"), AbstractTask.F_DESCRIPTION));

        if (editable) {
            columns.add(new LinkIconColumn<>(new Model<>("")) {

                @Override
                protected IModel<String> createIconModel(IModel<WorkDto> rowModel) {
                    return new Model<>("fa fa-trash text-danger");
                }

                @Override
                protected IModel<String> createTitleModel(IModel<WorkDto> rowModel) {
                    return getPageTemplate().createStringResource("PageDashboard.delete");
                }

                @Override
                protected void onClickPerformed(AjaxRequestTarget target, IModel<WorkDto> rowModel, AjaxLink<?> link) {
                    deletePerformed(target, rowModel.getObject());
                }
            });

            columns.add(new LinkIconColumn<>(new Model<>("")) {

                @Override
                protected IModel<String> createIconModel(IModel<WorkDto> rowModel) {
                    return new Model<>("fa fa-edit text-default");
                }

                @Override
                protected IModel<String> createTitleModel(IModel<WorkDto> rowModel) {
                    return getPageTemplate().createStringResource("PageDashboard.edit");
                }

                @Override
                protected void onClickPerformed(AjaxRequestTarget target, IModel<WorkDto> rowModel, AjaxLink<?> link) {
                    editWorkReportPerformed(rowModel.getObject());

                }
            });
        }

        return columns;
    }

    private void editWorkReportPerformed(WorkDto work) {
        PageParameters params = new PageParameters();
        params.add("workId", work.getId());
        setResponsePage(PageWork.class, params);
    }

    private void deletePerformed(AjaxRequestTarget target, WorkDto task) {
        //todo add confirmation
        try {
            AbstractTaskRepository repository = getPageTemplate().getAbstractTaskRepository();
            repository.deleteById(task.getId());

            success(createStringResource("Message.successfullyDeleted").getString());
            refresh(target);
        } catch (Exception ex) {
            getPageTemplate().handleGuiException(getPageTemplate(), "Message.couldntSaveWork", ex, target);
        }
    }

    protected void refresh(AjaxRequestTarget target) {
        throw new UnsupportedOperationException("Implement in caller for editable version of table.");
    }
}
