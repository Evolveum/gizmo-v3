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

import com.evolveum.gizmo.component.*;
import com.evolveum.gizmo.component.modal.MainPopupDialog;
import com.evolveum.gizmo.component.modal.ProjectPartPanel;
import com.evolveum.gizmo.data.Customer;
import com.evolveum.gizmo.data.Part;
import com.evolveum.gizmo.data.Project;
import com.evolveum.gizmo.repository.CustomerRepository;
import com.evolveum.gizmo.repository.PartRepository;
import com.evolveum.gizmo.repository.ProjectRepository;
import com.evolveum.gizmo.util.LoadableModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalDialog;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author lazyman
 */
@MountPath("/app/project")
public class PageProject extends PageAppProjects {

    public static final String PROJECT_ID = "projectId";

    private static final Logger LOG = LoggerFactory.getLogger(PageProject.class);

    private static final String ID_FORM = "form";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SAVE = "save";
    private static final String ID_TABS = "tabs";

    private static final String ID_PART_MODAL = "partModal";

    private IModel<Project> model;

    public PageProject() {
        this(null);
    }

    public PageProject(PageParameters params) {
        super(params);

        model = new LoadableModel<>() {

            @Override
            protected Project load() {
                return loadProject();
            }
        };

        initLayout();
    }

    private Project loadProject() {
        Integer projectId = getIntegerParam(PROJECT_ID);
        if (projectId == null) {
            Project project = new Project();
            Integer customerId = getIntegerParam(PageCustomer.CUSTOMER_ID);
            if (customerId != null) {
                CustomerRepository repository = getCustomerRepository();
                Optional<Customer> customer = repository.findById(customerId);
                customer.ifPresent(project::setCustomer);
            }
            return project;
        }

        ProjectRepository repository = getProjectRepository();
        Optional<Project> project = repository.findById(projectId);
        if (project.isEmpty()) {
            getSession().error(translateString("Message.couldntFindProject", projectId));
            throw new RestartResponseException(PageProject.class);
        }
        return project.get();
    }

    @Override
    protected IModel<String> createPageTitleModel() {
        return () -> {
            Integer id = getIntegerParam(PROJECT_ID);
            String key = id != null ? "page.title.edit" : "page.title";
            return createStringResource(key).getString();
        };
    }

    private void initLayout() {
        MainPopupDialog confirmDownload = new MainPopupDialog(ID_PART_MODAL);
        confirmDownload.setOutputMarkupId(true);
        add(confirmDownload);

        Form<Project> form = new Form<>(ID_FORM);
        add(form);



        initButtons(form);

        List<ITab> tabList = new ArrayList<>();

        tabList.add(new AbstractTab(createStringResource("PageProject.basics")) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new ProjectBasicsPanel(panelId, model);
            }
        });

        tabList.add(new AbstractTab(createStringResource("PageProject.parts")) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new ProjectPartsTab(panelId) {

                    @Override
                    protected void newPartPerformed(AjaxRequestTarget target) {
                        PageProject.this.newPartPerformed(target);
                    }

                    @Override
                    protected void editPartPerformed(AjaxRequestTarget target, Part part) {
                        PageProject.this.editPartPerformed(target, part);
                    }
                };
            }
        });

        GizmoTabbedPanel<ITab> tabs = new GizmoTabbedPanel<>(ID_TABS, tabList);
        tabs.setOutputMarkupId(true);
        form.add(tabs);
    }

    private void initButtons(Form<Project> form) {
        AjaxSubmitButton save = new AjaxSubmitButton(ID_SAVE, createStringResource("GizmoApplication.button.save")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                savePerformed(target);
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
        Project project = model.getObject();
        Customer customer = project.getCustomer();

        if (customer == null || customer.getId() == null) {
            setResponsePage(PageProjects.class);
            return;
        }

        PageParameters params = createPageParams(PageCustomer.CUSTOMER_ID, customer.getId());
        setResponsePage(PageCustomer.class, params);
    }

    private void savePerformed(AjaxRequestTarget target) {
        ProjectRepository repository = getProjectRepository();
        try {
            Project project = model.getObject();
            project = repository.save(project);

            model.setObject(project);

            PageParameters params = createPageParams(PROJECT_ID, project.getId());
            PageProject page = new PageProject(params);
            page.success(getString("Message.projectSavedSuccessfully"));

            setResponsePage(page);
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveProject", ex, target);
        }
    }

    private void newPartPerformed(AjaxRequestTarget target) {
        Part part = new Part();
        part.setProject(model.getObject());

        editPartPerformed(target, part);
    }

    private void editPartPerformed(AjaxRequestTarget target, Part part) {

        if (part.getId() != null) {
            PartRepository pr = getProjectPartRepository();
            part = pr.findAllWithLabelsByIdIn(java.util.List.of(part.getId()))
                    .stream().findFirst()
                    .orElse(part);
        }

        final Part loadedPart = part;

        ProjectPartPanel content = new ProjectPartPanel(ModalDialog.CONTENT_ID, () -> loadedPart) {

            @Override
            protected void savePerformed(AjaxRequestTarget target, IModel<Part> model) {
                savePartPerformed(target, model);
            }

            @Override
            protected void cancelPerformed(AjaxRequestTarget target) {
                closeModal(target);
            }
        };

        content.add(AttributeModifier.append("class", "modal-content"));
        MainPopupDialog partModal = (MainPopupDialog) get(ID_PART_MODAL);
        partModal.setContent(content);
        partModal.open(target);
    }

    private void closeModal(AjaxRequestTarget target) {
        MainPopupDialog partModal = (MainPopupDialog) get(ID_PART_MODAL);
        partModal.close(target);
    }

    private void savePartPerformed(AjaxRequestTarget target, IModel<Part> model) {
        closeModal(target);
        try {
            PartRepository repository = getProjectPartRepository();
            Part part = model.getObject();
            LOG.debug("Saving {}", part);

            repository.saveAndFlush(part);

            success(getString("Message.projectPartSavedSuccessfully"));
            target.add(getFeedbackPanel());
            Form<?> form = (Form<?>) get(ID_FORM);
            GizmoTabbedPanel<?> tabs = (GizmoTabbedPanel<?>) form.get(ID_TABS);
            Component panel = tabs.get("panel");
            target.add(panel);
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveProjectPart", ex, target);
        }
    }
}
