package sk.lazyman.gizmo.web.app;

import de.agilecoders.wicket.less.LessResourceReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.CompanyList;
import sk.lazyman.gizmo.data.Company;
import sk.lazyman.gizmo.dto.CompanyListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
@MountPath("/app/projects")
public class PageProjects extends PageAppTemplate {

    private static final String ID_COMPANY_LIST = "companyList";

    private IModel<List<CompanyListItem>> companies;

    public PageProjects() {
        companies = new LoadableDetachableModel<List<CompanyListItem>>() {

            @Override
            protected List<CompanyListItem> load() {
                List<CompanyListItem> items = new ArrayList<>();

                List<Company> companies = getCompanyRepository().listCompanies();
                if (companies != null) {
                    for (Company company : companies) {
                        items.add(new CompanyListItem(company));
                    }
                }

                return items;
            }
        };

        initLayout();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(
                new LessResourceReference(PageProjects.class, "PageProjects.less")));
    }

    private void initLayout() {
        CompanyList companyList = new CompanyList(ID_COMPANY_LIST, companies);
        add(companyList);
    }
}
