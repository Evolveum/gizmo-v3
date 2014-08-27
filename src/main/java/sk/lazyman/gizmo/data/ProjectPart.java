package sk.lazyman.gizmo.data;

import java.io.Serializable;

public class ProjectPart implements Serializable {

    private Long id;

    private Project project;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName(String delimiter) {
        StringBuffer retval = new StringBuffer();
        if (this.getProject() != null) {
            if (this.getProject().getCustomer() != null && this.getProject().getCustomer().getName() != null) {
                retval.append(this.getProject().getCustomer().getName());
                retval.append(delimiter);
            }
            if (this.getProject().getName() != null) {
                retval.append(this.getProject().getName());
                retval.append(delimiter);
            }
        }
        if (this.getName() != null) {
            retval.append(this.getName());
        }
        return retval.toString();
    }

    public String getFullName() {
        return getFullName(" - ");
    }

    public String getFullNameBr() {
        return getFullName("<br />");
    }

    public String getNameProject() {
        StringBuffer retval = new StringBuffer();
        if (this.getProject() != null) {
            if (this.getProject().getName() != null) {
                retval.append(this.getProject().getName());
                retval.append(" ");
            }
        }
        if (this.getName() != null) {
            retval.append(this.getName());
        }
        return retval.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id=" + id);
        builder.append(", name=" + name);
        builder.append("project=[" + getFullName() + "]");

        return builder.toString();
    }
}
