package sk.lazyman.gizmo.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import sk.lazyman.gizmo.dto.CompanyListItem;

import java.util.List;

/**
 * @author lazyman
 */
public class CompanyList extends SimplePanel<List<CompanyListItem>> {

    private static final String ID_REPEATER = "repeater";
    private static final String ID_LINK = "link";
    private static final String ID_EDIT = "edit";
    private static final String ID_NAME = "name";
    private static final String ID_DESCRIPTION = "description";

    public CompanyList(String id, IModel<List<CompanyListItem>> model) {
        super(id, model);

        add(AttributeModifier.replace("class", "list-group"));
        setOutputMarkupId(true);
    }

    @Override
    protected void initLayout() {
        ListView<CompanyListItem> repeater = new ListView<CompanyListItem>(ID_REPEATER, getModel()) {

            @Override
            protected void populateItem(final ListItem<CompanyListItem> item) {
                item.add(AttributeAppender.append("class", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        CompanyListItem i = item.getModelObject();
                        return i.isSelected() ? "list-group-item-info" : null;
                    }
                }));

                AjaxLink link = new AjaxLink(ID_LINK) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        companySelected(target, item.getModelObject());
                    }
                };
                item.add(link);

                Label name = new Label(ID_NAME,
                        new PropertyModel<>(item.getModel(), CompanyListItem.F_NAME));
                name.setRenderBodyOnly(true);
                link.add(name);

                Label description = new Label(ID_DESCRIPTION,
                        new PropertyModel<>(item.getModel(), CompanyListItem.F_DESCRIPTION));
                description.setRenderBodyOnly(true);
                link.add(description);

                AjaxLink edit = new AjaxLink(ID_EDIT) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        editPerformed(target);
                    }
                };
                item.add(edit);
            }
        };
        add(repeater);
    }

    private void companySelected(AjaxRequestTarget target, CompanyListItem selected) {
        target.add(this);

        List<CompanyListItem> items = getModelObject();
        for (CompanyListItem item : items) {
            item.setSelected(false);
        }

        selected.setSelected(true);
    }

    private void editPerformed(AjaxRequestTarget target) {
        //todo implement
    }
}
