package com.evolveum.gizmo.web.app;

import com.evolveum.gizmo.component.data.TablePanel;
import com.evolveum.gizmo.data.provider.BasicDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import com.evolveum.gizmo.data.LabelPart;
import com.querydsl.core.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.springframework.data.domain.Sort;
import com.evolveum.gizmo.data.QLabelPart;
import javax.persistence.Table;
import java.lang.classfile.Label;
import java.util.ArrayList;
import java.util.List;

public class PageLabels extends PageAppTemplate{

    private static final String ID_TABLE = "table";
    private static final String ID_FORM = "form";
    private static final String ID_NEW_LABEL = "newLabel";
    private static final String ID_SEARCH = "search";
    private static final String ID_SEARCH_TEXT = "searchText";

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

        BasicDataProvider provider = new BasicDataProvider(getLabelPartRepository()) {
            @Override public Predicate getPredicate() {
                return null;
            }
        };
        provider.setSort(Sort.by(Sort.Order.asc(LabelPart.F_NAME)));

        List<IColumn> columns = new ArrayList<>();
        columns.add(new PropertyColumn(createStringResource("LabelPart.name"), LabelPart.F_NAME));
        columns.add(new PropertyColumn(createStringResource("LabelPart.code"), LabelPart.F_CODE));
        columns.add(new PropertyColumn(createStringResource("LabelPart.id"), LabelPart.F_ID));

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 20);
        table.setOutputMarkupId(true);
        add(table);

    }



}
