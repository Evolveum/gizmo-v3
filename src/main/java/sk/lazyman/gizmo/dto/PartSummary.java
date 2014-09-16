package sk.lazyman.gizmo.dto;

/**
 * @author lazyman
 */
public class PartSummary extends TaskLength implements Comparable<PartSummary> {

    public static final String F_NAME = "name";
    private String name;

    public PartSummary(String name, double length, double invoice) {
        super(length, invoice);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(PartSummary o) {
        if (o == null) {
            return 0;
        }

        return String.CASE_INSENSITIVE_ORDER.compare(name, o.getName());
    }
}
