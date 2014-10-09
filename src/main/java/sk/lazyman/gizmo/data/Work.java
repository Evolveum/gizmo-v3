package sk.lazyman.gizmo.data;

import sk.lazyman.gizmo.util.GizmoUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author lazyman
 */
@Entity
public class Work implements Serializable {

    public static final String F_ID = "id";
    public static final String F_CUSTOMER = "customer";
    public static final String F_PART = "part";
    public static final String F_REALIZATOR = "realizator";
    public static final String F_INVOICE_LENGTH = "invoiceLength";
    public static final String F_WORK_LENGTH = "workLength";
    public static final String F_DATE = "date";
    public static final String F_DESCRIPTION = "description";
    public static final String F_TRACK_ID = "trackId";

    private Integer id;
    //if it's log
    private Customer customer;
    //if it's work
    private Part part;

    private Set<Attachment> attachments;

    private User realizator;
    private double invoiceLength;
    private double workLength;
    private Date date;
    private String description;
    private String trackId;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_id")
    @SequenceGenerator(name = "task_id", sequenceName = "g_task_id_seq")
    public Integer getId() {
        return id;
    }

    @ManyToOne
    public Customer getCustomer() {
        return customer;
    }

    @ManyToOne
    public Part getPart() {
        return part;
    }

    @ManyToOne
    public User getRealizator() {
        return realizator;
    }

    @OneToMany(mappedBy = Attachment.F_WORK)
    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public double getInvoiceLength() {
        return invoiceLength;
    }

    public double getWorkLength() {
        return workLength;
    }

    public Date getDate() {
        return date;
    }

    @Column(length = GizmoUtils.DESCRIPTION_SIZE)
    public String getDescription() {
        return description;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public void setRealizator(User realizator) {
        this.realizator = realizator;
    }

    public void setInvoiceLength(double invoiceLength) {
        this.invoiceLength = invoiceLength;
    }

    public void setWorkLength(double workLength) {
        this.workLength = workLength;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Work work = (Work) o;

        if (id != null ? !id.equals(work.id) : work.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Work{");
        sb.append("id=").append(id);
        sb.append(", customer=").append(customer);
        sb.append(", part=").append(part);
        sb.append(", realizator=").append(realizator);
        sb.append(", invoiceLength=").append(invoiceLength);
        sb.append(", workLength=").append(workLength);
        sb.append(", date=").append(date);
        sb.append(", trackId='").append(trackId).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", attachments='").append(attachments).append('\'');
        sb.append('}');
        return sb.toString();
    }
}