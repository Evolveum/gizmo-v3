package sk.lazyman.gizmo.component;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * @author lazyman
 */
public class ProjectPartsTab extends SimplePanel {

    private static final String ID_TABLE = "table";
    private static final String ID_NEW_PART = "newPart";

    public ProjectPartsTab(String id) {
        super(id);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        initPanelLayout();
    }

    private void initPanelLayout() {
        AjaxButton newProject = new AjaxButton(ID_NEW_PART, createStringResource("ProjectPartsTab.newPart")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                newPartPerformed(target);
            }
        };
        add(newProject);
    }

    protected void newPartPerformed(AjaxRequestTarget target) {

    }
}
