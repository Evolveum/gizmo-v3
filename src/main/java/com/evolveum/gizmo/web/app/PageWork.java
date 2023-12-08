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

package com.evolveum.gizmo.web.app;

import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.repository.PartRepository;
import com.evolveum.gizmo.security.GizmoPrincipal;
import com.evolveum.gizmo.security.SecurityUtils;
import org.apache.commons.lang3.Validate;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;
import org.wicketstuff.annotation.mount.MountPath;
import com.evolveum.gizmo.component.AjaxButton;
import com.evolveum.gizmo.component.AjaxSubmitButton;
import com.evolveum.gizmo.component.PartAutoCompleteText;
import com.evolveum.gizmo.component.behavior.DateRangePickerBehavior;
import com.evolveum.gizmo.data.Part;
import com.evolveum.gizmo.data.Project;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.data.Work;
import com.evolveum.gizmo.repository.WorkRepository;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author lazyman
 */
@MountPath("/app/work")
public class PageWork extends PageAppTemplate {

    public static final String WORK_ID = "workId";

    private static final String ID_FORM = "form";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_DATE = "date";
    private static final String ID_LENGTH = "length";
    private static final String ID_INVOICE = "invoice";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_TRACK_ID = "trackId";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SAVE = "save";
    private static final String ID_PART = "part";

    private IModel<List<CustomerProjectPartDto>> projects =
            GizmoUtils.createCustomerProjectPartList(this, true, true, true);

    private IModel<Work> model;

    public PageWork() {
        model = new LoadableModel<>(false) {

            @Override
            protected Work load() {
                return loadWork();
            }
        };

        initLayout();
    }

    public PageWork(IModel<Work> model) {
        Validate.notNull(model, "Model must not be null.");
        this.model = model;

        initLayout();
    }

    @Override
    protected IModel<String> createPageTitleModel() {
        return (IModel<String>) () -> {
            Integer workId = getIntegerParam(WORK_ID);
            String key = workId != null ? "page.title.edit" : "page.title";
            return createStringResource(key).getString();
        };
    }

    private Work loadWork() {
        Integer workId = getIntegerParam(WORK_ID);
        if (workId == null) {
            GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
            User user = principal.getUser();

            Work work = new Work();
            work.setRealizator(user);
            work.setDate(LocalDate.now());

            return work;
        }

        WorkRepository repository = getWorkRepository();
        Optional<Work> work = repository.findById(workId);
        if (work.isEmpty()) {
            getSession().error(translateString("Message.couldntFindWork", workId));
            throw new RestartResponseException(PageWork.class);
        }

        return work.get();
    }

    private void initLayout() {
        Label realizator = new Label(ID_REALIZATOR, new PropertyModel<>(model, (Work.F_REALIZATOR + ".fullName")));
        realizator.setRenderBodyOnly(true);
        add(realizator);

        Form<Work> form = new Form<>(ID_FORM);
        add(form);

        PartAutoCompleteText part = new PartAutoCompleteText(ID_PART,
                createPartModel(new PropertyModel<>(model, Work.F_PART)),
                GizmoUtils.createCustomerProjectPartList(this, true, true, true));
        form.add(part);

        LocalDateTextField from = new LocalDateTextField(ID_DATE, new PropertyModel<>(model, Work.F_DATE), "dd/MM/yyyy");
        from.setOutputMarkupId(true);
        from.add(new DateRangePickerBehavior());
        form.add(from);

        TextField<Double> invoice = new TextField<>(ID_INVOICE, new PropertyModel<>(model, Work.F_INVOICE_LENGTH), Double.class);
        invoice.add(new RangeValidator<>(0.0, 2000.0));
        invoice.setOutputMarkupId(true);
        form.add(invoice);

        TextField<Double> length = new TextField<>(ID_LENGTH, new PropertyModel<>(model, Work.F_WORK_LENGTH));
        length.add(new RangeValidator<>(0.0, 2000.0));
        length.setType(Double.class);
        form.add(length);

        length.add(new AjaxFormComponentUpdatingBehavior("blur") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (isProjectCommercial()) {
                    Work work = model.getObject();
                    work.setInvoiceLength(work.getWorkLength());

                    target.focusComponent(invoice);
                    target.add(invoice);
                }
            }
        });

        TextArea<String> description = new TextArea<>(ID_DESCRIPTION, new PropertyModel<>(model, Work.F_DESCRIPTION));
        form.add(description);

        TextField<String> trackId = new TextField<>(ID_TRACK_ID, new PropertyModel<>(model, Work.F_TRACK_ID));
        form.add(trackId);

        initButtons(form);
    }

    private boolean isProjectCommercial() {
        Work work = model.getObject();
        Part part = work.getPart();
        if (part == null) {
            return false;
        }

        Project project = part.getProject();
        return project.isCommercial();
    }

    private IModel<CustomerProjectPartDto> createPartModel(final IModel<Part> model) {
        return new IModel<>() {

            private Part part;

            @Override
            public CustomerProjectPartDto getObject() {
                part = model.getObject();

                if (part == null) {
                    return null;
                }

                for (CustomerProjectPartDto dto : projects.getObject()) {
                    if (part.getId().equals(dto.getPartId())) {
                        return dto;
                    }
                }

                return null;
            }

            @Override
            public void setObject(CustomerProjectPartDto object) {
                if (object == null || object.getPartId() == null) {
                    model.setObject(null);
                    return;
                }

                Integer id = object.getPartId();
                if (part != null && id.equals(part.getId())) {
                    model.setObject(part);
                    return;
                }

                PartRepository repository = getProjectPartRepository();
                Optional<Part> optionalPart = repository.findById(id);
                if (optionalPart.isPresent()) {
                    part = optionalPart.get();
                    model.setObject(part);
                }
            }

            @Override
            public void detach() {
            }
        };
    }

    private void initButtons(Form<Work> form) {
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
        setResponsePage(PageDashboard.class);
    }

    private void saveWorkPerformed(AjaxRequestTarget target) {
        WorkRepository repository = getWorkRepository();
        try {
            Work work = model.getObject();
            work = repository.save(work);

            model.setObject(work);

            PageDashboard response = new PageDashboard();
            response.success(createStringResource("Message.workSavedSuccessfully").getString());
            setResponsePage(response);
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveWork", ex, target);
        }
    }
}
