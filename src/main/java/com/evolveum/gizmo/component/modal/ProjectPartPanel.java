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
import com.evolveum.gizmo.component.SimplePanel;
import com.evolveum.gizmo.component.form.GizmoForm;
import com.evolveum.gizmo.component.form.IconButton;
import com.evolveum.gizmo.component.form.MultiselectDropDownInput;
import com.evolveum.gizmo.data.Part;
import com.evolveum.gizmo.util.ColorUtils;
import com.evolveum.gizmo.util.LabelService;
import com.evolveum.gizmo.data.LabelPart;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lazyman
 */
public class ProjectPartPanel extends SimplePanel<Part> {

    private static final String ID_NAME = "name";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_COLOR = "color";
    private static final String ID_LABELS = "labels";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SAVE = "save";
    private static final String ID_FORM = "form";

    @SpringBean
    private LabelService labelService;

    public ProjectPartPanel(String id, IModel<Part> partModel) {
        super(id, partModel);
        Part part = getModel().getObject();
        if (part.getColor() == null || part.getColor().isBlank()) {
            part.setColor(ColorUtils.getRandomFromPalette());
        }
    }


    protected void initLayout() {

        GizmoForm<Part> form = new GizmoForm<>(ID_FORM);
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


        IChoiceRenderer<LabelPart> labelRenderer = new IChoiceRenderer<>() {
            @Override public Object getDisplayValue(LabelPart l) { return l.getCode() + " — " + l.getName(); }
            @Override public String getIdValue(LabelPart l, int index) { return String.valueOf(l.getId()); }
            @Override public LabelPart getObject(String id, IModel<? extends List<? extends LabelPart>> choices) {
                Long lid = Long.valueOf(id);
                for (LabelPart lp : choices.getObject()) if (lp != null && lid.equals(lp.getId())) return lp;
                return null;
            }
        };

        LoadableDetachableModel<List<LabelPart>> labelsChoices = new LoadableDetachableModel<>() {
            @Override protected List<LabelPart> load() {
                return labelService != null ? labelService.findAllOrdered() : java.util.Collections.emptyList();
            }
        };

        IModel<List<LabelPart>> labelsSelectionModel = new IModel<>() {
            @Override public List<LabelPart> getObject() {
                Part p = ProjectPartPanel.this.getModelObject();
                if (p == null) return java.util.Collections.emptyList();
                return new ArrayList<>(p.getLabels());
            }
            @Override public void setObject(List<LabelPart> value) {
                Part p = ProjectPartPanel.this.getModelObject();
                if (p == null) return;
                p.getLabels().clear();
                if (value != null) p.getLabels().addAll(value);
            }
            @Override public void detach() {}
        };

        MultiselectDropDownInput<LabelPart> labelsField = new MultiselectDropDownInput<>(
                ID_LABELS,
                labelsSelectionModel,
                labelsChoices,
                labelRenderer
        );
        labelsField.setOutputMarkupId(true);
        form.add(labelsField);

        AjaxSubmitButton save = new AjaxSubmitButton(ID_SAVE, createStringResource("GizmoApplication.button.save")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                savePerformed(target, ProjectPartPanel.this.getModel());
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(form);
            }
        };
        form.add(save);

        AjaxButton cancel = new AjaxButton(ID_CANCEL, createStringResource("GizmoApplication.button.cancel")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                cancelPerformed(target);
            }
        };
        form.add(cancel);
    }

    protected void cancelPerformed(AjaxRequestTarget target) {
        //nothing by default, close modal where implemented.
   }

    protected void savePerformed(AjaxRequestTarget target, IModel<Part> model) {
//        close(target);
    }
}
