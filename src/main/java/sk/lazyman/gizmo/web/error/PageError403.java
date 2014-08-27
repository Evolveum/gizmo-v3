package sk.lazyman.gizmo.web.error;

import org.wicketstuff.annotation.mount.MountPath;

/**
 * @author lazyman
 */
@MountPath("/error/403")
public class PageError403 extends PageError {

    public PageError403() {
        super(403);
    }
}
