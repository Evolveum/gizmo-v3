package sk.lazyman.gizmo.dto;

import sk.lazyman.gizmo.data.User;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lazyman
 */
public class EmailFilterDto implements Serializable {

    public static final String F_FROM = "from";
    public static final String F_TO = "to";
    public static final String F_SENDER = "sender";

    private Date from;
    private Date to;
    private User sender;

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

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }
}
