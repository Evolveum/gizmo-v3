package sk.lazyman.gizmo.web.error;


import org.wicketstuff.annotation.mount.MountPath;

/**
 * @author lazyman
 */
@MountPath("/error/401")
public class PageError401 extends PageError {

    public PageError401() {
        super(401);
    }
}
