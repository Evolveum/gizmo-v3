package sk.lazyman.gizmo.component;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;

/**
 * @author lazyman
 */
public class CheckBox extends org.apache.wicket.markup.html.form.CheckBox {

    public CheckBox(String id, IModel<Boolean> model) {
        super(id, model);

        setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

//        response.render(OnDomReadyHeaderItem.forScript("$('#" + getMarkupId() + "').bootstrapSwitch();"));
    }
}
