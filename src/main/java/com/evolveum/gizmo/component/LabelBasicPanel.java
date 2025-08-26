package com.evolveum.gizmo.component;

import com.evolveum.gizmo.component.form.CustomerProjectPartSearchPanel;
import com.evolveum.gizmo.component.form.FormGroup;
import com.evolveum.gizmo.data.LabelPart;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.ProjectSearchSettings;
import com.evolveum.gizmo.repository.PartRepository;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import java.util.List;

public class LabelBasicPanel extends SimplePanel<LabelPart>{
    private static final String ID_NAME = "name";
    private static final String ID_CODE = "code";
    private static final String ID_CPP = "cpp";

    @SpringBean private PartRepository partRepository;

    private final IModel<ProjectSearchSettings> cppModel =
            org.apache.wicket.model.Model.of(new ProjectSearchSettings());

    public LabelBasicPanel(String id, IModel<LabelPart> model) {
        super(id, model);
    }

    protected void initLayout() {
        FormGroup name = new FormGroup(ID_NAME, new PropertyModel<String>(getModel(), LabelPart.F_NAME),
                createStringResource("LabelPart.name"), true);
        add(name);

        FormGroup code = new FormGroup(ID_CODE, new PropertyModel<String>(getModel(), LabelPart.F_CODE),
                createStringResource("LabelPart.code"), true);
        add(code);

        CustomerProjectPartSearchPanel cpp = new CustomerProjectPartSearchPanel(ID_CPP, cppModel);
        cpp.setOutputMarkupId(true);
        add(cpp);
    }

    public List<CustomerProjectPartDto> getSelectedTriples() {
        var s = cppModel.getObject();
        return (s != null && s.getCustomer() != null) ? s.getCustomer() : List.of();
    }
}
