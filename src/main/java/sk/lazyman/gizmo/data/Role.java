package sk.lazyman.gizmo.data;

/**
 * @author lazyman
 */
public enum Role {

    EMPLOYEE(1),
    ADMIN(2),
    DELETED(3);

    private int value;

    private Role(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
