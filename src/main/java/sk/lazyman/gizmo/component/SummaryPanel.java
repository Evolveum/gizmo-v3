package sk.lazyman.gizmo.component;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import sk.lazyman.gizmo.data.provider.SummaryDataProvider;
import sk.lazyman.gizmo.dto.SummaryPanelDto;
import sk.lazyman.gizmo.dto.WorkFilterDto;
import sk.lazyman.gizmo.dto.TaskLength;

/**
 * @author lazyman
 */
public class SummaryPanel extends SimplePanel<SummaryPanelDto> {

    private static final String ID_WEEK_REPEATER = "weekRepeater";
    private static final String ID_DAY_REPEATER = "dayRepeater";
    private static final String ID_LABEL = "label";

    public SummaryPanel(String id, final SummaryDataProvider provider, final IModel<WorkFilterDto> model) {
        super(id);

        setModel(new LoadableDetachableModel<SummaryPanelDto>() {

            @Override
            protected SummaryPanelDto load() {
                return provider.createSummary(model.getObject());
            }
        });

        setOutputMarkupId(true);
        initPanelLayout();
    }

    private void initPanelLayout() {
        Loop weekRepeater = new Loop(ID_WEEK_REPEATER, createWeekModel()) {

            @Override
            protected void populateItem(final LoopItem item) {
                Loop dayRepeater = new Loop(ID_DAY_REPEATER, 7) {

                    @Override
                    protected void populateItem(final LoopItem dayItem) {
                        final int index = item.getIndex() * 7 + dayItem.getIndex();

                        //todo handle item class attribute
                        Label label = new Label(ID_LABEL, createDayModel(index));
                        label.setRenderBodyOnly(true);

                        dayItem.add(label);

                        final SummaryPanelDto dto = getModelObject();
                        dayItem.add(AttributeModifier.append("class", new AbstractReadOnlyModel<String>() {

                            @Override
                            public String getObject() {
                                if (dto.isWeekend(index)) {
                                    return "success";
                                }

                                if (!dto.isWithinFilter(index)) {
                                    return "active";
                                }
                                return null;
                            }
                        }));
                    }
                };
                item.add(dayRepeater);
            }
        };
        add(weekRepeater);
    }

    private IModel<String> createDayModel(final int index) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                SummaryPanelDto dto = getModelObject();
                TaskLength length = dto.getTaskLength(index);
                if (length == null) {
                    length = new TaskLength(0, 0);
                }
                return StringUtils.join(new Object[]{length.getLength(), length.getInvoice()}, '/');
            }
        };
    }

    private IModel<Integer> createWeekModel() {
        return new AbstractReadOnlyModel<Integer>() {

            @Override
            public Integer getObject() {
                SummaryPanelDto dto = getModelObject();
                return dto.getWeekCount();
            }
        };
    }
}
