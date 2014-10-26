package sk.lazyman.gizmo.component;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import sk.lazyman.gizmo.dto.CustomerProjectPartDto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author lazyman
 */
public class PartAutoCompleteText extends AutoCompleteTextField<CustomerProjectPartDto> {

    private IModel<List<CustomerProjectPartDto>> allChoices;

    public PartAutoCompleteText(String id, IModel<CustomerProjectPartDto> model, IModel<List<CustomerProjectPartDto>> allChoices) {
        super(id, model, CustomerProjectPartDto.class, new PartAutoCompleteConverter(allChoices), new AutoCompleteSettings());

        this.allChoices = allChoices;
    }

    @Override
    protected Iterator<CustomerProjectPartDto> getChoices(String input) {
        List<CustomerProjectPartDto> list = allChoices.getObject();
        List<CustomerProjectPartDto> result = new ArrayList<>();
        for (CustomerProjectPartDto dto : list) {
            if (dto.match(input)) {
                result.add(dto);
            }
        }

        return result.iterator();
    }

    @Override
    public <C> IConverter<C> getConverter(Class<C> type) {
        return (IConverter<C>) new PartAutoCompleteConverter(allChoices);
    }
}
