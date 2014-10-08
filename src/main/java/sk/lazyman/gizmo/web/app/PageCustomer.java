package sk.lazyman.gizmo.web.app;

import org.wicketstuff.annotation.mount.MountPath;

/**
 * @author lazyman
 */
@MountPath("/app/customer")
public class PageCustomer extends PageAppTemplate {

    public static final String CUSTOMER_ID = "customerId";

    public PageCustomer() {
        initLayout();
    }

    private void initLayout() {

    }
}
