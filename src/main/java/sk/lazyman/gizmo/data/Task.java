package sk.lazyman.gizmo.data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author mamut
 */
public class Task {

    private long id;

    private ProjectPart projectPart;

    private User realizator;

    private double invoiceLength;

    private double taskLength;

    private Date date;

    private String desc;

    private Boolean milestone;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProjectPart getProjectPart() {
        return projectPart;
    }

    public void setProjectPart(ProjectPart projectPart) {
        this.projectPart = projectPart;
    }

    public User getRealizator() {
        return realizator;
    }

    public void setRealizator(User realizator) {
        this.realizator = realizator;
    }

    public double getInvoiceLength() {
        return invoiceLength;
    }

    public void setInvoiceLength(double invoiceLength) {
        this.invoiceLength = invoiceLength;
    }

    public double getTaskLength() {
        return taskLength;
    }

    public void setTaskLength(double taskLength) {
        this.taskLength = taskLength;
    }

    public Date getDate() {
        return date;
    }

    public String getDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("E dd.MMM.yyyy");
        return sdf.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getMilestone() {
        return milestone;
    }

    public void setMilestone(Boolean milestone) {
        this.milestone = milestone;
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
