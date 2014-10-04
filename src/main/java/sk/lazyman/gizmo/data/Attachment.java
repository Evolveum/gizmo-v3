package sk.lazyman.gizmo.data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author lazyman
 */
@Entity
public class Attachment implements Serializable {

    public static final String F_ID = "id";
    public static final String F_NAME = "name";
    public static final String F_VALUE = "value";
    public static final String F_WORK = "work";

    private Integer id;
    private String name;
    private byte[] value;
    private Work work;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_id")
    @SequenceGenerator(name = "attachment_id", sequenceName = "g_attachment_id_seq")
    public Integer getId() {
        return id;
    }

    @ManyToOne
    public Work getWork() {
        return work;
    }

    public String getName() {
        return name;
    }

    public byte[] getValue() {
        return value;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attachment that = (Attachment) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Attachment{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", work='").append(work).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
