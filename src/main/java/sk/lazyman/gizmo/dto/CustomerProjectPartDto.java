package sk.lazyman.gizmo.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author lazyman
 */
public class CustomerProjectPartDto implements Serializable, Comparable<CustomerProjectPartDto> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerProjectPartDto.class);

    private String customerName;
    private String projectName;
    private String partName;

    private Integer customerId;
    private Integer projectId;
    private Integer partId;

    public CustomerProjectPartDto(String customerName, Integer customerId) {
        this(customerName, null, null, customerId, null, null);
    }

    public CustomerProjectPartDto(String customerName, String projectName, Integer projectId) {
        this(customerName, projectName, null, null, projectId, null);
    }

    public CustomerProjectPartDto(String customerName, String projectName, String partName, Integer partId) {
        this(customerName, projectName, partName, null, null, partId);
    }

    public CustomerProjectPartDto(String customerName, String projectName, String partName,
                                  Integer customerId, Integer projectId, Integer partId) {
        this.customerName = customerName;
        this.projectName = projectName;
        this.partName = partName;

        this.customerId = customerId;
        this.projectId = projectId;
        this.partId = partId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getPartName() {
        return partName;
    }

    public Integer getPartId() {
        return partId;
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

        CustomerProjectPartDto that = (CustomerProjectPartDto) o;

        if (customerId != null ? !customerId.equals(that.customerId) : that.customerId != null) return false;
        if (customerName != null ? !customerName.equals(that.customerName) : that.customerName != null) return false;
        if (partId != null ? !partId.equals(that.partId) : that.partId != null) return false;
        if (partName != null ? !partName.equals(that.partName) : that.partName != null) return false;
        if (projectId != null ? !projectId.equals(that.projectId) : that.projectId != null) return false;
        if (projectName != null ? !projectName.equals(that.projectName) : that.projectName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = customerName != null ? customerName.hashCode() : 0;
        result = 31 * result + (projectName != null ? projectName.hashCode() : 0);
        result = 31 * result + (partName != null ? partName.hashCode() : 0);
        result = 31 * result + (customerId != null ? customerId.hashCode() : 0);
        result = 31 * result + (projectId != null ? projectId.hashCode() : 0);
        result = 31 * result + (partId != null ? partId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CustomerProjectPartDto{");
        sb.append("customerName='").append(customerName).append('\'');
        sb.append(", projectName='").append(projectName).append('\'');
        sb.append(", partName='").append(partName).append('\'');
        sb.append(", customerId=").append(customerId);
        sb.append(", projectId=").append(projectId);
        sb.append(", partId=").append(partId);
        sb.append('}');
        return sb.toString();
    }

    //todo improve
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
            switch (array.length) {
                case 3:

                case 2:
                    LOG.trace("Input contain '/' and two parts.");
                    return match(array[0].trim(), array[1].trim(), true);
                default:
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

    //todo improve
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

    //todo improve
    @Override
    public int compareTo(CustomerProjectPartDto o) {
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
