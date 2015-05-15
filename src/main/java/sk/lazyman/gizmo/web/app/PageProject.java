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

import com.github.sommeri.less4j.core.ast.Page;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import org.apache.commons.lang.Validate;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.AjaxButton;
import sk.lazyman.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.ProjectPartsTab;
import sk.lazyman.gizmo.component.form.AreaFormGroup;
import sk.lazyman.gizmo.component.form.CheckFormGroup;
import sk.lazyman.gizmo.component.form.DropDownFormGroup;
import sk.lazyman.gizmo.component.form.FormGroup;
import sk.lazyman.gizmo.component.modal.ProjectPartModal;
import sk.lazyman.gizmo.data.Customer;
import sk.lazyman.gizmo.data.Part;
import sk.lazyman.gizmo.data.Project;
import sk.lazyman.gizmo.repository.CustomerRepository;
import sk.lazyman.gizmo.repository.PartRepository;
import sk.lazyman.gizmo.repository.ProjectRepository;
import sk.lazyman.gizmo.util.GizmoUtils;
import sk.lazyman.gizmo.util.LoadableModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
@MountPath("/app/project")
public class PageProject extends PageAppProjects {

    public static final String PROJECT_ID = "projectId";

    private static final Logger LOG = LoggerFactory.getLogger(PageProject.class);

    private static final String ID_FORM = "form";
    private static final String ID_NAME = "name";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_CUSTOMER = "customer";
    private static final String ID_CLOSED = "closed";
    private static final String ID_COMMERCIAL = "commercial";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SAVE = "save";
    private static final String ID_TABS = "tabs";
    private static final String ID_PROJECT_PART = "projectPart";
    private static final String ID_DIALOG_FORM = "dialogForm";

    private IModel<Project> model;

    public PageProject() {
        this(null);
    }

    public PageProject(PageParameters params) {
        super(params);

        model = new LoadableModel<Project>() {

            @Override
            protected Project load() {
                return loadProject();
            }
        };

        initLayout();
    }

    public PageProject(PageParameters params, IModel<Project> model) {
        super(params);

        Validate.notNull(model, "Model must not be null.");
        this.model = model;

        initLayout();
    }

    private void initDialogs() {
        Form dialogForm = new Form(ID_DIALOG_FORM);
        add(dialogForm);

        ProjectPartModal modal = new ProjectPartModal(ID_PROJECT_PART) {

            @Override
            protected void savePerformed(AjaxRequestTarget target, IModel<Part> model) {
                super.savePerformed(target, model);

                savePartPerformed(target, model);
            }
        };
        dialogForm.add(modal);
    }

    private Project loadProject() {
        PageParameters params = getPageParameters();
        StringValue val = params.get(PROJECT_ID);
        String projectId = val != null ? val.toString() : null;

        if (projectId == null || !projectId.matches("[0-9]+")) {
            return new Project();
        }

        ProjectRepository repository = getProjectRepository();
        Project project = repository.findOne(Integer.parseInt(projectId));
        if (project == null) {
            getSession().error(translateString("Message.couldntFindProject", projectId));
            throw new RestartResponseException(PageProject.class);
        }

        return project;
    }

    @Override
    protected IModel<String> createPageTitleModel() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                Integer id = getIntegerParam(PROJECT_ID);
                String key = id != null ? "page.title.edit" : "page.title";
                return createStringResource(key).getString();
            }
        };
    }

    private void initLayout() {
        initDialogs();

        Form form = new Form(ID_FORM);
        add(form);

        FormGroup name = new FormGroup(ID_NAME, new PropertyModel<String>(model, Project.F_NAME),
                createStringResource("Project.name"), true);
        form.add(name);

        FormGroup description = new AreaFormGroup(ID_DESCRIPTION, new PropertyModel<String>(model, Project.F_DESCRIPTION),
                createStringResource("Project.description"), false);
        form.add(description);

        DropDownFormGroup customer = new DropDownFormGroup(ID_CUSTOMER,
                new PropertyModel<Customer>(model, Project.F_CUSTOMER),
                createStringResource("Project.customer"), true);
        customer.setNullValid(false);
        customer.setChoices(new LoadableModel<List<Customer>>(false) {

            @Override
            protected List<Customer> load() {
                CustomerRepository repository = getCustomerRepository();
                List<Customer> list = repository.listCustomers();
                if (list == null) {
                    list = new ArrayList<>();
                }
                return list;
            }
        });
        customer.setRenderer(GizmoUtils.createCustomerChoiceRenderer());
        form.add(customer);

        FormGroup closed = new CheckFormGroup(ID_CLOSED, new PropertyModel<Boolean>(model, Project.F_CLOSED),
                createStringResource("Project.closed"), true);
        form.add(closed);

        FormGroup commercial = new CheckFormGroup(ID_COMMERCIAL, new PropertyModel<Boolean>(model, Project.F_COMMERCIAL),
                createStringResource("Project.commercial"), true);
        form.add(commercial);

        initButtons(form);

        List<ITab> tabList = new ArrayList<>();
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

        AjaxBootstrapTabbedPanel tabs = new AjaxBootstrapTabbedPanel(ID_TABS, tabList);
        tabs.setOutputMarkupId(true);
        add(tabs);
    }

    private void initButtons(Form form) {
        AjaxSubmitButton save = new AjaxSubmitButton(ID_SAVE, createStringResource("GizmoApplication.button.save")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                savePerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
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
        ProjectPartModal modal = (ProjectPartModal) get(ID_DIALOG_FORM + ":" + ID_PROJECT_PART);
        target.add(modal);
        modal.show(target);

        modal.setPart(part);
    }

    private void savePartPerformed(AjaxRequestTarget target, IModel<Part> model) {
        try {
            PartRepository repository = getProjectPartRepository();
            Part part = model.getObject();
            LOG.debug("Saving {}", part);

            repository.saveAndFlush(part);

            success(getString("Message.projectPartSavedSuccessfully"));
            target.add(getFeedbackPanel(), get(ID_TABS));
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveProjectPart", ex, target);
        }
    }
}
