package sk.lazyman.gizmo.data;

import java.io.Serializable;

/**
 * @author mamut
 */
public class Project implements Serializable {

    private Long id;

    private String name;

    private Company customer;

    private String desc;

    private boolean closed;

    private boolean commercial;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isCommercial() {
        return commercial;
    }

    public void setCommercial(boolean commercial) {
        this.commercial = commercial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Company getCustomer() {
        return customer;
    }

    public void setCustomer(Company customer) {
        this.customer = customer;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id=" + id);
        builder.append(", name=" + name);
        builder.append(", customer=[" + customer);
        builder.append("], desc= " + desc);

        return builder.toString();
    }
}
