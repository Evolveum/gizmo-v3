package sk.lazyman.gizmo.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * @author lazyman
 */
public class IconColumn<T> extends AbstractColumn<T, String> {

    public IconColumn(IModel<String> displayModel) {
        super(displayModel);
    }

    @Override
    public String getCssClass() {
        return "icon";
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        Label image = new Label(componentId);
        image.add(AttributeModifier.replace("class", createIconModel(rowModel)));

        IModel<String> titleModel = createTitleModel(rowModel);
        if (titleModel != null) {
            image.add(AttributeModifier.replace("title", titleModel));
        }

        cellItem.add(image);
    }

    protected IModel<String> createTitleModel(final IModel<T> rowModel) {
        return null;
    }

    protected IModel<String> createIconModel(final IModel<T> rowModel) {
        throw new UnsupportedOperationException("Not implemented, please implement in your column.");
    }
}
