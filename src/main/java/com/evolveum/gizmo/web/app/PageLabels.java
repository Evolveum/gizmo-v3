package com.evolveum.gizmo.web.app;

import com.evolveum.gizmo.component.EnterIconButton;
import com.evolveum.gizmo.component.data.LinkIconColumn;
import com.evolveum.gizmo.component.data.TablePanel;
import com.evolveum.gizmo.data.LabelPart;
import com.evolveum.gizmo.data.Part;
import com.evolveum.gizmo.data.QLabelPart;
import com.evolveum.gizmo.data.provider.BasicDataProvider;
import com.evolveum.gizmo.repository.PartRepository;
import com.querydsl.core.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class PageLabels extends PageAppTemplate {

    private static final String ID_TABLE = "table";
    private static final String ID_FORM = "form";
    private static final String ID_NEW_LABEL = "newLabel";
    private static final String ID_SEARCH = "search";
    private static final String ID_SEARCH_TEXT = "searchText";


    @SpringBean
    private PartRepository partRepository;

    private IModel<String> searchModel = new Model<>();

    public PageLabels() {
        initLayout();
    }

    private void initLayout(){
        Form form = new Form(ID_FORM);
        add(form);

        TextField searchText = new TextField(ID_SEARCH_TEXT, searchModel);
        searchText.setOutputMarkupId(true);
        form.add(searchText);

        initButtons(form);

        BasicDataProvider provider = new BasicDataProvider(getLabelPartRepository()) {
            @Override public Predicate getPredicate() {
                String q = StringUtils.trimToNull(searchModel.getObject());
                if (q == null) return null;
                QLabelPart l = QLabelPart.labelPart;
                return l.name.containsIgnoreCase(q)
                        .or(l.code.containsIgnoreCase(q));
            }
        };

        provider.setSort(Sort.by(Sort.Order.asc(LabelPart.F_NAME)));

        List<IColumn> columns = new ArrayList<>();
        columns.add(new PropertyColumn(createStringResource("LabelPart.name"), LabelPart.F_NAME));
        columns.add(new PropertyColumn(createStringResource("LabelPart.code"), LabelPart.F_CODE));
        columns.add(new AbstractColumn<LabelPart, String>(createStringResource("LabelPart.customerProjectPart")) {
            @Override
            public void populateItem(Item<ICellPopulator<LabelPart>> cellItem,
                                     String componentId, IModel<LabelPart> rowModel) {
                LabelPart label = rowModel.getObject();

                List<String> rows = (label.getId() == null)
                        ? java.util.List.of()
                        : partRepository.findCustomerProjectPartStringsByLabelId(label.getId());

                String text = rows.isEmpty() ? "-" : String.join("<br/>", rows);

                Label lbl = new Label(componentId, text);
                lbl.setEscapeModelStrings(false);
                cellItem.add(lbl);
            }
        });
        columns.add(new LinkIconColumn<LabelPart>(Model.of("")) {

            @Override
            protected IModel<String> createIconModel(IModel<LabelPart> rowModel) {
                return Model.of("fa fa-trash text-danger");
            }

            @Override
            protected IModel<String> createTitleModel(IModel<LabelPart> rowModel) {
                return PageLabels.this.createStringResource("PageLabels.remove");
            }

            @Override
            protected void onClickPerformed(AjaxRequestTarget target,
                                            IModel<LabelPart> rowModel,
                                            AjaxLink<?> link) {
                removeLabelPerformed(target, rowModel.getObject());
            }
        });

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 20);
        table.setOutputMarkupId(true);
        add(table);

    }

    private void initButtons(Form<?> form) {
        EnterIconButton search = new EnterIconButton(ID_SEARCH,
                createStringResource("PageLabels.search"),
                createStringResource("fa-search"),
                createStringResource("btn-primary")) {
            @Override
            protected void submitPerformed(AjaxRequestTarget target) {
                searchPerformed(target);
            }
        };
        form.add(search);
    }

    private void searchPerformed(AjaxRequestTarget target) {
        target.add(get(ID_TABLE));
    }

    @Override
    public Fragment createHeaderButtonsFragment(String fragmentId) {
        Fragment fragment = new  Fragment(fragmentId, "buttonsFragment", this);

        AjaxLink<String> newLabel = new AjaxLink<>(ID_NEW_LABEL, createStringResource("PageLabels.newLabel")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                newLabelPerformed(target);
            }
        };
        newLabel.setOutputMarkupId(true);
        fragment.add(newLabel);

        return fragment;
    }

    private void newLabelPerformed(AjaxRequestTarget target) {
        setResponsePage(PageLabel.class);
    }

    private void removeLabelPerformed(AjaxRequestTarget target, LabelPart label) {
        try {
            Long labelId = label.getId();
            List<Part> parts = partRepository.findAllWithLabelsByLabelId(labelId);

            for (Part p : parts) {
                if (p.getLabels() != null) {
                    p.getLabels().removeIf(l -> l != null && labelId.equals(l.getId()));
                }
            }
            if (!parts.isEmpty()) {
                partRepository.saveAll(parts);
            }

            getLabelPartRepository().deleteById(Math.toIntExact(labelId));

            success(createStringResource("Message.successfullyDeleted").getString());
            target.add(getFeedbackPanel(), get(ID_TABLE));

        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntDeleteLabel", ex, target);
        }
    }

}
