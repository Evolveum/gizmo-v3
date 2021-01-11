package sk.lazyman.gizmo.component.form;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import sk.lazyman.gizmo.component.SimplePanel;
import sk.lazyman.gizmo.data.User;

import java.util.List;

public class MultiselectDropDownInput<T> extends ListMultipleChoice<T> {

    private static final String ID_INPUT = "input";

    private IModel<List<User>> choices;

    public MultiselectDropDownInput(String id, IModel<List<T>> model, IModel<List<T>> choices, IChoiceRenderer<T> renderer) {
        super(id, model, choices, renderer);
        setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        StringBuilder componentSb = new StringBuilder("$('#").append(getMarkupId()).append("')");
        String componentSelector = componentSb.toString();

        StringBuilder sb = new StringBuilder();
        sb.append("if (typeof ");
        sb.append(componentSelector);
        sb.append(".select2 === \"function\") {\n");

        sb.append(componentSelector);
        sb.append(".select2({").append("multiple: true").append("});");
        sb.append("}");
        response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
    }



}