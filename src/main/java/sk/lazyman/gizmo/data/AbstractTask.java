package sk.lazyman.gizmo.data;

import sk.lazyman.gizmo.util.GizmoUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author lazyman
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class AbstractTask implements Serializable {

    public static final String F_ID = "id";
    public static final String F_REALIZATOR = "realizator";
    public static final String F_WORK_LENGTH = "workLength";
    public static final String F_DATE = "date";
    public static final String F_DESCRIPTION = "description";
    public static final String F_TRACK_ID = "trackId";
    public static final String F_TYPE = "type";

    private Integer id;

    private TaskType type;

    private User realizator;
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
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_abstractTask_user"))
    public User getRealizator() {
        return realizator;
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

    @Enumerated
    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRealizator(User realizator) {
        this.realizator = realizator;
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

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractTask task = (AbstractTask) o;

        if (id != null ? !id.equals(task.id) : task.id != null) return false;

        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AbstractTask{");
        sb.append("id=").append(id);
        sb.append(", realizator=").append(realizator);
        sb.append(", workLength=").append(workLength);
        sb.append(", date=").append(date);
        sb.append(", description='").append(description).append('\'');
        sb.append(", trackId='").append(trackId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
