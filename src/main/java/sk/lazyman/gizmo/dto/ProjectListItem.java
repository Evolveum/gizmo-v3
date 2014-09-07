package sk.lazyman.gizmo.dto;

import java.io.Serializable;

/**
 * @author lazyman
 */
public class ProjectListItem<T extends Serializable> implements Serializable {

    public static final String F_NAME = "name";
    public static final String F_DESCRIPTION = "description";
    public static final String F_SELECTED = "selected";

    private T data;
    private boolean selected;

    public ProjectListItem(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return null;
    }

    public String getDescription() {
        return null;
    }
}
