package sk.lazyman.gizmo.dto;

import java.io.Serializable;

/**
 * @author lazyman
 */
public class DashboardProjectDto implements Serializable {

    private String customerName;
    private String projectName;

    private Integer customerId;
    private Integer projectId;

    public DashboardProjectDto(String customerName, Integer customerId) {
        this(customerName, null, customerId, null);
    }

    public DashboardProjectDto(String customerName, String projectName, Integer projectId) {
        this(customerName, projectName, null, projectId);
    }

    public DashboardProjectDto(String customerName, String projectName, Integer customerId, Integer projectId) {
        this.customerName = customerName;
        this.projectName = projectName;
        this.customerId = customerId;
        this.projectId = projectId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProjectName() {
        return projectName;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DashboardProjectDto that = (DashboardProjectDto) o;

        if (customerId != null ? !customerId.equals(that.customerId) : that.customerId != null) return false;
        if (customerName != null ? !customerName.equals(that.customerName) : that.customerName != null) return false;
        if (projectId != null ? !projectId.equals(that.projectId) : that.projectId != null) return false;
        if (projectName != null ? !projectName.equals(that.projectName) : that.projectName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = customerName != null ? customerName.hashCode() : 0;
        result = 31 * result + (projectName != null ? projectName.hashCode() : 0);
        result = 31 * result + (customerId != null ? customerId.hashCode() : 0);
        result = 31 * result + (projectId != null ? projectId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DashboardProjectDto{");
        sb.append("customerName='").append(customerName).append('\'');
        sb.append(", projectName='").append(projectName).append('\'');
        sb.append(", customerId=").append(customerId);
        sb.append(", projectId=").append(projectId);
        sb.append('}');
        return sb.toString();
    }

    public boolean match(String input) {
        if (input == null || input.isEmpty()) {
            return true;
        }

        input = input.toLowerCase().replace('/', ' ').trim();

        if (customerName != null && customerName.toLowerCase().contains(input)) {
            return true;
        }

        if (projectName != null && projectName.toLowerCase().contains(input)) {
            return true;
        }

        return false;
    }
}
