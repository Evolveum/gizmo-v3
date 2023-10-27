/*
 * Copyright 2015 Viliam Repan (lazyman)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sk.lazyman.gizmo.data;

import sk.lazyman.gizmo.util.GizmoUtils;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
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
    public static final String F_CUSTOMER_LIST = "customerList";
    public static final String F_DESCRIPTION = "description";

    private Integer id;

    // about mail
    private User sender;
    private LocalDate sentDate;
    private String mailTo;
    private String mailCc;
    private String mailBcc;
    private boolean successful;
    // about selection filter, data in mail
    private LocalDate fromDate;
    private LocalDate toDate;
    private Set<User> realizatorList;
    private Set<Project> projectList;
    private Set<Customer> customerList;
    private double summaryWork;
    private double summaryInvoice;
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_log_id")
    @SequenceGenerator(name = "email_log_id", sequenceName = "g_email_log_id_seq", allocationSize = 1, initialValue = 40000)
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
    public LocalDate getSentDate() {
        return sentDate;
    }

    @Column(nullable = false)
    public boolean isSuccessful() {
        return successful;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = {
                    @JoinColumn(name = "log_id",
                            foreignKey = @ForeignKey(name = "fk_emailLogUser_log"))},
            inverseJoinColumns = {
                    @JoinColumn(name = "user_id",
                            foreignKey = @ForeignKey(name = "fk_emailLogUser_realizator"))})
    public Set<User> getRealizatorList() {
        return realizatorList;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    public Set<Project> getProjectList() {
        return projectList;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    public Set<Customer> getCustomerList() {
        return customerList;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
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

    public void setSentDate(LocalDate sentDate) {
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

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(LocalDate toDate) {
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

    public void setCustomerList(Set<Customer> customerList) {
        this.customerList = customerList;
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
        builder.append(", sender: " + (sender != null ? sender.getFullName() : null));
        builder.append(", mail toDate: " + mailTo);
        builder.append(", mail cc: " + mailCc);
        builder.append(", mail bcc: " + mailBcc + "\n");

        builder.append("fromDate: " + fromDate);
        builder.append(", toDate: " + toDate);
        builder.append(", summary work: " + summaryWork);
        builder.append(", summary invoice: " + summaryInvoice + "\n");

        builder.append("realizator list: ");
        if (realizatorList != null) {
            for (User user : realizatorList) {
                builder.append(user.getFullName() + "; ");
            }
        }
        builder.append("\n");

        builder.append("project list: ");
        if (projectList != null) {
            for (Project project : projectList) {
                builder.append(project.getName() + "; ");
            }
        }

        builder.append("customer list: ");
        if (customerList != null) {
            for (Customer customer : customerList) {
                builder.append(customer.getName() + "; ");
            }
        }

        return builder.toString();
    }
}
