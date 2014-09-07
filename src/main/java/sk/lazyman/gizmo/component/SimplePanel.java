package sk.lazyman.gizmo.component;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

/**
 * @author lazyman
 */
public class SimplePanel<T> extends Panel {

    private IModel<T> model;

    public SimplePanel(String id) {
        this(id, null);
    }

    public SimplePanel(String id, IModel<T> model) {
        super(id);
        this.model = model == null ? createModel() : model;

        initLayout();
    }

    public IModel<T> createModel() {
        return null;
    }

    public IModel<T> getModel() {
        return model;
    }

    public T getModelObject() {
        return model != null ? model.getObject() : null;
    }

    public String getString(String resourceKey, Object... objects) {
        return createStringResource(resourceKey, objects).getString();
    }

    public StringResourceModel createStringResource(String resourceKey, Object... objects) {
        return new StringResourceModel(resourceKey, this, null, resourceKey, objects);
    }

    public StringResourceModel createStringResource(Enum e) {
        return createStringResource(e, null);
    }

    public StringResourceModel createStringResource(Enum e, String prefix) {
        return createStringResource(e, prefix, null);
    }

    public StringResourceModel createStringResource(Enum e, String prefix, String nullKey) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(prefix)) {
            sb.append(prefix).append('.');
        }

        if (e == null) {
            if (StringUtils.isNotEmpty(nullKey)) {
                sb.append(nullKey);
            } else {
                sb = new StringBuilder();
            }
        } else {
            sb.append(e.getDeclaringClass().getSimpleName()).append('.');
            sb.append(e.name());
        }

        return createStringResource(sb.toString());
    }

    protected String createComponentPath(String... components) {
        return StringUtils.join(components, ":");
    }

    protected void initLayout() {

    }
}
