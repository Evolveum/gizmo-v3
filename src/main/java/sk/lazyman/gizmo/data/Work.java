package sk.lazyman.gizmo.data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author lazyman
 */
@Entity
//javax.persistence.ForeignKey(name = "g_fk_work")
public class Work extends AbstractTask {

    public static final String F_PART = "part";
    public static final String F_INVOICE_LENGTH = "invoiceLength";

    private Part part;
    private double invoiceLength;

    @ManyToOne
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
