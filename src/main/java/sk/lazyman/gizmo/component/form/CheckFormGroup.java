package sk.lazyman.gizmo.component.form;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import sk.lazyman.gizmo.component.SimplePanel;

/**
 * @author lazyman
 */
public class CheckFormGroup extends SimplePanel<Boolean> {

    private static final String ID_CHECK = "check";
    private static final String ID_CHECK_WRAPPER = "checkWrapper";
    private static final String ID_LABEL = "label";

    public CheckFormGroup(String id, IModel<Boolean> value, IModel<String> label, String labelSize, String textSize) {
        super(id, value);

        initLayout(label, labelSize, textSize);
    }

    private void initLayout(IModel<String> label, String labelSize, String textSize) {
        Label l = new Label(ID_LABEL, label);
        if (StringUtils.isNotEmpty(labelSize)) {
            l.add(AttributeAppender.prepend("class", labelSize));
        }
        add(l);

        WebMarkupContainer checkWrapper = new WebMarkupContainer(ID_CHECK_WRAPPER);
        if (StringUtils.isNotEmpty(textSize)) {
            checkWrapper.add(AttributeAppender.prepend("class", textSize));
        }
        add(checkWrapper);

        CheckBox check = new CheckBox(ID_CHECK, getModel());
        check.setLabel(label);
        checkWrapper.add(check);
    }

    public CheckBox getCheck(){
        return (CheckBox) get(ID_CHECK_WRAPPER + ":" + ID_CHECK);
    }
}
