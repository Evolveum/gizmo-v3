package sk.lazyman.gizmo.data.provider;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import sk.lazyman.gizmo.data.QWork;
import sk.lazyman.gizmo.data.Work;
import sk.lazyman.gizmo.dto.WorkFilterDto;
import sk.lazyman.gizmo.repository.WorkRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author lazyman
 */
public class WorkDataProvider extends SortableDataProvider<Work, String> {

    private WorkRepository workRepository;
    private WorkFilterDto filter;

    public WorkDataProvider(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    @Override
    public Iterator<? extends Work> iterator(long first, long count) {
        Sort sort = new Sort(Sort.Direction.ASC, Work.F_DATE);

        PageRequest page = new PageRequest((int) first, (int) count, sort);
        Page<Work> found = workRepository.findAll(createPredicate(), page);
        if (found != null) {
            return found.iterator();
        }

        return new ArrayList<Work>().iterator();
    }

    @Override
    public long size() {
        return workRepository.count(createPredicate());
    }

    @Override
    public IModel<Work> model(Work object) {
        return new Model<>(object);
    }

    public void setFilter(WorkFilterDto filter) {
        this.filter = filter;
    }

    private Predicate createPredicate() {
        if (filter == null) {
            return null;
        }

        List<Predicate> list = new ArrayList<>();
        if (filter.getFrom() != null) {
            list.add(QWork.work.date.goe(filter.getFrom()));
        }

        if (filter.getTo() != null) {
            list.add(QWork.work.date.loe(filter.getTo()));
        }

        if (filter.getRealizator() != null) {
            list.add(QWork.work.realizator.eq(filter.getRealizator()));
        }

        if (list.isEmpty()) {
            return null;
        }

        BooleanBuilder bb = new BooleanBuilder();
        return bb.orAllOf(list.toArray(new Predicate[list.size()]));
    }
}
