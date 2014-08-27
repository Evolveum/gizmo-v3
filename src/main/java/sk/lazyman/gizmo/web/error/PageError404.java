package sk.lazyman.gizmo.web.error;

import org.wicketstuff.annotation.mount.MountPath;

/**
 * @author lazyman
 */
@MountPath("/error/404")
public class PageError404 extends PageError {

    public PageError404() {
        super(404);
    }
}
