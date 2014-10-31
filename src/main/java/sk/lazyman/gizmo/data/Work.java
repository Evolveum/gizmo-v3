package sk.lazyman.gizmo.data;

import com.mysema.query.annotations.QueryInit;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author lazyman
 */
@Entity
//@PrimaryKeyJoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_work_abstractTask"))
public class Work extends AbstractTask {

    public static final String F_PART = "part";
    public static final String F_INVOICE_LENGTH = "invoiceLength";

    private Part part;
    private double invoiceLength;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_work_part"))
    @QueryInit("project.*")
    public Part getPart() {
        return part;
    }

    public double getInvoiceLength() {
        return invoiceLength;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public void setInvoiceLength(double invoiceLength) {
        this.invoiceLength = invoiceLength;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Work{");
        sb.append("part=").append(part);
        sb.append(", invoiceLength=").append(invoiceLength);
        sb.append('}');
        return sb.toString();
    }
}
