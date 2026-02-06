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

import com.evolveum.gizmo.component.AjaxButton;
import com.evolveum.gizmo.component.AjaxSubmitButton;
import com.evolveum.gizmo.component.behavior.DateRangePickerBehavior;
import com.evolveum.gizmo.component.form.MultiselectDropDownInput;
import com.evolveum.gizmo.data.Part;
import com.evolveum.gizmo.data.TaskType;
import com.evolveum.gizmo.data.Work;
import com.evolveum.gizmo.dto.BulkDto;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.ProjectSearchSettings;
import com.evolveum.gizmo.repository.PartRepository;
import com.evolveum.gizmo.repository.WorkRepository;
import com.evolveum.gizmo.security.GizmoPrincipal;
import com.evolveum.gizmo.security.SecurityUtils;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.RangeValidator;
import org.wicketstuff.annotation.mount.MountPath;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author lazyman
 */
@MountPath("/app/to")
public class PageBulk extends PageAppTemplate {

    private static final String ID_FORM = "form";
    private static final String ID_LENGTH = "length";
    private static final String ID_FROM = "from";
    private static final String ID_TO = "to";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SAVE = "save";
    private static final String ID_PART = "part";

    private static final int MAX_BULK_CREATE = 20;

    private final IModel<BulkDto> model;
    private final PageReference returnPage;

    private PageBulk(PageReference returnPage, PageParameters params) {
        super(params);
        this.returnPage = returnPage;

        this.model = new LoadableModel<>(false) {
            @Override
            protected BulkDto load() {
                BulkDto dto = new BulkDto();
                GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
                dto.setRealizator(principal.getUser());
                dto.setFrom(LocalDate.now());
                dto.setWorkLength(8);
                return dto;
            }
        };

        initLayout();
    }

    public PageBulk(PageReference returnPage) {
        this(returnPage, new PageParameters());
    }

    public PageBulk() {
        this(null, new PageParameters());
    }

    @Override
    protected IModel<String> createPageTitleModel() {
        return () -> {
            GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
            return createStringResource("page.title", principal.getUser().getFullName()).getString();
        };
    }

    private void initLayout() {
        Form<?> form = new Form<>(ID_FORM);
        add(form);

        MultiselectDropDownInput<CustomerProjectPartDto> partCombo = new MultiselectDropDownInput<>(ID_PART,
                new PropertyModel<>(model, BulkDto.F_PART),
                false,
                GizmoUtils.createCustomerProjectPartList(this, () -> new ProjectSearchSettings(true, true, true)),
                GizmoUtils.createCustomerProjectPartRenderer());
        form.add(partCombo);

        LocalDateTextField from = new LocalDateTextField(ID_FROM, new PropertyModel<>(model, BulkDto.F_FROM), "dd/MM/yyyy");
        from.setOutputMarkupId(true);
        from.add(new DateRangePickerBehavior());
        form.add(from);

        LocalDateTextField to = new LocalDateTextField(ID_TO, new PropertyModel<>(model, BulkDto.F_TO), "dd/MM/yyyy");
        to.setOutputMarkupId(true);
        to.add(new DateRangePickerBehavior());
        form.add(to);

        TextField<Double> length = new TextField<>(ID_LENGTH, new PropertyModel<>(model, BulkDto.F_WORK_LENGTH));
        length.add(new RangeValidator<>(0.0, 2000.0));
        length.setType(Double.class);
        form.add(length);

        TextArea<String> description = new TextArea<>(ID_DESCRIPTION, new PropertyModel<>(model, BulkDto.F_DESCRIPTION));
        form.add(description);


        initButtons(form);
    }


    private void initButtons(Form<?> form) {
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
                cancelPerformed();
            }
        };
        form.add(cancel);
    }

    private void cancelPerformed() {
        if (returnPage != null && returnPage.getPage() != null) {
            setResponsePage(returnPage.getPage());
        } else {
            setResponsePage(PageDashboard.class);
        }
    }

    private void saveWorkPerformed(AjaxRequestTarget target) {
        WorkRepository repository = getWorkRepository();
        try {
            BulkDto bulk = model.getObject();

            PartRepository partsRepository = getProjectPartRepository();
            List<CustomerProjectPartDto> parts = bulk.getPart();

            if (bulk.getFrom() == null || bulk.getTo() == null) {
                error(createStringResource("Message.SetFromAndTo").getString());
                target.add(getFeedbackPanel());
                return;
            }

            if (bulk.getFrom().isAfter(bulk.getTo())) {
                error(createStringResource("Message.ToBeforeAfter").getString());
                target.add(getFeedbackPanel());
                return;
            }

            if (parts.size() > 1) {
                error(createStringResource("Message.onlyOnePartAllowed").getString());
                target.add(getFeedbackPanel());
                return;
            }
            CustomerProjectPartDto partDto = parts.getFirst();
            Optional<Part> optionalPart = partsRepository.findById(partDto.getPartId());
            Part part = null;
            if (optionalPart.isPresent()) {
                part = optionalPart.get();
            }

            LocalDate date = bulk.getFrom();
            LocalDate end = bulk.getTo();

            LocalTime startTime = LocalTime.of(8, 0);

            List<Work> works = new ArrayList<>();
            int count = 0;

            while (!date.isAfter(end)) {

                if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY || !GizmoUtils.isNotHoliday(date)) {
                    date = date.plusDays(1);
                    continue;
                }
                Work work = createWork(bulk, part, date, startTime);
                works.add(work);
                count++;

                if (count >= MAX_BULK_CREATE) {
                    break;
                }

                date = date.plusDays(1);
            }

            repository.saveAll(works);

            success(createStringResource("Message.workSavedSuccessfully").getString());

            if (count >= MAX_BULK_CREATE) {
                warn(createStringResource("Message.bulkStopped", MAX_BULK_CREATE).getString());
            }

            if (returnPage != null && returnPage.getPage() != null) {
                setResponsePage(returnPage.getPage());
            } else {
                setResponsePage(PageDashboard.class);
            }
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveWork", ex, target);
        }
    }

    private Work createWork(BulkDto bulk, Part part, LocalDate date, LocalTime startTime) {
        Work work = new Work();
        work.setRealizator(bulk.getRealizator());
        work.setPart(part);
        work.setDate(date);
        work.setFrom(startTime);
        work.setTo(startTime.plusMinutes((long) (bulk.getWorkLength() * 60)));
        work.setWorkLength(bulk.getWorkLength());
        work.setInvoiceLength(0);
        work.setDescription(bulk.getDescription());
        work.setType(TaskType.WORK);

        return work;
    }
}
