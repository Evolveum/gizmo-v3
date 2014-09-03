package sk.lazyman.gizmo.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author lazyman
 */
public class EmailLog {

    private long id;

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

    private List<User> realizatorList;

    private List<Project> projectList;

    private double summaryWork;

    private double summaryInvoice;

    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User user) {
        this.sender = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public List<User> getRealizatorList() {
        return realizatorList;
    }

    public void setRealizatorList(List<User> realizatorList) {
        this.realizatorList = realizatorList;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

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

    public String getMailBcc() {
        return mailBcc;
    }

    public String getMailCc() {
        return mailCc;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailBcc(String mailBcc) {
        this.mailBcc = mailBcc;
    }

    public void setMailCc(String mailCc) {
        this.mailCc = mailCc;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getFormattedDate() {
        DateFormat sdf = new SimpleDateFormat("dd. MMM. yyyy, HH:mm:ss");

        return sdf.format(date);
    }

    public String getFormattedFromDate() {
        DateFormat sdf = new SimpleDateFormat("dd. MMM. yyyy");

        return sdf.format(from);
    }

    public String getFormattedToDate() {
        DateFormat sdf = new SimpleDateFormat("dd. MMM. yyyy");

        return sdf.format(to);
    }

    public double getSummaryInvoice() {
        return summaryInvoice;
    }

    public void setSummaryInvoice(double summaryInvoice) {
        this.summaryInvoice = summaryInvoice;
    }

    public double getSummaryWork() {
        return summaryWork;
    }

    public void setSummaryWork(double summaryWork) {
        this.summaryWork = summaryWork;
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
