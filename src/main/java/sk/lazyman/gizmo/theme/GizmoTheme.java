package sk.lazyman.gizmo.theme;

import de.agilecoders.wicket.core.settings.ITheme;
import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * @author lazyman
 */
public class GizmoTheme implements ITheme {

    @Override
    public String name() {
        return "Gizmo";
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(
                new PackageResourceReference(GizmoTheme.class, "bootstrap.css")));

        response.render(CssHeaderItem.forReference(
                new WebjarsCssResourceReference("/bootstrap-switch/current/css/bootstrap3/bootstrap-switch.css")));

        response.render(JavaScriptHeaderItem.forReference(
                new WebjarsJavaScriptResourceReference("/bootstrap-switch/current/js/bootstrap-switch.js")));
    }

    @Override
    public Iterable<String> getCdnUrls() {
        return null;
    }
}
