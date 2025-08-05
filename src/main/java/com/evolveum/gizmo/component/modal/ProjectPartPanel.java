/*
 *  Copyright (C) 2023 Evolveum
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.evolveum.gizmo.component.modal;

import com.evolveum.gizmo.component.SimplePanel;
import com.evolveum.gizmo.component.form.AreaFormGroup;
import com.evolveum.gizmo.component.form.FormGroup;
import com.evolveum.gizmo.component.form.IconButton;
import com.evolveum.gizmo.data.Part;
import com.evolveum.gizmo.dto.WorkDto;
import com.evolveum.gizmo.util.ColorUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * @author lazyman
 */
public class ProjectPartPanel extends SimplePanel<Part> {
//        extends ModalDialog {
//
    private static final String ID_LABEL = "label";
    private static final String ID_NAME = "name";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_COLOR = "color";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SAVE = "save";
    private static final String ID_PROJECT = "project";
    private static final String ID_FORM = "form";

//    private IModel<Part> partModel;

//    public ProjectPartPanel(String id, ) {
//        this(id, new Model<>(new Part()));
//    }

    public ProjectPartPanel(String id, IModel<Part> partModel) {
        super(id, partModel);
        Part part = getModel().getObject();
        if (part.getColor() == null || part.getColor().isBlank()) {
            part.setColor(ColorUtils.getRandomFromPalette());
        }
//        this.partModel = partModel;

//        header(createTitle());

    }


    private IModel<String> createTitle() {
        return () -> {
                Part part = getModelObject();

                String key = part.getId() != null ? "ProjectPartModal.edit" : "ProjectPartModal.new";
                return createStringResource(key).getObject();
        };
    }

    protected void initLayout() {

        Form<Part> form = new Form<>(ID_FORM);
        add(form);

        TextField<String> name = new TextField<>(ID_NAME, new PropertyModel<>(getModel(), Part.F_NAME));
        form.add(name);

        TextArea<String> description = new TextArea<>(ID_DESCRIPTION, new PropertyModel<>(getModel(), Part.F_DESCRIPTION));
        form.add(description);

        TextField<String> color = new TextField<>(ID_COLOR, new PropertyModel<>(getModel(), Part.F_COLOR)) {
            @Override
            protected void onComponentTag(org.apache.wicket.markup.ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("type", "color");
            }
        };
        form.add(color);


        IconButton cancel = new IconButton(ID_CANCEL,
                createStringResource("GizmoApplication.button.cancel"),
                createStringResource("fa fa-times"),
                createStringResource("btn-default")) {

            @Override
            public void submitPerformed(AjaxRequestTarget target) {
                cancelPerformed(target);
            }
        };

        form.add(cancel);

        IconButton save = new IconButton(ID_SAVE,
                createStringResource("GizmoApplication.button.save"),
                createStringResource("fa fa-times"),
                createStringResource("btn-default")) {

            @Override
            public void submitPerformed(AjaxRequestTarget target) {
                savePerformed(target, ProjectPartPanel.this.getModel());
            }
        };

        form.add(save);

//        AjaxSubmitButton save = new AjaxSubmitButton(ID_SA,
//                createStringResource("GizmoApplication.button.save"), Buttons.Type.Primary) {
//
//            @Override
//            protected void onSubmit(AjaxRequestTarget target) {
//                savePerformed(target, partModel);
//            }
//
//            @Override
//            protected void onError(AjaxRequestTarget target) {
//                target.add(name, description);
//            }
//        };
//        addButton(save);
    }

//    public Part getPart() {
//        return partModel.getObject();
//    }

//    public IModel<Part> getPartModel() {
//        return partModel;
//    }
//
//    public void setPart(Part part) {
//        this.partModel.setObject(part);
//    }

//    private IModel<String> createStringResource(String key) {
//        return new StringResourceModel(key, this, null);
//    }

    protected void cancelPerformed(AjaxRequestTarget target) {
//        close(target);
    }

    protected void savePerformed(AjaxRequestTarget target, IModel<Part> model) {
//        close(target);
    }
}
