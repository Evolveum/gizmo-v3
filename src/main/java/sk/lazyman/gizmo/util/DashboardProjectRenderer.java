package sk.lazyman.gizmo.util;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import sk.lazyman.gizmo.dto.DashboardProjectDto;

/**
 * @author lazyman
 */
public class DashboardProjectRenderer extends AbstractAutoCompleteTextRenderer<DashboardProjectDto> {

    @Override
    protected String getTextValue(DashboardProjectDto object) {

//        return DashboardProjectDto.getTextValue(object);
        return null;
    }
}
