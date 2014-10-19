package sk.lazyman.gizmo.component;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameModifier;
import de.agilecoders.wicket.less.LessResourceReference;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import sk.lazyman.gizmo.data.provider.SummaryDataProvider;
import sk.lazyman.gizmo.dto.SummaryPanelDto;
import sk.lazyman.gizmo.dto.TaskLength;
import sk.lazyman.gizmo.dto.WorkFilterDto;

/**
 * @author lazyman
 */
public class SummaryPanel extends SimplePanel<SummaryPanelDto> {

    private static final String ID_MONTH_REPEATER = "monthRepeater";
    private static final String ID_DAY_REPEATER = "dayRepeater";
    private static final String ID_LABEL = "label";

    private static final int TABLE_DAY_SIZE = 32;

    public SummaryPanel(String id, final SummaryDataProvider provider, final IModel<WorkFilterDto> model) {
        super(id);

        add(new CssClassNameModifier("table-responsive"));

        setModel(new LoadableDetachableModel<SummaryPanelDto>() {

            @Override
            protected SummaryPanelDto load() {
                return provider.createSummary(model.getObject());
            }
        });

        setOutputMarkupId(true);
        initPanelLayout();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new LessResourceReference(SummaryPanel.class, "SummaryPanel.less")));
    }

    private void initPanelLayout() {
        Loop monthRepeater = new Loop(ID_MONTH_REPEATER,
                new PropertyModel<Integer>(getModel(), SummaryPanelDto.F_MONTH_COUNT)) {

            @Override
            protected void populateItem(final LoopItem item) {
                Loop dayRepeater = new Loop(ID_DAY_REPEATER, TABLE_DAY_SIZE) {

                    @Override
                    protected void populateItem(final LoopItem dayItem) {
                        final int dayIndex = dayItem.getIndex() - 1;

                        Label label = new Label(ID_LABEL, createDayModel(item.getIndex(), dayIndex));
                        label.setRenderBodyOnly(true);
                        dayItem.add(label);

                        final SummaryPanelDto dto = getModelObject();
                        dayItem.add(new CssClassNameModifier(new AbstractReadOnlyModel<String>() {

                            @Override
                            public String getObject() {
                                // month year column
                                if (dayIndex == -1) {
                                    return null;
                                }

                                if (dto.isToday(item.getIndex(), dayIndex)) {
                                    return "info";
                                }

                                if (!dto.isWithinFilter(item.getIndex(), dayIndex)) {
                                    return "active";
                                }

                                if (dto.isWeekend(item.getIndex(), dayIndex)) {
                                    return "success";
                                }

                                if (!dto.isFullDayDone(item.getIndex(), dayIndex)) {
                                    return "danger";
                                }

                                return null;
                            }
                        }));
                    }
                };
                item.add(dayRepeater);
            }
        };
        add(monthRepeater);
    }

    private IModel<String> createDayModel(final int monthIndex, final int dayIndex) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                SummaryPanelDto dto = getModelObject();

                if (dayIndex == -1) {
                    return dto.getMonthYear(monthIndex);
                }

                if (dto.getDayForIndex(monthIndex, dayIndex) == null) {
                    //not "existing" date like 31. september
                    return null;
                }

                if (!dto.isWithinFilter(monthIndex, dayIndex)) {
                    return null;
                }

                TaskLength length = dto.getTaskLength(monthIndex, dayIndex);
                if (length == null) {
                    length = new TaskLength(0, 0);
                }
                return StringUtils.join(new Object[]{length.getLength(), length.getInvoice()}, '/');
            }
        };
    }
}
