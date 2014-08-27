package sk.lazyman.gizmo.data;

import java.util.Date;

public class Tasks {

    private long id;
    private double taskLength;
    private double invoiceLength;
    private Date date;
    private String desc;
    private ProjectPart projectPart;
    private User realizator;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getTaskLength() {
        return taskLength;
    }

    public void setTaskLength(double taskLength) {
        this.taskLength = taskLength;
    }

    public double getInvoiceLength() {
        return invoiceLength;
    }

    public void setInvoiceLength(double invoiceLength) {
        this.invoiceLength = invoiceLength;
    }

    public Date getDate() {
        return date;
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

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof Tasks)) {
            return false;
        } else {
            Tasks tasks = (Tasks) obj;
            return (this.getId() == tasks.getId());
        }
    }
}