package sk.lazyman.gizmo.component;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

/**
 * @author lazyman
 */
public class VisibleEnableBehaviour extends Behavior {

    public boolean isVisible() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onConfigure(Component component) {
        component.setEnabled(isEnabled());

        boolean visible = isVisible();
        component.setVisible(visible);
        component.setVisibilityAllowed(visible);
    }
}
