package sk.lazyman.gizmo.util;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import sk.lazyman.gizmo.dto.DashboardProjectDto;

import java.util.List;
import java.util.Locale;

/**
 * @author lazyman
 */
public class DashboardProjectConverter extends AbstractAutoCompleteTextRenderer<DashboardProjectDto>
        implements IConverter<DashboardProjectDto> {

    private IModel<List<DashboardProjectDto>> projects;

    public DashboardProjectConverter(IModel<List<DashboardProjectDto>> projects) {
        this.projects = projects;
    }

    @Override
    protected String getTextValue(DashboardProjectDto object) {
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

        return sb.toString();
    }

    @Override
    public DashboardProjectDto convertToObject(String value, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        }

        List<DashboardProjectDto> list = projects.getObject();
        for (DashboardProjectDto dto : list) {
            if (value.equals(convertToString(dto, locale))) {
                return dto;
            }
        }

        throw new ConversionException("DashboardProjectConverter.cantConvert").setSourceValue(value)
                .setTargetType(DashboardProjectDto.class)
                .setConverter(this)
                .setLocale(locale);
    }

    @Override
    public String convertToString(DashboardProjectDto value, Locale locale) {
        return getTextValue(value);
    }
}
