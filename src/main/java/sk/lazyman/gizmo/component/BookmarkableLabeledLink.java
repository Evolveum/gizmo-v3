package sk.lazyman.gizmo.component;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.model.IModel;

/**
 * @author lazyman
 */
public class BookmarkableLabeledLink extends BookmarkablePageLink {

    private IModel<String> model;

    public BookmarkableLabeledLink(String id, IModel<String> model, Class pageClass) {
        super(id, pageClass);
        this.model = model;
    }

    @Override
    public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
        String text = model.getObject();
        if (StringUtils.isNotEmpty(text)) {
            replaceComponentTagBody(markupStream, openTag, text);
            return;
        }

        super.onComponentTagBody(markupStream, openTag);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        if (tag.isOpenClose()) {
            tag.setType(XmlTag.TagType.OPEN);
        }
    }
}
