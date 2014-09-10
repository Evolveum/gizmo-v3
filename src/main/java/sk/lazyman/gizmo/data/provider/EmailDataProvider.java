package sk.lazyman.gizmo.data.provider;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import sk.lazyman.gizmo.data.EmailLog;
import sk.lazyman.gizmo.repository.EmailLogRepository;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author lazyman
 */
public class EmailDataProvider extends SortableDataProvider<EmailLog, String> {

    private EmailLogRepository emailRepository;

    public EmailDataProvider(EmailLogRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    public Iterator<? extends EmailLog> iterator(long first, long count) {
        Sort sort = new Sort(Sort.Direction.DESC, EmailLog.F_DATE);

        PageRequest page = new PageRequest((int) first, (int) count, sort);
        Page<EmailLog> found = emailRepository.findAll(page);
        if (found != null) {
            return found.iterator();
        }

        return new ArrayList<EmailLog>().iterator();
    }

    @Override
    public long size() {
        return emailRepository.count();
    }

    @Override
    public IModel<EmailLog> model(EmailLog object) {
        return new Model<>(object);
    }
}
