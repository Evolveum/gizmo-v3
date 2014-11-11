package sk.lazyman.gizmo.dto;

import sk.lazyman.gizmo.data.User;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lazyman
 */
public class WorkFilterDto implements Serializable {

    public static final String F_FROM = "from";
    public static final String F_TO = "to";
    public static final String F_PROJECT = "project";
    public static final String F_REALIZATOR = "realizator";
    public static final String F_TYPE = "type";

    private Date from;
    private Date to;
    private CustomerProjectPartDto project;
    private User realizator;
    private WorkType type = WorkType.ALL;

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public CustomerProjectPartDto getProject() {
        return project;
    }

    public void setProject(CustomerProjectPartDto project) {
        this.project = project;
    }

    public User getRealizator() {
        return realizator;
    }

    public void setRealizator(User realizator) {
        this.realizator = realizator;
    }

    public WorkType getType() {
        return type;
    }

    public void setType(WorkType type) {
        if (type == null) {
            type = WorkType.ALL;
        }
        this.type = type;
    }
}
