package sk.lazyman.gizmo.data.provider;

import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author lazyman
 */
public class CustomTabDataProvider<T extends Serializable> extends BasicDataProvider<T> {

    public CustomTabDataProvider(JpaRepository<T, Integer> repository) {
        super(repository);
    }

    public CustomTabDataProvider(JpaRepository<T, Integer> repository, int itemsPerPage) {
        super(repository, itemsPerPage);
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        if (getPredicate() == null) {
            return new ArrayList<T>().iterator();
        }
        return super.iterator(first, count);
    }

    @Override
    public long size() {
        if (getPredicate() == null) {
            return 0L;
        }
        return super.size();
    }
}
