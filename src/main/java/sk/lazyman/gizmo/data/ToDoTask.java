package sk.lazyman.gizmo.data;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class ToDoTask {

    private Long id;

    private String name;

    private String description;

    private User author;

    private User assignee;

    private String state;

    private String priority;

    private Date deadline;

    private Date created;

    private List<TaskHistory> history;

    private Set<Notify> notifyList;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public User getAuthor() {
        return author;
    }

    public Date getDeadline() {
        return deadline;
    }

    public String getDescription() {
        return description;
    }

    public List<TaskHistory> getHistory() {
        return history;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHistory(List<TaskHistory> history) {
        this.history = history;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(String state) {
        this.state = state;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set getNotifyList() {
        return notifyList;
    }

    public void setNotifyList(Set<Notify> notifyList) {
        this.notifyList = notifyList;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStateName(int val) {
        switch (val) {
            case 0:
                return "New";
            case 1:
                return "Assigned";
            case 2:
                return "Resolved";
            case 3:
                return "Closed";
            default:
                return "Unknown";
        }
    }
}