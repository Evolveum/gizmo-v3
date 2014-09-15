package sk.lazyman.gizmo.dto;

/**
 * @author lazyman
 */
public class PartSummary extends TaskLength {

    public static final String F_NAME = "name";
    private String name;

    public PartSummary(String name, double length, double invoice) {
        super(length, invoice);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
