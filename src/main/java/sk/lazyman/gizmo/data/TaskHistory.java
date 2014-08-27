package sk.lazyman.gizmo.data;

import java.util.Date;

public class TaskHistory {

    private Long id;

    private ToDoTask task;

    private String state;

    private Date deadline;

    private String description;

    private User author;

    private User assignee;

    private Date creationDate;

    private String priority;

    public User getAuthor() {
        return author;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPriority() {
        return priority;
    }

    public ToDoTask getTask() {
        return task;
    }

    public void setTask(ToDoTask task) {
        this.task = task;
    }
}