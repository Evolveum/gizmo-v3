package sk.lazyman.gizmo.data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author lazyman
 */
@Entity
@Table(name = "EmailLog")
public class EmailLog implements Serializable {

    public static final String F_DATE = "date";
    public static final String F_SUCCESSFUL = "successful";
    public static final String F_MAIL_TO = "mailTo";
    public static final String F_FROM = "from";
    public static final String F_TO = "to";
    public static final String F_SUMMARY_WORK = "summaryWork";
    public static final String F_SUMMARY_INVOICE = "summaryInvoice";
    public static final String F_REALIZATOR_LIST = "realizatorList";
    public static final String F_PROJECT_LIST = "projectList";

    private Integer id;

    // about mail
    private User sender;
    private Date date;
    private String mailTo;
    private String mailCc;
    private String mailBcc;
    private boolean successful;
    // about selection filter, data in mail
    private Date from;
    private Date to;
    private Set<User> realizatorList;
    private Set<Project> projectList;
    private double summaryWork;
    private double summaryInvoice;
    private String comment;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_log_id")
    @SequenceGenerator(name = "email_log_id", sequenceName = "email_log_id_seq")
    public Integer getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    @ManyToOne
    @JoinColumn(name = "sender")
    public User getSender() {
        return sender;
    }

    @Column(name = "sentDate")
    public Date getDate() {
        return date;
    }

    @Column(nullable = false)
    public boolean isSuccessful() {
        return successful;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "EmailLog_realizator",
            joinColumns = {@JoinColumn(name = "log_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "user_id", nullable = false, updatable = false)})
    public Set<User> getRealizatorList() {
        return realizatorList;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "EmailLog_project",
            joinColumns = {@JoinColumn(name = "log_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "project_id", nullable = false, updatable = false)})
    public Set<Project> getProjectList() {
        return projectList;
    }

    @Column(name = "fromDate")
    public Date getFrom() {
        return from;
    }

    @Column(name = "toDate")
    public Date getTo() {
        return to;
    }

    public String getMailBcc() {
        return mailBcc;
    }

    public String getMailCc() {
        return mailCc;
    }

    public String getMailTo() {
        return mailTo;
    }

    public double getSummaryInvoice() {
        return summaryInvoice;
    }

    public double getSummaryWork() {
        return summaryWork;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public void setMailCc(String mailCc) {
        this.mailCc = mailCc;
    }

    public void setMailBcc(String mailBcc) {
        this.mailBcc = mailBcc;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public void setRealizatorList(Set<User> realizatorList) {
        this.realizatorList = realizatorList;
    }

    public void setProjectList(Set<Project> projectList) {
        this.projectList = projectList;
    }

    public void setSummaryWork(double summaryWork) {
        this.summaryWork = summaryWork;
    }

    public void setSummaryInvoice(double summaryInvoice) {
        this.summaryInvoice = summaryInvoice;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailLog emailLog = (EmailLog) o;

        if (id != null ? !id.equals(emailLog.id) : emailLog.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("date: " + date);
        builder.append(", sender: " + sender.getFullName());
        builder.append(", mail to: " + mailTo);
        builder.append(", mail cc: " + mailCc);
        builder.append(", mail bcc: " + mailBcc + "\n");

        builder.append("from: " + from);
        builder.append(", to: " + to);
        builder.append(", summary work: " + summaryWork);
        builder.append(", summary invoice: " + summaryInvoice + "\n");

        builder.append("realizator list: ");
        for (User user : realizatorList) {
            builder.append(user.getFullName() + "; ");
        }
        builder.append("\n");

        builder.append("project list: ");
        for (Project project : projectList) {
            builder.append(project.getName() + "; ");
        }

        return builder.toString();
    }
}
