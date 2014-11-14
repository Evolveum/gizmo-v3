package sk.lazyman.gizmo.data;

import sk.lazyman.gizmo.util.GizmoUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author lazyman
 */
@Entity
public class EmailLog implements Serializable {

    public static final String F_ID = "id";
    public static final String F_SENDER = "sender";
    public static final String F_SENT_DATE = "sentDate";
    public static final String F_SUCCESSFUL = "successful";
    public static final String F_MAIL_TO = "mailTo";
    public static final String F_MAIL_CC = "mailCc";
    public static final String F_MAIL_BCC = "mailBcc";
    public static final String F_FROM_DATE = "fromDate";
    public static final String F_TO_DATE = "toDate";
    public static final String F_SUMMARY_WORK = "summaryWork";
    public static final String F_SUMMARY_INVOICE = "summaryInvoice";
    public static final String F_REALIZATOR_LIST = "realizatorList";
    public static final String F_PROJECT_LIST = "projectList";
    public static final String F_DESCRIPTION = "description";

    private Integer id;

    // about mail
    private User sender;
    private Date sentDate;
    private String mailTo;
    private String mailCc;
    private String mailBcc;
    private boolean successful;
    // about selection filter, data in mail
    private Date fromDate;
    private Date toDate;
    private Set<User> realizatorList;
    private Set<Project> projectList;
    private double summaryWork;
    private double summaryInvoice;
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_log_id")
    @SequenceGenerator(name = "email_log_id", sequenceName = "g_email_log_id_seq")
    public Integer getId() {
        return id;
    }

    @Column(length = GizmoUtils.DESCRIPTION_SIZE)
    public String getDescription() {
        return description;
    }

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_emaillog_user"))
    public User getSender() {
        return sender;
    }

    @Column(nullable = false)
    public Date getSentDate() {
        return sentDate;
    }

    @Column(nullable = false)
    public boolean isSuccessful() {
        return successful;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = {
                    @JoinColumn(name = "log_id",// nullable = false, updatable = false,
                            foreignKey = @ForeignKey(name = "fk_emailLogUser_log"))},
            inverseJoinColumns = {
                    @JoinColumn(name = "user_id",// nullable = false, updatable = false,
                            foreignKey = @ForeignKey(name = "fk_emailLogUser_realizator"))})
    public Set<User> getRealizatorList() {
        return realizatorList;
    }

    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(joinColumns = {@JoinColumn(name = "log_id", nullable = false, updatable = false)},
//            inverseJoinColumns = {@JoinColumn(name = "project_id", nullable = false, updatable = false)})
    public Set<Project> getProjectList() {
        return projectList;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
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

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
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

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
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

    public void setDescription(String description) {
        this.description = description;
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

        builder.append("sentDate: " + sentDate);
        builder.append(", sender: " + sender.getFullName());
        builder.append(", mail toDate: " + mailTo);
        builder.append(", mail cc: " + mailCc);
        builder.append(", mail bcc: " + mailBcc + "\n");

        builder.append("fromDate: " + fromDate);
        builder.append(", toDate: " + toDate);
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
