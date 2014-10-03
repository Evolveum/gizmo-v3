package sk.lazyman.gizmo.web.app;

import de.agilecoders.wicket.less.LessResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.ProjectItemList;
import sk.lazyman.gizmo.data.Customer;
import sk.lazyman.gizmo.data.Part;
import sk.lazyman.gizmo.data.Project;
import sk.lazyman.gizmo.dto.ProjectListItem;
import sk.lazyman.gizmo.util.LoadableModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
@MountPath("/app/projects")
public class PageProjects extends PageAppTemplate {

    private static final String ID_COMPANY_LIST = "companyList";
    private static final String ID_PROJECT_LIST = "projectList";
    private static final String ID_PROJECT_PART_LIST = "projectPartList";

    private LoadableModel<List<ProjectListItem<Customer>>> companies;
    private LoadableModel<List<ProjectListItem<Project>>> projects;
    private LoadableModel<List<ProjectListItem<Part>>> parts;

    public PageProjects() {
        companies = new LoadableModel<List<ProjectListItem<Customer>>>(false) {

            @Override
            protected List<ProjectListItem<Customer>> load() {
                return loadCompanies();
            }
        };

        projects = new LoadableModel<List<ProjectListItem<Project>>>(false) {

            @Override
            protected List<ProjectListItem<Project>> load() {
                return loadProjects();
            }
        };

        parts = new LoadableModel<List<ProjectListItem<Part>>>(false) {

            @Override
            protected List<ProjectListItem<Part>> load() {
                return loadProjectParts();
            }
        };

        initLayout();
    }

    private List<ProjectListItem<Part>> loadProjectParts() {
        List<ProjectListItem<Part>> items = new ArrayList<>();

        Project project = getSelectedItem(projects.getObject());
        if (project == null) {
            return items;
        }

        List<Part> projects = getProjectPartRepository().findParts(project.getId());
        if (projects != null) {
            for (Part part : projects) {
                ProjectListItem item = new ProjectListItem<Part>(part) {

                    @Override
                    public String getName() {
                        return getData().getName();
                    }
                };
                items.add(item);
            }
        }

        return items;
    }

    private List<ProjectListItem<Project>> loadProjects() {
        List<ProjectListItem<Project>> items = new ArrayList<>();

        Customer company = getSelectedItem(companies.getObject());
        if (company == null) {
            return items;
        }

        List<Project> projects = getProjectRepository().findProjects(company.getId());
        if (projects != null) {
            for (Project project : projects) {
                ProjectListItem item = new ProjectListItem<Project>(project) {

                    @Override
                    public String getName() {
                        return getData().getName();
                    }

                    @Override
                    public String getDescription() {
                        return getData().getDescription();
                    }
                };
                items.add(item);
            }
        }

        return items;
    }

    private List<ProjectListItem<Customer>> loadCompanies() {
        List<ProjectListItem<Customer>> items = new ArrayList<>();

        List<Customer> companies = getCustomerRepository().listCustomers();
        if (companies != null) {
            for (Customer company : companies) {
                ProjectListItem item = new ProjectListItem<Customer>(company) {

                    @Override
                    public String getName() {
                        return getData().getName();
                    }

                    @Override
                    public String getDescription() {
                        return getData().getDescription();
                    }
                };
                items.add(item);
            }
        }

        return items;
    }

    private <T extends Serializable> T getSelectedItem(List<ProjectListItem<T>> list) {
        for (ProjectListItem<T> item : list) {
            if (!item.isSelected()) {
                continue;
            }

            return item.getData();
        }

        return null;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(
                new LessResourceReference(PageProjects.class, "PageProjects.less")));
    }

    private void initLayout() {
        ProjectItemList companyList = new ProjectItemList<Customer>(ID_COMPANY_LIST, companies) {

            @Override
            protected void itemSelected(AjaxRequestTarget target, ProjectListItem<Customer> selected) {
                super.itemSelected(target, selected);
                companySelected(target, selected);
            }

            @Override
            protected void editPerformed(AjaxRequestTarget target, ProjectListItem<Customer> selected) {
                super.editPerformed(target, selected);
                companyEdited(target, selected);
            }
        };
        add(companyList);

        ProjectItemList projectList = new ProjectItemList<Project>(ID_PROJECT_LIST, projects) {

            @Override
            protected void itemSelected(AjaxRequestTarget target, ProjectListItem<Project> selected) {
                super.itemSelected(target, selected);
                projectSelected(target, selected);
            }

            @Override
            protected void editPerformed(AjaxRequestTarget target, ProjectListItem<Project> selected) {
                super.editPerformed(target, selected);
                projectEdited(target, selected);
            }
        };
        add(projectList);

        ProjectItemList projectPartList = new ProjectItemList<Part>(ID_PROJECT_PART_LIST, parts) {

            @Override
            protected void itemSelected(AjaxRequestTarget target, ProjectListItem<Part> selected) {
                super.itemSelected(target, selected);
                projectPartSelected(target, selected);
            }

            @Override
            protected void editPerformed(AjaxRequestTarget target, ProjectListItem<Part> selected) {
                super.editPerformed(target, selected);
                projectPartEdited(target, selected);
            }
        };
        add(projectPartList);
    }

    private void companySelected(AjaxRequestTarget target, ProjectListItem selected) {
        projects.reset();
        target.add(get(ID_PROJECT_LIST));

        parts.reset();
        target.add(get(ID_PROJECT_PART_LIST));
    }

    private void companyEdited(AjaxRequestTarget target, ProjectListItem selected) {
        //todo implement
    }

    private void projectSelected(AjaxRequestTarget target, ProjectListItem selected) {
        parts.reset();
        target.add(get(ID_PROJECT_PART_LIST));
    }

    private void projectEdited(AjaxRequestTarget target, ProjectListItem selected) {
        //todo implement
    }

    private void projectPartSelected(AjaxRequestTarget target, ProjectListItem selected) {

    }

    private void projectPartEdited(AjaxRequestTarget target, ProjectListItem selected) {
        //todo implement
    }
}
