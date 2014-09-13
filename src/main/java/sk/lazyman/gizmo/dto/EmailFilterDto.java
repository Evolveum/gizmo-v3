package sk.lazyman.gizmo.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lazyman
 */
public class EmailFilterDto implements Serializable {

    public static final String F_FROM = "from";
    public static final String F_TO = "to";

    private Date from;
    private Date to;

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
}
