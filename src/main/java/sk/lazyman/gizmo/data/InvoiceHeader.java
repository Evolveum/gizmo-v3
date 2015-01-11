package sk.lazyman.gizmo.data;

import sk.lazyman.gizmo.util.GizmoUtils;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author lazyman
 */
//@Entity
public class InvoiceHeader implements Serializable {

    private Integer id;
    private String name;
    private String header;
    private byte[] logo;

    @Column(nullable = true, length = GizmoUtils.DESCRIPTION_SIZE)
    public String getHeader() {
        return header;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "header_id")
    @SequenceGenerator(name = "header_id", sequenceName = "g_header_id_seq", allocationSize = 1, initialValue = 40000)
    public Integer getId() {
        return id;
    }

    @Column(nullable = true)
    public byte[] getLogo() {
        return logo;
    }

    @Column(unique = true)
    public String getName() {
        return name;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvoiceHeader that = (InvoiceHeader) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "InvoiceHeader{" +
                "header='" + header + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", logo=" + (logo != null ? "[data]" : null) +
                '}';
    }
}
