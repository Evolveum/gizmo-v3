package sk.lazyman.gizmo.dto;

import org.springframework.security.core.parameters.P;
import sk.lazyman.gizmo.data.User;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportFilterDto implements Serializable {

    public static final String F_SUMMARY = "summary";
    public static final String F_WORKLOG = "worklog";
    public static final String F_PER_USER = "perUser";
//    public static final String F_SHOW_SUMMARY = "showSummary";
    public static final String F_DATE_FROM = "dateFrom";
    public static final String F_DATE_TO = "dateTo";
    public static final String F_CUSTOM_PROJECT_PART = "customerProjectPart";
    public static final String F_PROJECT = "project";
    public static final String F_PROJECTS = "projects";
    public static final String F_REALIZATORS = "realizators";
    public static final String F_WORK_TYPE = "workType";

    private boolean summary;
    private boolean workLog;
    private boolean perUser;

    private LocalDate dateFrom;
    private LocalDate dateTo;
    private List<CustomerProjectPartDto> customerProjectPart;
    private List<User> realizators;
    private WorkType workType = WorkType.ALL;

    public boolean isSummary() {
        return summary;
    }

    public void setSummary(boolean summary) {
        this.summary = summary;
    }

    public boolean isWorkLog() {
        return workLog;
    }

    public void setWorkLog(boolean workLog) {
        this.workLog = workLog;
    }

    public boolean isPerUser() {
        return perUser;
    }

    public void setPerUser(boolean perUser) {
        this.perUser = perUser;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public List<CustomerProjectPartDto> getCustomerProjectPartDtos() {
        if (customerProjectPart == null) {
            customerProjectPart = new ArrayList<>();
        }
        return customerProjectPart;
    }

    public void setCustomerProjectPartDtos(List<CustomerProjectPartDto> customerProjectPartDtos) {
        this.customerProjectPart = customerProjectPartDtos;
    }

    public CustomerProjectPartDto getProject() {
        List<CustomerProjectPartDto> projects = getCustomerProjectPartDtos();
        if (projects.isEmpty()) {
            return null;
        }

        return projects.get(0);
    }



    public void setProject(CustomerProjectPartDto project) {
        List<CustomerProjectPartDto> projects = getCustomerProjectPartDtos();
        projects.clear();

        if (project != null) {
            projects.add(project);
        }
    }


    public List<User> getRealizators() {
        if (realizators == null) {
            realizators = new ArrayList<>();
        }
        return realizators;
    }

    public void setRealizators(List<User> realizators) {
        this.realizators = realizators;
    }

    public WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }

    public String getMonth() {
        return dateFrom.getMonth().getDisplayName(TextStyle.FULL, Locale.US);
    }
}
