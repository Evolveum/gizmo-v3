package sk.lazyman.gizmo.data.provider;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * @author lazyman
 */
public class ListDataProvider<T extends Serializable> extends SortableDataProvider<T, String> {

    private IModel<List<T>> model;

    public ListDataProvider(IModel<List<T>> model) {
        this.model = model;
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        List<T> list = model.getObject();

        long toIndex = first + count;
        if (toIndex > list.size()) {
            toIndex = list.size();
        }
        return list.subList((int) first, (int) toIndex).listIterator();
    }

    @Override
    public long size() {
        List<T> list = model.getObject();
        return list.size();
    }

    @Override
    public IModel<T> model(T object) {
        return new Model<>(object);
    }
}
