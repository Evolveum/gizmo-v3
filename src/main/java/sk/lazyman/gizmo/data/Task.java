package sk.lazyman.gizmo.data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author lazyman
 */
@Entity
@Table(name = "tasks")
public class Task implements Serializable {

    public static final String F_DATE = "date";
    public static final String F_DESC = "desc";

    private Integer id;
    private ProjectPart projectPart;
    private User realizator;
    private double invoiceLength;
    private double taskLength;
    private Date date;
    private String desc;
    private Boolean milestone;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_id")
    @SequenceGenerator(name = "task_id", sequenceName = "tasks_id_seq")
    public Integer getId() {
        return id;
    }

    @ManyToOne
    @JoinColumn(name = "part_id")
    public ProjectPart getProjectPart() {
        return projectPart;
    }

    @ManyToOne
    @JoinColumn(name = "realizator_id")
    public User getRealizator() {
        return realizator;
    }

    @Column(name = "invoice")
    public double getInvoiceLength() {
        return invoiceLength;
    }

    @Column(name = "length")
    public double getTaskLength() {
        return taskLength;
    }

    public Date getDate() {
        return date;
    }

    @Column(name = "description", length = 1024)
    public String getDesc() {
        return desc;
    }

    public Boolean getMilestone() {
        return milestone;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProjectPart(ProjectPart projectPart) {
        this.projectPart = projectPart;
    }

    public void setRealizator(User realizator) {
        this.realizator = realizator;
    }

    public void setInvoiceLength(double invoiceLength) {
        this.invoiceLength = invoiceLength;
    }

    public void setTaskLength(double taskLength) {
        this.taskLength = taskLength;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setMilestone(Boolean milestone) {
        this.milestone = milestone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (id != null ? !id.equals(task.id) : task.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id=" + id);
        builder.append(", part=[" + projectPart);
        builder.append("], realizator=[" + realizator);
        builder.append("], length= " + taskLength);
        builder.append(", invoice=" + invoiceLength);
        builder.append(", desc=" + desc);
        builder.append(", date=" + date);

        return builder.toString();
    }
}
