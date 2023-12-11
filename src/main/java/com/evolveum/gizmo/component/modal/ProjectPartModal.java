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

import com.evolveum.gizmo.component.AjaxButton;
import com.evolveum.gizmo.component.AjaxSubmitButton;
import com.evolveum.gizmo.component.form.AreaFormGroup;
import com.evolveum.gizmo.component.form.FormGroup;
import com.evolveum.gizmo.data.Part;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalDialog;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * @author lazyman
 */
public class ProjectPartModal {
//        extends ModalDialog {
//
//    private static final String ID_LABEL = "label";
//    private static final String ID_NAME = "name";
//    private static final String ID_DESCRIPTION = "description";
//    private static final String ID_PROJECT = "project";
//
//    private IModel<Part> partModel;
//
//    public ProjectPartModal(String id) {
//        this(id, new Model<>(new Part()));
//    }
//
//    public ProjectPartModal(String id, IModel<Part> partModel) {
//        super(id);
//
//        this.partModel = partModel;
//
//        header(createTitle());
//        initLayout();
//    }
//
//    private IModel<String> createTitle() {
//        return () -> {
//                Part part = partModel.getObject();
//
//                String key = part.getId() != null ? "ProjectPartModal.edit" : "ProjectPartModal.new";
//                return createStringResource(key).getObject();
//        };
//    }
//
//    private void initLayout() {
//        final FormGroup name = new FormGroup(ID_NAME,
//                new PropertyModel<String>(partModel, Part.F_NAME),
//                createStringResource("Part.name"), true);
//        name.setOutputMarkupId(true);
//        add(name);
//
//        final FormGroup description = new AreaFormGroup(ID_DESCRIPTION,
//                new PropertyModel<String>(partModel, Part.F_DESCRIPTION),
//                createStringResource("Part.description"), false);
//        description.setOutputMarkupId(true);
//        add(description);
//
//        AjaxSubmitButton cancel = new AjaxSubmitButton(BUTTON_MARKUP_ID,
//                createStringResource("GizmoApplication.button.cancel")) {
//
//            @Override
//            protected void onSubmit(AjaxRequestTarget target) {
//                cancelPerformed(target);
//            }
//
//            @Override
//            protected void onError(AjaxRequestTarget target) {
//                cancelPerformed(target);
//            }
//        };
//        addButton(cancel);
//
//        AjaxSubmitButton save = new AjaxSubmitButton(BUTTON_MARKUP_ID,
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
//    }
//
//    public Part getPart() {
//        return partModel.getObject();
//    }
//
//    public IModel<Part> getPartModel() {
//        return partModel;
//    }
//
//    public void setPart(Part part) {
//        this.partModel.setObject(part);
//    }
//
//    private IModel<String> createStringResource(String key) {
//        return new StringResourceModel(key, this, null);
//    }
//
//    protected void cancelPerformed(AjaxRequestTarget target) {
//        close(target);
//    }
//
//    protected void savePerformed(AjaxRequestTarget target, IModel<Part> model) {
//        close(target);
//    }
}
