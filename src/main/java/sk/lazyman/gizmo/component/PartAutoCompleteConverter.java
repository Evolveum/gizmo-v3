package sk.lazyman.gizmo.component;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import sk.lazyman.gizmo.dto.CustomerProjectPartDto;

import java.util.List;
import java.util.Locale;

/**
 * @author lazyman
 */
public class PartAutoCompleteConverter extends AbstractAutoCompleteTextRenderer<CustomerProjectPartDto>
        implements IConverter<CustomerProjectPartDto> {

    private IModel<List<CustomerProjectPartDto>> projects;

    public PartAutoCompleteConverter(IModel<List<CustomerProjectPartDto>> projects) {
        this.projects = projects;
    }

    @Override
    protected String getTextValue(CustomerProjectPartDto object) {
        if (object == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(object.getCustomerName())) {
            sb.append(object.getCustomerName());
        }

        if (StringUtils.isNotEmpty(object.getCustomerName())
                && StringUtils.isNotEmpty(object.getProjectName())) {
            sb.append(" / ");
        }

        if (StringUtils.isNotEmpty(object.getProjectName())) {
            sb.append(object.getProjectName());
        }

        if (StringUtils.isNotEmpty(object.getProjectName())
                && StringUtils.isNotEmpty(object.getPartName())) {
            sb.append(" / ");
        }

        if (StringUtils.isNotEmpty(object.getPartName())) {
            sb.append(object.getPartName());
        }

        return sb.toString();
    }

    @Override
    public CustomerProjectPartDto convertToObject(String value, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        }

        List<CustomerProjectPartDto> list = projects.getObject();
        for (CustomerProjectPartDto dto : list) {
            if (value.equals(convertToString(dto, locale))) {
                return dto;
            }
        }

        throw new ConversionException("DashboardProjectConverter.cantConvert").setSourceValue(value)
                .setTargetType(CustomerProjectPartDto.class)
                .setConverter(this)
                .setLocale(locale);
    }

    @Override
    public String convertToString(CustomerProjectPartDto value, Locale locale) {
        return getTextValue(value);
    }
}
