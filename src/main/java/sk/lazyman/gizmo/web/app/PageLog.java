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

import org.apache.commons.lang3.Validate;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.AjaxButton;
import sk.lazyman.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.form.*;
import sk.lazyman.gizmo.data.Customer;
import sk.lazyman.gizmo.data.Log;
import sk.lazyman.gizmo.data.User;
import sk.lazyman.gizmo.dto.CustomerProjectPartDto;
import sk.lazyman.gizmo.repository.CustomerRepository;
import sk.lazyman.gizmo.repository.LogRepository;
import sk.lazyman.gizmo.security.GizmoPrincipal;
import sk.lazyman.gizmo.security.SecurityUtils;
import sk.lazyman.gizmo.util.GizmoUtils;
import sk.lazyman.gizmo.util.LoadableModel;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author lazyman
 */
@MountPath("/app/log")
public class PageLog extends PageAppTemplate {

    public static final String LOG_ID = "logId";

    private static final String ID_FORM = "form";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_DATE = "date";
    private static final String ID_LENGTH = "length";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_TRACK_ID = "trackId";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SAVE = "save";
    private static final String ID_CUSTOMER = "customer";

    private IModel<List<User>> users = GizmoUtils.createUsersModel(this);
    private IModel<List<CustomerProjectPartDto>> projects =
            GizmoUtils.createCustomerProjectPartList(this, true, false, false);

    private IModel<Log> model;

    public PageLog() {
        model = new LoadableModel<Log>(false) {

            @Override
            protected Log load() {
                return loadLog();
            }
        };

        initLayout();
    }

    public PageLog(IModel<Log> model) {
        Validate.notNull(model, "Model must not be null.");
        this.model = model;

        initLayout();
    }

    @Override
    protected IModel<String> createPageTitleModel() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                Integer logId = getIntegerParam(LOG_ID);
                String key = logId != null ? "page.title.edit" : "page.title";
                return createStringResource(key).getString();
            }
        };
    }

    private Log loadLog() {
        Integer logId = getIntegerParam(LOG_ID);
        if (logId == null) {
            GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
            User user = principal.getUser();

            Log log = new Log();
            log.setRealizator(user);
            log.setDate(new Date());

            return log;
        }

        LogRepository repository = getLogRepository();
        Optional<Log> log = repository.findById(logId);
        if (log == null || !log.isPresent()) {
            getSession().error(translateString("Message.couldntFindLog", logId));
            throw new RestartResponseException(PageLog.class);
        }

        return log.get();
    }

    private <T extends FormInput> void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        HDropDownFormGroup<User> realizator = new HDropDownFormGroup<>(ID_REALIZATOR,
                new PropertyModel<User>(model, Log.F_REALIZATOR),
                createStringResource("AbstractTask.realizator"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        realizator.setRenderer(GizmoUtils.createUserChoiceRenderer());
        realizator.setChoices(users);
        form.add(realizator);

        HFormGroup customer = new HFormGroup<T, Customer>(ID_CUSTOMER, new PropertyModel<Customer>(model, Log.F_CUSTOMER),
                createStringResource("Log.customer"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true) {

            @Override
            protected FormInput createInput(String componentId, IModel<Customer> model, IModel<String> placeholder) {
                AutoCompleteInput formInput = new AutoCompleteInput(componentId, createCustomerModel(model), projects);
                FormComponent input = formInput.getFormComponent();
                input.add(AttributeAppender.replace("placeholder", placeholder));

                return formInput;
            }
        };
        form.add(customer);

        HFormGroup date = new HDateFormGroup(ID_DATE, new PropertyModel<Date>(model, Log.F_DATE),
                createStringResource("AbstractTask.date"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        form.add(date);

        HFormGroup length = new HFormGroup(ID_LENGTH, new PropertyModel<String>(model, Log.F_WORK_LENGTH),
                createStringResource("AbstractTask.workLength"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        length.getFormComponent().add(new RangeValidator<>(0, 2000));
        length.getFormComponent().setType(Double.class);
        form.add(length);

        HAreaFormGroup description = new HAreaFormGroup(ID_DESCRIPTION, new PropertyModel<String>(model, Log.F_DESCRIPTION),
                createStringResource("AbstractTask.description"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, true);
        description.setRows(5);
        form.add(description);

        HFormGroup trackId = new HFormGroup(ID_TRACK_ID, new PropertyModel<String>(model, Log.F_TRACK_ID),
                createStringResource("AbstractTask.trackId"), LABEL_SIZE, TEXT_SIZE, FEEDBACK_SIZE, false);
        form.add(trackId);

        initButtons(form);
    }

    private IModel<CustomerProjectPartDto> createCustomerModel(final IModel<Customer> model) {
        return new IModel<CustomerProjectPartDto>() {

            private Customer customer;

            @Override
            public CustomerProjectPartDto getObject() {
                customer = model.getObject();

                if (customer != null) {
                    for (CustomerProjectPartDto dto : projects.getObject()) {
                        if (customer.getId().equals(dto.getCustomerId())) {
                            return dto;
                        }
                    }
                }

                return null;
            }

            @Override
            public void setObject(CustomerProjectPartDto object) {
                if (object == null || object.getCustomerId() == null) {
                    model.setObject(null);
                    return;
                }

                Integer id = object.getCustomerId();
                if (customer != null && id.equals(customer.getId())) {
                    model.setObject(customer);
                    return;
                }

                CustomerRepository repository = getCustomerRepository();
                Optional<Customer> optionalCustomer = repository.findById(id);
                if (optionalCustomer != null && optionalCustomer.isPresent()) {
                    customer = optionalCustomer.get();
                    model.setObject(customer);
                }
            }

            @Override
            public void detach() {
            }
        };
    }

    private void initButtons(Form form) {
        AjaxSubmitButton save = new AjaxSubmitButton(ID_SAVE, createStringResource("GizmoApplication.button.save")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                saveLogPerformed(target);
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

    private void saveLogPerformed(AjaxRequestTarget target) {
        LogRepository repository = getLogRepository();
        try {
            Log log = model.getObject();
            log = repository.save(log);

            model.setObject(log);

            PageDashboard response = new PageDashboard();
            response.success(createStringResource("Message.logSavedSuccessfully").getString());
            setResponsePage(response);
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveLog", ex, target);
        }
    }
}
