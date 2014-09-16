package sk.lazyman.gizmo.theme;

import de.agilecoders.wicket.core.settings.ITheme;
import de.agilecoders.wicket.core.settings.ThemeProvider;

import java.util.Arrays;
import java.util.List;

/**
 * @author lazyman
 */
public class GizmoThemeProvider implements ThemeProvider {

    private ITheme theme = new GizmoTheme();

    @Override
    public ITheme byName(String name) {
        return theme;
    }

    @Override
    public List<ITheme> available() {
        return Arrays.asList(new ITheme[]{theme});
    }

    @Override
    public ITheme defaultTheme() {
        return theme;
    }
}
