package sk.lazyman.gizmo.dto;

import sk.lazyman.gizmo.data.Company;

import java.io.Serializable;

/**
 * @author lazyman
 */
public class CompanyListItem implements Serializable {

    public static final String F_NAME = "company.name";
    public static final String F_DESCRIPTION = "company.description";
    public static final String F_SELECTED = "selected";

    private Company company;
    private boolean selected;

    public CompanyListItem(Company company) {
        this.company = company;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
