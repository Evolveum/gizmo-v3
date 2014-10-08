package sk.lazyman.gizmo.data.provider;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;
import org.springframework.data.jpa.repository.JpaRepository;
import sk.lazyman.gizmo.data.EmailLog;
import sk.lazyman.gizmo.data.QEmailLog;
import sk.lazyman.gizmo.dto.EmailFilterDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
public class EmailDataProvider extends BasicDataProvider<EmailLog> {

    private EmailFilterDto filter;

    public EmailDataProvider(JpaRepository<EmailLog, Integer> repository, int itemsPerPage) {
        super(repository, itemsPerPage);
    }

    public void setFilter(EmailFilterDto filter) {
        this.filter = filter;
    }

    @Override
    public Predicate getPredicate() {
        if (filter == null) {
            return null;
        }

        List<Predicate> list = new ArrayList<>();
        if (filter.getFrom() != null) {
            list.add(QEmailLog.emailLog.sentDate.goe(filter.getFrom()));
        }

        if (filter.getTo() != null) {
            list.add(QEmailLog.emailLog.sentDate.loe(filter.getTo()));
        }

        if (filter.getSender() != null) {
            list.add(QEmailLog.emailLog.sender.eq(filter.getSender()));
        }

        if (list.isEmpty()) {
            return null;
        }

        BooleanBuilder bb = new BooleanBuilder();
        return bb.orAllOf(list.toArray(new Predicate[list.size()]));
    }
}
