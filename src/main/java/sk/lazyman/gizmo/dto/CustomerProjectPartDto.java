package sk.lazyman.gizmo.dto;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
public class CustomerProjectPartDto implements Serializable, Comparable<CustomerProjectPartDto> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerProjectPartDto.class);

    public static final String F_CUSTOMER_NAME = "customerName";
    public static final String F_PROJECT_NAME = "projectName";
    public static final String F_PART_NAME = "partName";
    public static final String F_CUSTOMER_ID = "customerId";
    public static final String F_PROJECT_ID = "projectId";
    public static final String F_PART_ID = "partId";

    private String customerName;
    private String projectName;
    private String partName;

    private Integer customerId;
    private Integer projectId;
    private Integer partId;

    public CustomerProjectPartDto() {
    }

    public CustomerProjectPartDto(String customerName, Integer customerId) {
        this(customerName, null, null, customerId, null, null);
    }

    public CustomerProjectPartDto(String customerName, String projectName, Integer customerId, Integer projectId) {
        this(customerName, projectName, null, customerId, projectId, null);
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

    public String getDescription() {
        List<String> values = new ArrayList<>();
        if (StringUtils.isNotEmpty(customerName)) {
            values.add(customerName);
        }
        if (StringUtils.isNotEmpty(projectName)) {
            values.add(projectName);
        }
        if (StringUtils.isNotEmpty(partName)) {
            values.add(partName);
        }

        return StringUtils.join(values, " - ");
    }

    public boolean match(String input) {
        if (input == null || input.isEmpty()) {
            return true;
        }

        input = input.toLowerCase().trim();
        if (!input.contains("/")) {
            return matchCustomerProject(input, input, false) || matchProjectPart(input, input, false);
        } else {
            String[] array = input.split("/");
            switch (array.length) {
                case 3:
                    return matchCustomerProject(array[0].trim(), array[1].trim(), true)
                            && matchProjectPart(array[1].trim(), array[2].trim(), true);
                case 2:
                    return matchCustomerProject(array[0].trim(), array[1].trim(), true)
                            || matchProjectPart(array[0].trim(), array[1].trim(), true);
                default:
                    boolean retVal;
                    for (int i = 0; i < array.length; i++) {
                        String item = array[i].trim();
                        if (item.length() == 0) {
                            continue;
                        }

                        retVal = matchCustomerProject(item, item, false);
                        if (retVal) {
                            return true;
                        }
                    }
            }
        }

        return false;
    }

    private boolean matchProjectPart(String project, String part, boolean and) {
        if (and) {
            return projectName != null && projectName.toLowerCase().contains(project)
                    && partName != null && partName.toLowerCase().contains(part);
        }

        if (projectName != null && projectName.toLowerCase().contains(project)) {
            return true;
        }
        if (partName != null && partName.toLowerCase().contains(part)) {
            return true;
        }

        return false;
    }

    private boolean matchCustomerProject(String customer, String project, boolean and) {
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
    public int compareTo(CustomerProjectPartDto o) {
        if (o == null) {
            return 0;
        }

        int val = customerName.compareTo(o.customerName);
        if (val != 0) {
            return val;
        }

        if (projectName == null && o.projectName != null) {
            return -1;
        }
        if (projectName != null && o.projectName == null) {
            return 1;
        }

        val = projectName != null ? projectName.compareTo(o.projectName) : 0;
        if (val != 0) {
            return val;
        }

        if (partName == o.partName) {
            return 0;
        }

        if (partName == null && o.partName != null) {
            return -1;
        }
        if (partName != null && o.partName == null) {
            return 1;
        }

        return partName.compareTo(o.partName);
    }
}
