package com.evolveum.gizmo.web.app;

import com.evolveum.gizmo.component.*;
import com.evolveum.gizmo.data.LabelPart;
import com.evolveum.gizmo.data.Part;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.repository.PartRepository;
import com.evolveum.gizmo.util.LoadableModel;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.*;

public class PageLabel extends PageAppLabels{

    public static final String LABEL_ID = "labelId";
    private static final String ID_FORM = "form";

    private static final String ID_TABS = "tabs";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SAVE = "save";
    private final IModel<LabelPart> model;

    @SpringBean private PartRepository partRepository;
    private LabelBasicPanel basicsPanel;

    public PageLabel() {
        this(new PageParameters());
    }

    public PageLabel(PageParameters params) {
        super(params);

        model = new LoadableModel<>() {
            @Override
            protected LabelPart load() {
                return loadLabel();
            }
        };

        initLayout();
    }

    @Override
    protected IModel<String> createPageTitleModel() {
        return () -> {
            Integer id = getLabelId();
            String key = id != null ? "PageLabel.page.title.edit" : "PageLabel.page.title";
            return createStringResource(key, id != null ? "Edit label" : "Create label").getString();
        };
    }

    private LabelPart loadLabel() {
        Integer labelId = getLabelId();
        if (labelId == null) {
            return new LabelPart();
        }

        var repo = getLabelPartRepository();
        var label = repo.findById(labelId);
        if (label.isEmpty()) {
            getSession().error(translateString("Message.couldntFindLabel", labelId));
            throw new RestartResponseException(PageLabels.class);
        }
        return label.get();
    }

    private Integer getLabelId() {
        String s = getPageParameters().get(LABEL_ID).toOptionalString();
        if (s == null || s.isBlank()) return null;
        try { return Integer.valueOf(s); } catch (NumberFormatException e) { return null; }
    }

    private void initLayout() {
        Form<LabelPart> form = new Form<>(ID_FORM);
        form.setOutputMarkupId(true);
        add(form);

        initButtons(form);

        GizmoTabbedPanel<ITab> tabs = new GizmoTabbedPanel<>(ID_TABS, createTabs());
        form.add(tabs);
    }

    private List<ITab> createTabs() {
        List<ITab> tabList = new ArrayList<>();

        tabList.add(new AbstractTab(createStringResource("PageLabel.basics")) {
            @Override
            public WebMarkupContainer getPanel(String panelId) {
                basicsPanel = new LabelBasicPanel(panelId, model);
                return basicsPanel;
            }
        });

        return tabList;
    }

    private void initButtons(Form form) {
        AjaxSubmitButton save = new AjaxSubmitButton(ID_SAVE, createStringResource("GizmoApplication.button.save")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                savePerformed(target);
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
    private void cancelPerformed(AjaxRequestTarget target) {
        setResponsePage(PageLabels.class);
    }

    private void savePerformed(AjaxRequestTarget target) {
        var labelRepo = getLabelPartRepository();
        try {
            LabelPart label = labelRepo.saveAndFlush(model.getObject());
            model.setObject(label);

            var picks = basicsPanel != null
                    ? basicsPanel.getSelectedTriples()
                    : List.<CustomerProjectPartDto>of();

            Set<Integer> partIds = new LinkedHashSet<>();
            Set<Integer> projectIds = new LinkedHashSet<>();
            Set<Integer> customerIds = new LinkedHashSet<>();

            if (picks != null) {
                for (var t : picks) {
                    if (t == null) continue;
                    if (t.getPartId() != null) {
                        partIds.add(t.getPartId());
                    } else if (t.getProjectId() != null) {
                        projectIds.add(t.getProjectId());
                    } else if (t.getCustomerId() != null) {
                        customerIds.add(t.getCustomerId());
                    }
                }
            }

            if (!partIds.isEmpty()) {
                projectIds.clear();
                customerIds.clear();
            } else if (!projectIds.isEmpty()) {
                customerIds.clear();
            }

            List<Part> toUpdate = new ArrayList<>();
            if (!partIds.isEmpty()) {
                toUpdate.addAll(partRepository.findAllWithLabelsByIdIn(partIds));
            }
            if (!projectIds.isEmpty()) {
                toUpdate.addAll(partRepository.findAllWithLabelsByProjectIdIn(projectIds));
            }
            if (!customerIds.isEmpty()) {
                toUpdate.addAll(partRepository.findAllWithLabelsByCustomerIdIn(customerIds));
            }

            Map<Integer, Part> uniq = new LinkedHashMap<>();
            for (Part p : toUpdate) uniq.put(p.getId(), p);

            for (Part p : uniq.values()) {
                if (p.getLabels() == null) p.setLabels(new HashSet<>());
                p.getLabels().add(label);
            }
            if (!uniq.isEmpty()) {
                partRepository.saveAll(uniq.values());
            }


            Integer idParam = (label.getId() != null) ? Math.toIntExact(label.getId()) : null;
            PageLabels list = new PageLabels();
            list.success(getString("Message.labelSavedSuccessfully", null, "Label saved successfully."));
            setResponsePage(list);

        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveLabel", ex, target);
        }
    }
}
