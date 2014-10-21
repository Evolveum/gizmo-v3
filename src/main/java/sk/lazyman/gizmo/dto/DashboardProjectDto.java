package sk.lazyman.gizmo.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author lazyman
 */
public class DashboardProjectDto implements Serializable, Comparable<DashboardProjectDto> {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardProjectDto.class);

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
        LOG.trace("Matching '{}'", input);

        if (input == null || input.isEmpty()) {
            return true;
        }

        input = input.toLowerCase().trim();
        if (!input.contains("/")) {
            LOG.trace("Input doesn't contain '/'.");
            return match(input, input, false);
        } else {
            String[] array = input.split("/");
            if (array.length == 2) {
                LOG.trace("Input contain '/' and two parts.");
                return match(array[0].trim(), array[1].trim(), true);
            } else {
                LOG.trace("Input contain '/'.");
                boolean retVal;
                for (int i = 0; i < array.length; i++) {
                    String item = array[i].trim();
                    if (item.length() == 0) {
                        continue;
                    }

                    retVal = match(item, item, false);
                    if (retVal) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean match(String customer, String project, boolean and) {
        if (and) {
            return customerName != null && customerName.toLowerCase().contains(customer)
                    && projectName != null && projectName.toLowerCase().contains(project);
        }

        if (customerName != null && customerName.toLowerCase().contains(customer)) {
            return true;
        }
        if (projectName != null && projectName.toLowerCase().contains(project)) {
            return true;
        }

        return false;
    }

    @Override
    public int compareTo(DashboardProjectDto o) {
        if (o == null) {
            return 0;
        }

        int val = customerName.compareTo(o.customerName);
        if (val != 0) {
            return val;
        }

        if (projectName == o.projectName) {
            return 0;
        }
        if (projectName == null && o.projectName != null) {
            return -1;
        }
        if (projectName != null && o.projectName == null) {
            return 1;
        }

        return projectName.compareTo(projectName);
    }
}
