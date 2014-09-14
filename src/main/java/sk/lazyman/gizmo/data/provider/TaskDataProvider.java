package sk.lazyman.gizmo.data.provider;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import sk.lazyman.gizmo.data.QEmailLog;
import sk.lazyman.gizmo.data.QTask;
import sk.lazyman.gizmo.data.Task;
import sk.lazyman.gizmo.dto.TaskFilterDto;
import sk.lazyman.gizmo.repository.TaskRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author lazyman
 */
public class TaskDataProvider extends SortableDataProvider<Task, String> {

    private TaskRepository taskRepository;
    private TaskFilterDto filter;

    public TaskDataProvider(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Iterator<? extends Task> iterator(long first, long count) {
        Sort sort = new Sort(Sort.Direction.ASC, Task.F_DATE);

        PageRequest page = new PageRequest((int) first, (int) count, sort);
        Page<Task> found = taskRepository.findAll(createPredicate(), page);
        if (found != null) {
            return found.iterator();
        }

        return new ArrayList<Task>().iterator();
    }

    @Override
    public long size() {
        return taskRepository.count(createPredicate());
    }

    @Override
    public IModel<Task> model(Task object) {
        return new Model<>(object);
    }

    public void setFilter(TaskFilterDto filter) {
        this.filter = filter;
    }

    private Predicate createPredicate() {
        if (filter == null) {
            return null;
        }

        List<Predicate> list = new ArrayList<>();
        if (filter.getFrom() != null) {
            list.add(QTask.task.date.goe(filter.getFrom()));
        }

        if (filter.getTo() != null) {
            list.add(QTask.task.date.loe(filter.getTo()));
        }

        if (filter.getRealizator() != null) {
            list.add(QTask.task.realizator.eq(filter.getRealizator()));
        }

        if (list.isEmpty()) {
            return null;
        }

        BooleanBuilder bb = new BooleanBuilder();
        return bb.orAllOf(list.toArray(new Predicate[list.size()]));
    }
}
