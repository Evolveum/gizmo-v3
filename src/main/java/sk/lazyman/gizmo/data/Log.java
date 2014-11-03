package sk.lazyman.gizmo.data;

import javax.persistence.*;
import java.util.Set;

/**
 * @author lazyman
 */
@Entity
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "fk_log_abstractTask"))
public class Log extends AbstractTask {

    public static final String F_CUSTOMER = "customer";

    private Customer customer;
    private Set<Attachment> attachments;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_log_customer"))
    public Customer getCustomer() {
        return customer;
    }

    @OneToMany(mappedBy = Attachment.F_LOG)
    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Log{");
        sb.append("customer=").append(customer);
        sb.append(", attachments=").append(attachments);
        sb.append('}');
        return sb.toString();
    }
}
