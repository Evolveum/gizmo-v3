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

import com.evolveum.gizmo.component.form.*;
import com.evolveum.gizmo.data.Part;
import com.evolveum.gizmo.data.TaskType;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.data.Work;
import com.evolveum.gizmo.dto.BulkDto;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.repository.PartRepository;
import com.evolveum.gizmo.repository.WorkRepository;
import com.evolveum.gizmo.security.GizmoPrincipal;
import com.evolveum.gizmo.security.SecurityUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;
import com.evolveum.gizmo.component.AjaxButton;
import com.evolveum.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.form.*;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author lazyman
 */
@MountPath("/app/to")
public class PageBulk extends PageAppTemplate {

    private static final String ID_FORM = "form";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_FROM = "from";
    private static final String ID_TO = "to";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SAVE = "save";
    private static final String ID_PART = "part";

    private static final int MAX_BULK_CREATE = 20;

    private IModel<List<User>> users = GizmoUtils.createUsersModel(this);
    private IModel<List<CustomerProjectPartDto>> projects =
            GizmoUtils.createCustomerProjectPartList(this, true, true, true);

    private IModel<BulkDto> model;

    public PageBulk() {
        model = new LoadableModel<BulkDto>(false) {

            @Override
            protected BulkDto load() {
                BulkDto dto = new BulkDto();
                GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
                dto.setRealizator(principal.getUser());
                dto.setFrom(LocalDate.now());

                return dto;
            }
        };

        initLayout();
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        HDropDownFormGroup<User> realizator = new HDropDownFormGroup<>(ID_REALIZATOR,
                new PropertyModel<User>(model, BulkDto.F_REALIZATOR),
                createStringResource("AbstractTask.realizator"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        realizator.setRenderer(GizmoUtils.createUserChoiceRenderer());
        realizator.setChoices(users);
        form.add(realizator);

        HFormGroup part = new HFormGroup<AutoCompleteInput, CustomerProjectPartDto>(ID_PART,
                new PropertyModel<CustomerProjectPartDto>(model, BulkDto.F_PART),
                createStringResource("Work.part"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true) {

            @Override
            protected FormInput createInput(String componentId, IModel<CustomerProjectPartDto> model,
                                            IModel<String> placeholder) {
                AutoCompleteInput formInput = new AutoCompleteInput(componentId, model, projects);
                FormComponent input = formInput.getFormComponent();
                input.add(AttributeAppender.replace("placeholder", placeholder));

                return formInput;
            }
        };
        form.add(part);
        FormComponent partText = part.getFormComponent();
        partText.add(new AjaxFormComponentUpdatingBehavior("blur") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });

        HFormGroup from = new HDateFormGroup(ID_FROM, new PropertyModel<Date>(model, BulkDto.F_FROM),
                createStringResource("PageBulk.from"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(from);

        HFormGroup to = new HDateFormGroup(ID_TO, new PropertyModel<Date>(model, BulkDto.F_TO),
                createStringResource("PageBulk.to"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(to);

        HAreaFormGroup description = new HAreaFormGroup(ID_DESCRIPTION, new
                PropertyModel<String>(model, BulkDto.F_DESCRIPTION),
                createStringResource("AbstractTask.description"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        description.setRows(5);
        form.add(description);

        initButtons(form);
    }


    private void initButtons(Form form) {
        AjaxSubmitButton save = new AjaxSubmitButton(ID_SAVE, createStringResource("GizmoApplication.button.save")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                saveWorkPerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(form);
            }
        };
        form.add(save);

        AjaxButton cancel = new AjaxButton(ID_CANCEL, createStringResource("GizmoApplication.button.cancel")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                cancelPerformed(target);
            }
        };
        form.add(cancel);
    }

    private void cancelPerformed(AjaxRequestTarget target) {
        setResponsePage(PageDashboard.class);
    }

    private void saveWorkPerformed(AjaxRequestTarget target) {
        WorkRepository repository = getWorkRepository();
        try {
            BulkDto bulk = model.getObject();

            PartRepository parts = getProjectPartRepository();
            CustomerProjectPartDto partDto = bulk.getPart();
            Optional<Part> optionalPart = parts.findById(partDto.getPartId());
            Part part = null;
            if (optionalPart != null && optionalPart.isPresent()) {
                part = optionalPart.get();
            }

            LocalDate date = bulk.getFrom(); //GizmoUtils.clearTime(bulk.getFrom());
            LocalDate to = bulk.getTo(); //GizmoUtils.clearTime(bulk.getTo());
//            to = GizmoUtils.removeOneMilis(GizmoUtils.addOneDay(to));

            int count = 0;
            while (date.isBefore(to)) {
                Work work = createWork(bulk, part, date);
                repository.save(work);

                count++;
                date = date.plusDays(1);

                if (count > MAX_BULK_CREATE) {
                    break;
                }
            }
            PageDashboard response = new PageDashboard();
            response.success(createStringResource("Message.workSavedSuccessfully").getString());
            if (count > MAX_BULK_CREATE) {
                response.warn(createStringResource("Message.bulkStopped", MAX_BULK_CREATE).getString());
            }
            setResponsePage(response);
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveWork", ex, target);
        }
    }

    private Work createWork(BulkDto bulk, Part part, LocalDate date) {
        Work work = new Work();
        work.setRealizator(bulk.getRealizator());
        work.setPart(part);
        work.setDate(date);
        work.setDescription(bulk.getDescription());
        work.setInvoiceLength(0);
        work.setWorkLength(8);
        work.setType(TaskType.WORK);

        return work;
    }
}
