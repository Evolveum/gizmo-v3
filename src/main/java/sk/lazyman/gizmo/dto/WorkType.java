package sk.lazyman.gizmo.dto;

import sk.lazyman.gizmo.data.TaskType;

/**
 * @author lazyman
 */
public enum WorkType {

    ALL(null),

    WORK(TaskType.WORK),

    LOG(TaskType.LOG);

    private TaskType type;

    WorkType(TaskType type) {
        this.type = type;
    }

    public TaskType getType() {
        return type;
    }
}
