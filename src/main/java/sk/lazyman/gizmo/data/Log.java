package sk.lazyman.gizmo.data;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.Set;

/**
 * @author lazyman
 */
@Entity
//@ForeignKey(name = "g_fk_log")
public class Log extends AbstractTask {

    public static final String F_CUSTOMER = "customer";

    private Customer customer;
    private Set<Attachment> attachments;

    @ManyToOne
    public Customer getCustomer() {
        return customer;
    }

    @OneToMany(mappedBy = Attachment.F_WORK)
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
