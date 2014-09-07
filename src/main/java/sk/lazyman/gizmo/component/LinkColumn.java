package sk.lazyman.gizmo.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * @author lazyman
 */
public class LinkColumn<T> extends AbstractColumn<T, String> {

    private String propertyExpression;

    public LinkColumn(IModel<String> displayModel) {
        super(displayModel);
    }

    public LinkColumn(IModel<String> displayModel, String propertyExpression) {
        this(displayModel, null, propertyExpression);
    }

    public LinkColumn(IModel<String> displayModel, String sortProperty, String propertyExpression) {
        super(displayModel, sortProperty);
        this.propertyExpression = propertyExpression;
    }

    protected String getPropertyExpression() {
        return propertyExpression;
    }

    protected IModel<String> createLinkModel(IModel<T> rowModel) {
        return new PropertyModel<String>(rowModel, propertyExpression);
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, final IModel<T> rowModel) {
        cellItem.add(new LinkPanel(componentId, createLinkModel(rowModel)) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                LinkColumn.this.onClick(target, rowModel);
            }

            @Override
            public boolean isEnabled() {
                return LinkColumn.this.isEnabled(rowModel);
            }
        });
    }

    public boolean isEnabled(IModel<T> rowModel) {
        return true;
    }

    public void onClick(AjaxRequestTarget target, IModel<T> rowModel) {
    }
}
