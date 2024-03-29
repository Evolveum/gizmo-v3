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

import com.evolveum.gizmo.component.VisibleEnableBehaviour;
import com.evolveum.gizmo.component.form.IconButton;
import com.evolveum.gizmo.component.form.MultiselectDropDownInput;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.WorkDto;
import com.evolveum.gizmo.repository.PartRepository;
import com.evolveum.gizmo.security.GizmoPrincipal;
import com.evolveum.gizmo.security.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lazyman
 */
@MountPath("/app/work")
public class PageWork extends PageAppTemplate {

    public static final String WORK_ID = "workId";

    private static final String ID_FORM = "form";
    private static final String ID_DATE = "date";
    private static final String ID_LENGTH = "length";
    private static final String ID_INVOICE = "invoice";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_TRACK_ID = "trackId";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SAVE = "save";
    private static final String ID_ADD = "add";
    private static final String ID_CUSTOMER_PROJECT_PART = "customerProjectPart";

    private IModel<List<WorkDto>> model;

    public PageWork() {
        this(null);
    }

    public PageWork(PageParameters params) {
        super(params);
        model = new LoadableModel<>(false) {

            @Override
            protected List<WorkDto> load() {
                return loadWorks();
            }
        };

        initLayout();
    }

    @Override
    protected IModel<String> createPageTitleModel() {
        return () -> {
            Integer workId = getIntegerParam(WORK_ID);
            String key = workId != null ? "page.title.edit" : "page.title";
            GizmoPrincipal principal = SecurityUtils.getPrincipalUser();

            return createStringResource(key, principal.getUser().getFullName()).getString();
        };
    }

    @Override
    public Fragment createHeaderButtonsFragment(String fragmentId) {
        Fragment fragment = new  Fragment(fragmentId, "buttonsFragment", this);

        AjaxButton addWork = new AjaxButton(ID_ADD, createStringResource("GizmoApplication.button.new")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                addNewWork(target);
            }
        };
        addWork.add(new VisibleEnableBehaviour() {
            @Override
            public boolean isVisible() {
                return isMultipleWorkEnabled();
            }
        });
        fragment.add(addWork);
        return fragment;
    }

    protected boolean isMultipleWorkEnabled() {
        return true;
    }

    private void addNewWork(AjaxRequestTarget target) {
        model.getObject().add(new WorkDto());
        target.add(PageWork.this);
    }

    private List<WorkDto> loadWorks() {
        ArrayList<WorkDto> list = new ArrayList<>();
        list.add(loadWork());
        return list;
    }

    private WorkDto loadWork() {
        Integer workId = getIntegerParam(WORK_ID);
        if (workId == null) {
            return new WorkDto();
        }

        WorkRepository repository = getWorkRepository();
        Optional<Work> work = repository.findById(workId);
        if (work.isEmpty()) {
            getSession().error(translateString("Message.couldntFindWork", workId));
            throw new RestartResponseException(PageWork.class);
        }

        return new WorkDto(work.get());
    }

    private void initLayout() {


        Form<Work> form = new Form<>(ID_FORM);
        add(form);

        ListView<WorkDto> list = new ListView<>("works", model) {

            @Override
            protected void populateItem(ListItem<WorkDto> item) {
                createNewWorkForm(item, item.getModel());
            }
        };
        form.add(list);

        initButtons(form);
    }

    private void createNewWorkForm(ListItem<WorkDto> item, IModel<WorkDto> workModel) {
        MultiselectDropDownInput<CustomerProjectPartDto> partCombo = new MultiselectDropDownInput<>(ID_CUSTOMER_PROJECT_PART,
                new PropertyModel<>(workModel, WorkDto.F_CUSTOMER_PROJECT_PART),
                isMultiProjectEnabled(),
                GizmoUtils.createCustomerProjectPartList(this, true, true, true),
                GizmoUtils.createCustomerProjectPartRenderer());
        item.add(partCombo);

        LocalDateTextField from = new LocalDateTextField(ID_DATE, new PropertyModel<>(workModel, WorkDto.F_DATE), "dd/MM/yyyy");
        from.setOutputMarkupId(true);
        from.add(new DateRangePickerBehavior());
        item.add(from);

        TextField<Double> invoice = new TextField<>(ID_INVOICE, new PropertyModel<>(workModel, WorkDto.F_INVOICE_LENGTH), Double.class);
        invoice.add(new RangeValidator<>(0.0, 2000.0));
        invoice.setOutputMarkupId(true);
        item.add(invoice);

        TextField<Double> length = new TextField<>(ID_LENGTH, new PropertyModel<>(workModel, WorkDto.F_WORK_LENGTH));
        length.add(new RangeValidator<>(0.0, 2000.0));
        length.setType(Double.class);
        item.add(length);

        TextArea<String> description = new TextArea<>(ID_DESCRIPTION, new PropertyModel<>(workModel, WorkDto.F_DESCRIPTION));
        item.add(description);

        TextField<String> trackId = new TextField<>(ID_TRACK_ID, new PropertyModel<>(workModel, WorkDto.F_TRACK_ID));
        item.add(trackId);
    }

    protected boolean isMultiProjectEnabled() {
        return getIntegerParam(WORK_ID) == null;
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
        try {
            PartRepository repositoryPart = getProjectPartRepository();
            List<WorkDto> preparedWorks = model.getObject();

            List<Work> works = preparedWorks.stream()
                    .map(preparedWork -> preparedWork.createWorks(repositoryPart))
                    .flatMap(Collection::stream)
                    .toList();

            getWorkRepository().saveAll(works);

            PageDashboard response = new PageDashboard();
            response.success(createStringResource("Message.workSavedSuccessfully").getString());
            setResponsePage(response);
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveWork", ex, target);
        }
    }
}
